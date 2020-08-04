package com.scanlibrary;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import com.scanlibrary.models.Form;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    private View view;
    private ImageView scannedImageView;
    private Button doneButton;
    private Button addButton;
    private Bitmap original;
    private Bitmap transformed;
    private TextView pageNumber;
    private static ProgressDialogFragment progressDialogFragment;

    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);
        init();

        return view;
    }

    private void init() {
        showProgressDialog(getResources().getString(R.string.loading));
        Bitmap bitmap = getBitmap();
        bitmap = ConvertGray(bitmap);
        final Bitmap ORJ_bitmap = bitmap;
        scannedImageView = view.findViewById(R.id.scannedImage);
        setScannedImage(bitmap);
        doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new DoneButtonClickListener());
        addButton = (Button) view.findViewById(R.id.addBtn);
        addButton.setOnClickListener(new AddButtonClickListener());
        pageNumber = (TextView) view.findViewById(R.id.pageNumber);

        final File sd = Environment.getExternalStorageDirectory();
        final String stagingDirPath = view.getContext().getString(R.string.base_staging_path);
        final File stagingDir = new File(sd, stagingDirPath);
        if (stagingDir.listFiles() != null && stagingDir.listFiles().length > 0) {
            pageNumber.setText(String.valueOf(stagingDir.listFiles().length + 1));

        } else {
            pageNumber.setText("1");
        }

        addButton.setText("Tara");
        SkipMood = false;
        ScanConstants.Skip = false;
        Bitmap cutBitmap = ConvertGray(Bitmap.createBitmap(bitmap.getWidth() / 2, bitmap.getHeight() / 2, Bitmap.Config.ARGB_8888));
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Rect srcRect = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, srcRect, desRect, null);

        //setScannedImage(cutBitmap);
        Form selectedForm = ScanConstants.Selected_Form;
        Log.e("SONUÇ: ", selectedForm.getName());

        try {
            Log.e("BARCODE ARIYORUM: ", selectedForm.getHasBarcode());

            InputImage image = InputImage.fromBitmap(cutBitmap, 1);

            BarcodeScanner scanner = BarcodeScanning.getClient();

            Task<List<Barcode>> result = scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes.size() == 0) {
                                Warning();
                            } else {
                                Log.e("SONUC Google: ", barcodes.get(0).getDisplayValue());
                                foundedQR = barcodes.get(0).getDisplayValue();
                                String pageType = foundedQR.substring(0, 1);
                                if (pageType.equals("2")) {
                                    for (Barcode qr : barcodes) {
                                        Log.e("KOD: " + qr.getDisplayValue() + ", KONUM: ", String.valueOf(qr.getBoundingBox()));
                                    }
                                    CutSign(ORJ_bitmap);
                                }

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("SONUC Google: ", e.getMessage());
                            Warning();
                        }
                    });

            dismissDialog();

        } catch (Exception ex) {
            dismissDialog();
            Warning();
            Log.e("BARCODE ARAMIYORUM: ", selectedForm.getHasBarcode());
        }

        dismissDialog();
    }

    private void CutSign(final Bitmap bitmap) {

        InputImage image = InputImage.fromBitmap(bitmap, 1);
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_CODE_128)
                        .build();

        final BarcodeScanner scanner = BarcodeScanning.getClient(options);

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes.size() == 0) {
                            Warning();

                        } else {
                            Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                            for (Barcode qr : barcodes) {
                                Log.e("X: " + qr.getDisplayValue() + ", KONUM: ", String.valueOf(qr.getBoundingBox()));

                                Canvas canvas = new Canvas(tempBitmap);
                                Rect r = new Rect(qr.getBoundingBox().left, qr.getBoundingBox().top - qr.getBoundingBox().height(), qr.getBoundingBox().right, qr.getBoundingBox().bottom);

                                Paint p = new Paint();
                                p.setStyle(Paint.Style.STROKE);
                                p.setAntiAlias(true);
                                p.setFilterBitmap(true);
                                p.setDither(true);
                                p.setColor(Color.RED);
                                Paint p2 = new Paint();
                                p2.setStyle(Paint.Style.STROKE);
                                p2.setAntiAlias(true);
                                p2.setFilterBitmap(true);
                                p2.setDither(true);
                                p2.setColor(Color.GREEN);
                                r.set(r.left - r.top / 2, r.top, r.right + r.top / 2, r.bottom);
                                //  Bitmap cutBitmap = Bitmap.createBitmap(r.width() + 10, r.height() + 10, Bitmap.Config.ARGB_8888);
                                canvas.drawRect(r, p);

                               /*

                                //Bitmap cutBitmap = ConvertGray(Bitmap.createBitmap(bitmap.getWidth() / 2, bitmap.getHeight() / 2, Bitmap.Config.ARGB_8888));
                                Canvas canvas = new Canvas(tempBitmap);
                                //Rect desRect = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                                Rect srcRect = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
                                canvas.drawBitmap(tempBitmap, srcRect, r, p);*/

                                BitmapTransporter bt = new BitmapTransporter();
                                bt.QrValue = qr.getDisplayValue();
                                bt.B64Imza = getEncoded64ImageStringFromBitmap(tempBitmap);

                                ScanConstants.bitmapTransporterList.add(bt);
                            }
                            setScannedImage(tempBitmap);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SONUC Google: ", e.getMessage());
                        Warning();
                    }
                });
    }

    boolean SkipMood = false;

    private Bitmap cropTest(Rect rect, Bitmap originalBmp) {
        Log.e("SONUCLAR: ", rect.width() + "w-h " + rect.height() + " x/y" + rect.left + "-" + rect.top);
        Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, rect.left - 10, rect.top - 10, rect.width(), rect.height());
        return croppedBmp;
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    private void Warning() {
        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.appRed);
        pDialog.setTitleText("Uyarı");
        pDialog.setContentText("Barkod net değil.");
        pDialog.setConfirmText("Tamam");
        pDialog.setCancelable(false);
        pDialog.show();
        //getFragmentManager().popBackStack();
        addButton.setText("Tekrar Çek");
        ScanConstants.Skip = true;
    }

    private Bitmap ConvertGray(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    private Bitmap getBitmap() {

        Uri uri = getUri();
        try {
            original = Utils.getBitmap(getActivity(), uri);

            final File sd = Environment.getExternalStorageDirectory();
            File fdelete = new File(sd, uri.getPath());
            boolean isDeleted = fdelete.delete();

            //getActivity().getContentResolver().delete(uri, null, null);
            return original;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
        scannedImageView.setImageBitmap(scannedImage);
    }

    private class DoneButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent data = new Intent();
                        Bitmap bitmap = transformed;
                        if (bitmap == null) {
                            bitmap = original;
                        }
                        Uri uri = Utils.getUri(getActivity(), bitmap);
                        data.putExtra(ScanConstants.SCANNED_RESULT, uri);
                        data.putExtra(ScanConstants.SCAN_MORE, false);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        original.recycle();
                        System.gc();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                getActivity().finish();
                            }
                        });

                        dismissDialog();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class AddButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.loading));
            GoMultiPage();

        }
    }

    String foundedQR = "";

    private void GoMultiPage() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent data = new Intent();
                    Bitmap bitmap = transformed;
                    if (bitmap == null) {
                        bitmap = original;
                    }
                    Uri uri = Utils.getUri(getActivity(), bitmap);

                    data.putExtra(ScanConstants.SCANNED_QR, foundedQR);

                    data.putExtra(ScanConstants.SCANNED_RESULT, uri);

                    if (ScanConstants.Skip == false) {
                        BitmapTransporter bitmapTransporter = new BitmapTransporter();
                        bitmapTransporter.BitmapPath = uri;
                        bitmapTransporter.QrValue = foundedQR;

                        ScanConstants.bitmapTransporterList.add(bitmapTransporter);
                    }

                    data.putExtra(ScanConstants.SCAN_MORE, true);
                    getActivity().setResult(Activity.RESULT_OK, data);

                    original.recycle();
                    System.gc();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            getActivity().finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }
}