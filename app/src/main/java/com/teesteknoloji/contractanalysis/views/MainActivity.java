package com.teesteknoloji.contractanalysis.views;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.developer.kalert.KAlertDialog;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import com.pixplicity.easyprefs.library.Prefs;
import com.scanlibrary.BitmapTransporter;
import com.scanlibrary.models.Form;
import com.scanlibrary.models.Image;
import com.scanlibrary.models.SendFormRequestModel;
import com.teesteknoloji.contractanalysis.MainApplication;
import com.teesteknoloji.contractanalysis.R;
import com.teesteknoloji.contractanalysis.models.Campaign;
import com.teesteknoloji.contractanalysis.models.MenuResponseModel;
import com.teesteknoloji.contractanalysis.models.Product;
import com.teesteknoloji.contractanalysis.models.SendResponseModel;
import com.teesteknoloji.contractanalysis.unuseds.SearchableActivity;
import com.teesteknoloji.contractanalysis.unuseds.SettingsActivity;
import com.teesteknoloji.contractanalysis.persistance.Document;
import com.teesteknoloji.contractanalysis.utils.Constants;
import com.teesteknoloji.contractanalysis.utils.DialogUtil;
import com.teesteknoloji.contractanalysis.utils.DialogUtilCallback;
import com.teesteknoloji.contractanalysis.fileView.FLAdapter;
import com.teesteknoloji.contractanalysis.utils.FileIOUtils;
import com.teesteknoloji.contractanalysis.utils.FileWritingCallback;
import com.teesteknoloji.contractanalysis.utils.PDFWriterUtil;
import com.teesteknoloji.contractanalysis.utils.PermissionUtil;
/*import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;*/
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FLAdapter fileAdapter;
    private final Context c = this;
    private List<Uri> scannedBitmaps = new ArrayList<>();
    ProgressBar progressBar;
    CubeGrid doubleBounce;
    View progressOverlay;
    private ArrayAdapter<Product> productAdapter;
    private ArrayAdapter<Campaign> campaingAdapter;
    private ArrayAdapter<Form> formTypeAdapter;

    LiveData<List<Document>> liveData;

    MaterialSpinner DbProducts;
    MaterialSpinner DbCampaings;
    MaterialSpinner DbFormTypes;
    EditText TbContractNumber;
    Button StartButton;
    private List<Product> ProductList;
    private List<Campaign> CampaingList;
    private List<Form> FormTypeList;
    Form selectedForm;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StartButton = findViewById(R.id.BuScan);
        StartButton.setEnabled(false);
        LoadingBarInit();

        PermissionUtil.ask(this);
        Constants.StartLoadingAnim(doubleBounce, progressOverlay);
        final String baseStorageDirectory = getApplicationContext().getString(R.string.base_storage_path);
        FileIOUtils.mkdir(baseStorageDirectory);

        final String baseStagingDirectory = getApplicationContext().getString(R.string.base_staging_path);
        FileIOUtils.mkdir(baseStagingDirectory);

        final String scanningTmpDirectory = getApplicationContext().getString(R.string.base_scantmp_path);
        FileIOUtils.mkdir(scanningTmpDirectory);
        InitData();
    }

    private void InitData() {
        try {


            long bId = Prefs.getLong("BayiId", 0);
            long uId = Prefs.getLong("UserId", 0);
            MainApplication.apiManager.GetMenus(bId, uId, new Callback<MenuResponseModel>() {
                @Override
                public void onResponse(Call<MenuResponseModel> call, Response<MenuResponseModel> response) {
                    if (response.isSuccessful()) {
                        MenuResponseModel responseModel = response.body();
                        ProductList = new ArrayList<>();
                        ProductList = responseModel.getProduct();
                        InitComponents();
                        Constants.StopLoadingAnim(doubleBounce, progressOverlay);

                    } else {
                        Warning(response.message());
                    }
                }

                @Override
                public void onFailure(Call<MenuResponseModel> call, Throwable t) {
                    Warning("Bir hata oluştu.");
                }
            });
        } catch (Exception ex) {
            Log.e("HATA", ex.getMessage());
        }
    }

    private void Warning(String message) {
        KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.appRed);
        pDialog.setTitleText("Uyarı");
        pDialog.setContentText(message);
        pDialog.setConfirmText("Tamam");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void LoadingBarInit() {
        progressOverlay = findViewById(R.id.progress_overlay);
        progressBar = findViewById(R.id.PBar_Loading);
        doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);
    }

    private void InitComponents() {
        DbProducts = findViewById(R.id.DbProducts);
        DbCampaings = findViewById(R.id.DbCampaings);
        DbFormTypes = findViewById(R.id.DbFormTypes);
        TbContractNumber = findViewById(R.id.TbContractNumber);
        DbCampaings.setEnabled(false);
        DbFormTypes.setEnabled(false);

        productAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_spinner_dropdown_item, ProductList);
        MaterialSpinnerAdapter<Product> productMaterialSpinnerAdapter = new MaterialSpinnerAdapter<Product>(this, ProductList);
        DbProducts.setAdapter(productMaterialSpinnerAdapter);

        DbProducts.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<Product>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Product item) {
                CampaingList = new ArrayList<>();
                CampaingList = item.getCampaign();
                campaingAdapter = new ArrayAdapter<Campaign>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, CampaingList);
                MaterialSpinnerAdapter<Campaign> campaignMaterialSpinnerAdapter = new MaterialSpinnerAdapter<Campaign>(MainActivity.this, CampaingList);
                DbCampaings.setAdapter(campaignMaterialSpinnerAdapter);
                DbCampaings.setEnabled(true);
                DbCampaings.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<Campaign>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Campaign item) {
                        FormTypeList = new ArrayList<>();
                        FormTypeList = item.getForm();
                        formTypeAdapter = new ArrayAdapter<Form>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, FormTypeList);
                        MaterialSpinnerAdapter<Form> formMaterialSpinnerAdapter = new MaterialSpinnerAdapter<Form>(MainActivity.this, FormTypeList);
                        DbFormTypes.setAdapter(formMaterialSpinnerAdapter);
                        DbFormTypes.setEnabled(true);
                        DbFormTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<Form>() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Form item) {
                                selectedForm = item;
                                StartButton.setEnabled(true);
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    public void goToSearch(MenuItem mi) {
        Intent intent = new Intent(this, SearchableActivity.class);
        startActivityForResult(intent, 0);
    }

    public void goToPreferences(MenuItem mi) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
    }

    public void openCamera(View v) {

        if (TbContractNumber.getText().length() > 0) {
            scannedBitmaps.clear();
            String stagingDirPath = getApplicationContext().getString(R.string.base_staging_path);
            String scanningTmpDirectory = getApplicationContext().getString(R.string.base_scantmp_path);

            FileIOUtils.clearDirectory(stagingDirPath);
            FileIOUtils.clearDirectory(scanningTmpDirectory);

            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
            ScanConstants.Selected_Form = selectedForm;
            ScanConstants.CNo = TbContractNumber.getText().toString();

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle());
        } else {
            KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.ERROR_TYPE);
            pDialog.getProgressHelper().setBarColor(R.color.appRed);
            pDialog.setTitleText("Uyarı");
            pDialog.setContentText("Sözleşme Numarası.");
            pDialog.setConfirmText("Tamam");
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    private void saveBitmap(final Bitmap bitmap, final boolean addMore) {

        final String baseDirectory = getApplicationContext().getString(addMore ? R.string.base_staging_path : R.string.base_storage_path);
        final File sd = Environment.getExternalStorageDirectory();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        final String timestamp = simpleDateFormat.format(new Date());

        if (addMore) {

            try {

                String filename = "SCANNED_STG_" + timestamp + ".png";

                FileIOUtils.writeFile(baseDirectory, filename, new FileWritingCallback() {
                    @Override
                    public void write(FileOutputStream out) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                });

                bitmap.recycle();
                System.gc();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } else {

            DialogUtil.askUserFilaname(c, null, null, new DialogUtilCallback() {

                @Override
                public void onSave(String textValue, String category) {

                    try {

                        final PDFWriterUtil pdfWriter = new PDFWriterUtil();

                        String stagingDirPath = getApplicationContext().getString(R.string.base_staging_path);

                        List<File> stagingFiles = FileIOUtils.getAllFiles(stagingDirPath);
                        for (File stagedFile : stagingFiles) {
                            pdfWriter.addFile(stagedFile);
                        }

                        pdfWriter.addBitmap(bitmap);

                        String filename = "SCANNED_" + timestamp + ".pdf";
                        FileIOUtils.writeFile(baseDirectory, filename, new FileWritingCallback() {
                            @Override
                            public void write(FileOutputStream out) {
                                try {
                                    pdfWriter.write(out);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        fileAdapter.notifyDataSetChanged();

                        FileIOUtils.clearDirectory(stagingDirPath);

                        SimpleDateFormat simpleDateFormatView = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        final String timestampView = simpleDateFormatView.format(new Date());

                        Document newDocument = new Document();
                        newDocument.setName(textValue);
                        newDocument.setCategory(category);
                        newDocument.setPath(filename);
                        newDocument.setScanned(timestampView);
                        newDocument.setPageCount(pdfWriter.getPageCount());
                        //viewModel.saveDocument(newDocument);

                        pdfWriter.close();

                        bitmap.recycle();
                        System.gc();

                    } catch (IOException ioe) {
                        ioe.printStackTrace();

                    }

                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ScanConstants.PICKFILE_REQUEST_CODE || requestCode == ScanConstants.START_CAMERA_REQUEST_CODE) &&
                resultCode == Activity.RESULT_OK) {

            Constants.StartLoadingAnim(doubleBounce, progressOverlay);

            boolean saveMode = data.getExtras().containsKey(ScanConstants.SAVE_PDF) ? data.getExtras().getBoolean(ScanConstants.SAVE_PDF) : Boolean.FALSE;
            if (saveMode) {
                List<BitmapTransporter> ll = ScanConstants.bitmapTransporterList;
                Log.e("SAVE MODE: ", String.valueOf(saveMode));
              /*  try {
                    requestUploadSurvey();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            } else {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                boolean doScanMore = data.getExtras().getBoolean(ScanConstants.SCAN_MORE);

                final File sd = Environment.getExternalStorageDirectory();
                File src = new File(sd, uri.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(src.getAbsolutePath());

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                saveBitmap(bitmap, doScanMore);

                if (doScanMore) {
                    scannedBitmaps.add(uri);
                    Intent intent = new Intent(this, MultiPageActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);

                    startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
                }

                //getContentResolver().delete(uri, null, null);
            }

            Constants.StopLoadingAnim(doubleBounce, progressOverlay);
        }
    }

    List<Image> qrList = new ArrayList<>();

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }
    private final OkHttpClient httpClient = new OkHttpClient();

    private void requestUploadSurvey()  throws IOException{
Constants.StartLoadingAnim(doubleBounce,progressOverlay);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        SendFormRequestModel requestModel = new SendFormRequestModel();

            qrList = new ArrayList<>();
            for (int index = 0; index < ScanConstants.bitmapTransporterList.size(); index++) {
                Image im = new Image();
                im.setImageQR(ScanConstants.bitmapTransporterList.get(index).QrValue);

                Uri ir = ScanConstants.bitmapTransporterList.get(index).BitmapPath;
                final File sd = Environment.getExternalStorageDirectory();
                File src = new File(sd, ir.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(src.getAbsolutePath());
                im.setImageBase64(getEncoded64ImageStringFromBitmap(bitmap));
                //im.setImageBase64("asd");
                qrList.add(im);
            }
            requestModel.setImages(qrList);

            requestModel.setFormDetail(ScanConstants.Selected_Form);
            requestModel.setCNo(ScanConstants.CNo);
                   Gson gson = new Gson();
            String json = gson.toJson(requestModel);
            URL url = new URL(Constants.BASE_URL+"Form/SendForm");

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "OkHttp Bot")
                    .post(body)
                    .build();

            try (okhttp3.Response response = httpClient.newCall(request).execute()) {

                if (response.isSuccessful()){
                    Constants.StopLoadingAnim(doubleBounce,progressOverlay);
                    KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.SUCCESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(com.scanlibrary.R.color.appRed);
                    pDialog.setTitleText("Sonuç");
                    pDialog.setContentText("Başarı ile kaydedildi.");
                    pDialog.setConfirmText("Tamam");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
                else        {
                    Constants.StopLoadingAnim(doubleBounce,progressOverlay);
                    KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.WARNING_TYPE);
                    pDialog.getProgressHelper().setBarColor(com.scanlibrary.R.color.appRed);
                    pDialog.setTitleText("Sonuç");
                    pDialog.setContentText("Bir sorun oluştu.");
                    pDialog.setConfirmText("Tamam");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // Get response body
                System.out.println(response.body().string());
            }
           /*MainApplication.apiManager.SendForm(requestModel, new Callback<SendResponseModel>() {
                @Override
                public void onResponse(Call<SendResponseModel> call, Response<SendResponseModel> response) {
                    Log.e("SONUC 200:", String.valueOf(response.isSuccessful()));
                    Constants.StopLoadingAnim(doubleBounce,progressOverlay);
                    KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.SUCCESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(com.scanlibrary.R.color.appRed);
                    pDialog.setTitleText("Sonuç");
                    pDialog.setContentText("Başarı ile kaydedildi.");
                    pDialog.setConfirmText("Tamam");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                @Override
                public void onFailure(Call<SendResponseModel> call, Throwable t) {
                    Log.e("SONUC 500: ", t.getMessage());
                    Constants.StopLoadingAnim(doubleBounce,progressOverlay);
                    KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.WARNING_TYPE);
                    pDialog.getProgressHelper().setBarColor(com.scanlibrary.R.color.appRed);
                    pDialog.setTitleText("Sonuç");
                    pDialog.setContentText("Bir sorun oluştu.");
                    pDialog.setConfirmText("Tamam");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
            });*/
    }

}
