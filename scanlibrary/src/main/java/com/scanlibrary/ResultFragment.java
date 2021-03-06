package com.scanlibrary;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
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
import com.scanlibrary.models.Page;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    private View view;
    private ImageView scannedImageView;
    private ImageView IvSign;
    private Button doneButton;
    private Button addButton;
    private Bitmap original;
    private Bitmap transformed;
    private TextView pageNumber;
    private TextView LbQR;
    private static ProgressDialogFragment progressDialogFragment;
    private String LeftQR = "";
    private String RightQR = "11111111111";
    private String FormQR = "";
    /*SAĞ ÜST İÇİN
    *         Bitmap cutBitmap = ConvertGray(Bitmap.createBitmap(bitmap.getWidth() / 2, bitmap.getHeight() / 2, Bitmap.Config.ARGB_8888));
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Rect srcRect = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, srcRect, desRect, null);
    * */


    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);
        showProgressDialog(getResources().getString(R.string.loading));

        Bitmap takedPicture = InitComponents();
        Bitmap cropedPicture = CropRightTopPicture(takedPicture);
        DetectQR(cropedPicture);

        dismissDialog();
        return view;
    }

    private Bitmap InitComponents() {
        //Son resmi getir ve griye çevir
        Bitmap bitmap = getBitmap();
        bitmap = ConvertGray(bitmap);
        //Ekrandaki imageViewleri tanımlama ve resmi ekranda göster
        scannedImageView = view.findViewById(R.id.scannedImage);
        IvSign = view.findViewById(R.id.IvSign);
        setScannedImage(bitmap);
        //Ekrandaki butonları ekle ve ayarla
        doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new DoneButtonClickListener());
        addButton = (Button) view.findViewById(R.id.addBtn);
        addButton.setOnClickListener(new AddButtonClickListener());
        pageNumber = (TextView) view.findViewById(R.id.pageNumber);
        LbQR = (TextView) view.findViewById(R.id.LbQr);
        //Dosya sayısı ile sayfa sayısını hesapla
        final File sd = Environment.getExternalStorageDirectory();
        final String stagingDirPath = view.getContext().getString(R.string.base_staging_path);
        final File stagingDir = new File(sd, stagingDirPath);
        if (stagingDir.listFiles() != null && stagingDir.listFiles().length > 0) {
            pageNumber.setText(String.valueOf(stagingDir.listFiles().length + 1));
        } else {
            pageNumber.setText("1");
        }
        //Bir takım hesaplar
        addButton.setText("Tara");
        SkipMood = false;
        ScanConstants.Skip = false;
        return bitmap;
    }

    private Bitmap CropRightTopPicture(Bitmap takedPicture) {
        Bitmap cutBitmap = Bitmap.createBitmap(takedPicture.getWidth() / 2, takedPicture.getHeight() / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, takedPicture.getWidth() / 2, takedPicture.getHeight() / 2);
        Rect srcRect = new Rect(0, 0, takedPicture.getWidth(), takedPicture.getHeight() / 2);
        canvas.drawBitmap(takedPicture, srcRect, desRect, null);
        return cutBitmap;
    }

    private void DetectQR(Bitmap cropedPicture) {
        try {
            InputImage image = InputImage.fromBitmap(cropedPicture, 1);
            BarcodeScanner scanner = BarcodeScanning.getClient();
            Task<List<Barcode>> result = scanner.process(image).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                @Override
                public void onSuccess(List<Barcode> barcodes) {
                    if (barcodes.size() == 0) {
                        RightQR = "11111111111";
                        LeftQR = "NOT_FOUND";
                    } else {
                        for (Barcode barcode : barcodes) {
                            if (barcode.getDisplayValue().length() == 11) {
                                RightQR = barcode.getDisplayValue();
                                String isSign = RightQR.substring(0, 1);
                                LbQR.setText(RightQR);
                                if (isSign.equals("2")) {
                                    //Bu Sayfada İmza Aramam Lazım
                                }
                                continue;
                            }

                            if (barcode.getDisplayValue().length() == 14) {
                                LeftQR = barcode.getDisplayValue();
                                FormQR = LeftQR;
                                continue;
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Warning();
                }
            });
        } catch (Exception ex) {
            Warning();
        }
    }

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

                    data.putExtra(ScanConstants.SCANNED_QR, LeftQR);

                    data.putExtra(ScanConstants.SCANNED_RESULT, uri);

                    if (ScanConstants.Skip == false) {

                        if (FormQR.length() == 14) {
                            ScanConstants.ActiveForm.setFormBarcode(FormQR);
                            ScanConstants.ActiveForm.setFormNo(FormQR);
                        }

                        ScanConstants.ActiveForm.setIsControl(true);
                        ScanConstants.ActiveForm.setFormId(Long.valueOf(ScanConstants.Selected_Form.getId()));
                        Page page = new Page();
                        page.setBarcode(RightQR);
                        page.setImageBase64(getEncoded64ImageStringFromBitmap(ConvertGray(bitmap)));
                        ScanConstants.ActivePages.add(page);

                        /*BitmapTransporter bitmapTransporter = new BitmapTransporter();
                        bitmapTransporter.BitmapPath = uri;
                        bitmapTransporter.QrValue = RightQR;

                        ScanConstants.bitmapTransporterList.add(bitmapTransporter);*/
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

    private void CutSign(Bitmap bitmap) {

        Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        double defaultX = 350;
        double defaultY = 485;
        double defaultWidht = 793;
        double defaultHeight = 1140;
        double defaultSignHeight = 120;

        double difX = defaultWidht / tempBitmap.getWidth();
        double newX = (difX * defaultX) + defaultX;

        double difY = defaultHeight / tempBitmap.getHeight();
        double newY = (difY * defaultY) + defaultY;

        double newWidth = (tempBitmap.getWidth() - newX) - 10;
        double newHeight = (difY * defaultSignHeight) + defaultSignHeight;

        Bitmap newBitmap = Bitmap.createBitmap(tempBitmap, (int) newX, (int) newY, (int) newWidth, (int) newHeight);
        Log.e("KONUMLAR: ", (int) newX + "-" + (int) newY + "/" + (int) newWidth + "-" + (int) newHeight);
        BitmapTransporter bt = new BitmapTransporter();
        bt.QrValue = "IMZA";
        bt.B64Imza = getEncoded64ImageStringFromBitmap(newBitmap);
        IvSign.setImageBitmap(newBitmap);
        //ScanConstants.bitmapTransporterList.add(bt);
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

    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
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