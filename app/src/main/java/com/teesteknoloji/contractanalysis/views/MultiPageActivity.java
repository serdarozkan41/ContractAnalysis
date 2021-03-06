package com.teesteknoloji.contractanalysis.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.gson.Gson;

import com.pixplicity.easyprefs.library.Prefs;
import com.scanlibrary.ProgressDialogFragment;
import com.scanlibrary.models.Form;
import com.scanlibrary.models.Image;
import com.scanlibrary.models.SendFormRequestModel;
import com.teesteknoloji.contractanalysis.R;
import com.teesteknoloji.contractanalysis.utils.Constants;
import com.teesteknoloji.contractanalysis.utils.FileIOUtils;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MultiPageActivity extends AppCompatActivity {
    ProgressBar progressBar;
    CubeGrid doubleBounce;
    View progressOverlay;
    List<File> stagingFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_page);

        setTitle(getResources().getString(R.string.multi_page_title));
        LoadingBarInit();
        final String stagingDirPath = getApplicationContext().getString(R.string.base_staging_path);
        stagingFiles = FileIOUtils.getAllFiles(stagingDirPath);

        final GridView pagesGridView = (GridView) findViewById(R.id.multi_page_grid);
        final BaseAdapter gvAdapter = new ImageAdapterGridView(this);
        pagesGridView.setAdapter(gvAdapter);

        pagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) scanMore(view);
                else showImage(stagingFiles, position, view);
            }
        });


        setMargins(pagesGridView, 20, 20 + getStatusBarHeight(), 20, 20);


    }

    private void LoadingBarInit() {
        progressOverlay = findViewById(R.id.progress_overlay);
        progressBar = findViewById(R.id.PBar_Loading);
        doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private int getStatusBarHeight() {
        int height;
        Resources myResources = getResources();
        int idStatusBarHeight = myResources.getIdentifier("status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = getResources().getDimensionPixelSize(idStatusBarHeight);
        } else {
            height = 0;
        }
        return height;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Intent out = new Intent();
            out.putExtra(ScanConstants.SCANNED_RESULT, data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT));
            out.putExtra(ScanConstants.SCAN_MORE, data.getExtras().getBoolean(ScanConstants.SCAN_MORE));

            setResult(RESULT_OK, out);
            finish();

            System.gc();
        } catch (Exception ex) {
        }
    }

    public void saveNow(View view) {
        Constants.StartLoadingAnim(doubleBounce, progressOverlay);
        showProgressDialog(getResources().getString(com.scanlibrary.R.string.loading));
    }

    List<Image> qrList = new ArrayList<>();
    private static ProgressDialogFragment progressDialogFragment;

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    private void requestUploadSurvey() throws IOException {
        Constants.StartLoadingAnim(doubleBounce, progressOverlay);
        showProgressDialog(getResources().getString(com.scanlibrary.R.string.loading));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();


        Gson gson = new Gson();
        String json = gson.toJson(ScanConstants.ActiveReqModel);
        URL url = new URL(Constants.BASE_URL + "Form/SendForm");


        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json);
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "OkHttp Bot")
                .addHeader("Authorization", "Bearer " + Prefs.getString("Token", ""))
                .post(body)
                .build();


        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    okhttp3.Response response = httpClient.newCall(request).execute();
                    Log.e("HATAAA", String.valueOf(response.body()));
                    if (response.isSuccessful()) {
                        dismissDialog();

                        Intent out = new Intent();
                        out.putExtra(ScanConstants.SAVE_PDF, Boolean.TRUE);
                        setResult(RESULT_OK, out);
                        finish();
                        return response.body().string();
                    } else {
                        dismissDialog();

                        Intent out = new Intent();
                        out.putExtra(ScanConstants.SAVE_PDF, Boolean.TRUE);
                        setResult(RESULT_OK, out);
                        finish();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {

                }
            }
        };

        asyncTask.execute();
    }


    public void scanMore(View view) {


        int ActivePageCount = ScanConstants.ActivePages.size();
        int SelectedPageCount = Integer.parseInt(ScanConstants.Selected_Form.getPageCount()) ;

        if (ActivePageCount == SelectedPageCount) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            try {
                                ScanConstants.ActiveReqModel.setFormDetail(ScanConstants.Selected_Form);
                                ScanConstants.ActiveReqModel.setCampaingId(ScanConstants.Selected_Campaing.getId());
                                ScanConstants.ActiveReqModel.setProductId(ScanConstants.Selected_Product.getId());
                                ScanConstants.ActiveReqModel.setCNo(ScanConstants.CNo);
                                ScanConstants.ActiveForm.setPages(ScanConstants.ActivePages);
                                ScanConstants.ActivePages = new ArrayList<>();
                                ScanConstants.Forms.add(ScanConstants.ActiveForm);
                                ScanConstants.ActiveForm = new Form();
                                ScanConstants.ActiveReqModel.setForms(ScanConstants.Forms);
                                ScanConstants.IsFinish=true;
                                requestUploadSurvey();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            ScanConstants.ActiveReqModel.setFormDetail(ScanConstants.Selected_Form);
                            ScanConstants.ActiveReqModel.setCampaingId(ScanConstants.Selected_Campaing.getId());
                            ScanConstants.ActiveReqModel.setProductId(ScanConstants.Selected_Product.getId());
                            ScanConstants.ActiveReqModel.setCNo(ScanConstants.CNo);
                            ScanConstants.ActiveForm.setPages(ScanConstants.ActivePages);
                            ScanConstants.ActivePages = new ArrayList<>();
                            ScanConstants.Forms.add(ScanConstants.ActiveForm);
                            ScanConstants.ActiveForm = new Form();
                            ScanConstants.IsFinish=false;
                            Intent out = new Intent();
                            out.putExtra(ScanConstants.SAVE_PDF, Boolean.TRUE);
                            setResult(RESULT_OK, out);
                            finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Seçili form için sayfalar tamamlandı.").setPositiveButton("Gönder", dialogClickListener)
                    .setNegativeButton("Yeni Form Seç", dialogClickListener).show();
        } else {

            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle());
        }
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

    public void showImage(List<File> stagingFiles, int position, View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        String newFileName = stagingFiles.get(position - 1).getPath();
        File toOpen = new File(newFileName);

        Uri sharedFileUri = FileProvider.getUriForFile(view.getContext(), "com.teesteknoloji.contractanalysis.provider", toOpen);
        intent.setDataAndType(sharedFileUri, "image/png");
        PackageManager pm = view.getContext().getPackageManager();

        if (intent.resolveActivity(pm) != null) {
            view.getContext().startActivity(intent);
        }
    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;
        private GridView mGridView;
        private List<File> stagingFiles;

        public ImageAdapterGridView(Context c) {
            mContext = c;

            final String stagingDirPath = getApplicationContext().getString(R.string.base_staging_path);
            stagingFiles = FileIOUtils.getAllFiles(stagingDirPath);

            if (ScanConstants.Skip) {
                File fLast = stagingFiles.get(stagingFiles.size() - 1);
                fLast.delete();
                stagingFiles.remove(stagingFiles.size() - 1);
                ScanConstants.Skip = false;
            }
            /*for (File _file: stagingFiles) {

            }*/

            registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    stagingFiles = FileIOUtils.getAllFiles(stagingDirPath);
                }
            });
        }

        public int getCount() {
            return stagingFiles.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return Long.valueOf(position);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int width = (displayMetrics.widthPixels - 100) / 3;
            int height = (width / 3) * 4;

            if (position == 0) {

                View addMoreView = getLayoutInflater().inflate(R.layout.add_more_img, null);
                addMoreView.setLayoutParams(new GridView.LayoutParams(width, height));
                return addMoreView;

            } else {

                View eachFileView = getLayoutInflater().inflate(R.layout.each_file_img, null);
                eachFileView.setLayoutParams(new GridView.LayoutParams(width, height));

                ImageView deleteButton = eachFileView.findViewById(R.id.each_file_delete);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stagingFiles.get(stagingFiles.size() - position).delete();
                        notifyDataSetChanged();
                    }
                });

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;

                File imgFile = stagingFiles.get(stagingFiles.size() - position);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

                ImageView imageView = eachFileView.findViewById(R.id.each_file_screenshot);
                imageView.setImageBitmap(myBitmap);


                TextView textView = eachFileView.findViewById(R.id.each_pageno);
                textView.setText("Sayfa " + (stagingFiles.size() - position + 1));


                return eachFileView;
            }
        }
    }
}