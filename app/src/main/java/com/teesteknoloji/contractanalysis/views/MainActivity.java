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
import com.scanlibrary.models.Campaign;
import com.scanlibrary.models.Form;
import com.scanlibrary.models.FormDetail;
import com.scanlibrary.models.Image;
import com.scanlibrary.models.Page;
import com.scanlibrary.models.Product;
import com.scanlibrary.models.SendFormRequestModel;
import com.teesteknoloji.contractanalysis.MainApplication;
import com.teesteknoloji.contractanalysis.R;
import com.teesteknoloji.contractanalysis.models.MenuResponseModel;
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
/*import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;*/
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private ArrayAdapter<FormDetail> formTypeAdapter;

    LiveData<List<Document>> liveData;

    MaterialSpinner DbProducts;
    MaterialSpinner DbCampaings;
    MaterialSpinner DbFormTypes;
    EditText TbContractNumber;
    Button StartButton;
    private List<Product> ProductList;
    private List<Campaign> CampaingList;
    private List<FormDetail> FormTypeList;
    FormDetail selectedForm;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StartButton = findViewById(R.id.BuScan);
        StartButton.setEnabled(false);
        LoadingBarInit();


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
                ScanConstants.Selected_Product = item;
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
                        ScanConstants.Selected_Campaing = item;
                        formTypeAdapter = new ArrayAdapter<FormDetail>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, FormTypeList);
                        MaterialSpinnerAdapter<FormDetail> formMaterialSpinnerAdapter = new MaterialSpinnerAdapter<FormDetail>(MainActivity.this, FormTypeList);
                        DbFormTypes.setAdapter(formMaterialSpinnerAdapter);
                        DbFormTypes.setEnabled(true);
                        DbFormTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<FormDetail>() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, FormDetail item) {
                                selectedForm = item;
                                ScanConstants.Selected_Form = item;
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
            // ScanConstants.bitmapTransporterList = new ArrayList<BitmapTransporter>();
            ScanConstants.ActiveForm = new Form();
            ScanConstants.ActivePages = new ArrayList<>();

            String stagingDirPath = getApplicationContext().getString(R.string.base_staging_path);
            String scanningTmpDirectory = getApplicationContext().getString(R.string.base_scantmp_path);

            FileIOUtils.clearDirectory(stagingDirPath);
            FileIOUtils.clearDirectory(scanningTmpDirectory);

            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
            ScanConstants.CNo = TbContractNumber.getText().toString();

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle());
        } else {
            KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.ERROR_TYPE);
            pDialog.getProgressHelper().setBarColor(R.color.appRed);
            pDialog.setTitleText("Uyarı");
            pDialog.setContentText("Sözleşme numarası boş olamaz.");
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
                //List<BitmapTransporter> ll = ScanConstants.bitmapTransporterList;
                if (ScanConstants.IsFinish) {
                    Log.e("SAVE MODE: ", String.valueOf(saveMode));
                    KAlertDialog pDialog = new KAlertDialog(MainActivity.this, KAlertDialog.SUCCESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(com.scanlibrary.R.color.appRed);
                    pDialog.setTitleText("Sonuç");
                    pDialog.setContentText("Başarı ile kaydedildi.");
                    pDialog.setConfirmText("Tamam");
                    pDialog.setCancelable(false);
                    pDialog.show();


                    ScanConstants.Selected_Form = new FormDetail();
                    ScanConstants.Skip = false;
                    ScanConstants.CNo = "";
                    ScanConstants.Selected_Product = new Product();
                    ScanConstants.Selected_Campaing = new Campaign();
                    ScanConstants.Forms = new ArrayList<>();
                    ScanConstants.ActiveForm = new Form();
                    ScanConstants.ActivePages = new ArrayList<>();
                    ScanConstants.ActiveReqModel = new SendFormRequestModel();
                    ScanConstants.IsFinish = false;
                } else {
                    InitComponents();
                }
            } else {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                boolean doScanMore = data.getExtras().getBoolean(ScanConstants.SCAN_MORE);

                final File sd = Environment.getExternalStorageDirectory();
                File src = new File(sd, uri.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(src.getAbsolutePath());
                saveBitmap(bitmap, doScanMore);

                if (doScanMore) {
                    scannedBitmaps.add(uri);
                    Intent intent = new Intent(this, MultiPageActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);

                    startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
                }
            }

            Constants.StopLoadingAnim(doubleBounce, progressOverlay);
        }
    }


    private final OkHttpClient httpClient = new OkHttpClient();
}
