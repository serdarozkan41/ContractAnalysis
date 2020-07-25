package com.teesteknoloji.contractanalysis;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.teesteknoloji.contractanalysis.persistance.DocumentViewModel;
import com.teesteknoloji.contractanalysis.persistance.Document;
import com.teesteknoloji.contractanalysis.utils.DialogUtil;
import com.teesteknoloji.contractanalysis.utils.DialogUtilCallback;
import com.teesteknoloji.contractanalysis.fileView.FLAdapter;
import com.teesteknoloji.contractanalysis.utils.FileIOUtils;
import com.teesteknoloji.contractanalysis.utils.FileWritingCallback;
import com.teesteknoloji.contractanalysis.utils.OCRUtils;
import com.teesteknoloji.contractanalysis.utils.PDFWriterUtil;
import com.teesteknoloji.contractanalysis.utils.PermissionUtil;
import com.teesteknoloji.contractanalysis.utils.UIUtil;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FLAdapter fileAdapter;
    private final Context c = this;
    private List<Uri> scannedBitmaps = new ArrayList<>();

   // private DocumentViewModel viewModel;
    //private LinearLayout emptyLayout;

  //  private String searchText = "";
    LiveData<List<Document>> liveData;

    MaterialSpinner DbProducts;
    MaterialSpinner DbCampaings;
    MaterialSpinner DbFormTypes;
    MaterialSpinner DbScanForms;
    EditText TbContractNumber;
    private List<String> ProductList;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtil.ask(this);

        final String baseStorageDirectory =  getApplicationContext().getString( R.string.base_storage_path);
        FileIOUtils.mkdir( baseStorageDirectory );

        final String baseStagingDirectory =  getApplicationContext().getString( R.string.base_staging_path);
        FileIOUtils.mkdir( baseStagingDirectory );

        final String scanningTmpDirectory =  getApplicationContext().getString( R.string.base_scantmp_path);
        FileIOUtils.mkdir( scanningTmpDirectory );

        InitComponents();
    }

    private void InitComponents(){
        DbProducts = findViewById(R.id.DbProducts);
        DbCampaings = findViewById(R.id.DbCampaings);
        DbFormTypes = findViewById(R.id.DbFormTypes);
        DbScanForms = findViewById(R.id.DbScanForms);
        TbContractNumber = findViewById(R.id.TbContractNumber);

        ProductList = new ArrayList<>();
        ProductList.add("Kampong Thom");
        ProductList.add("Kampong Cham");
        ProductList.add("Kampong Chhnang");
        ProductList.add("Phnom Penh");
        ProductList.add("Kandal");
        ProductList.add("Kampot");

        DbProducts.setItems(ProductList);

        DbProducts.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        DbCampaings.setItems(ProductList);

        DbCampaings.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        DbFormTypes.setItems(ProductList);

        DbFormTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        DbScanForms.setItems(ProductList);

        DbScanForms.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    public void goToSearch(MenuItem mi){
        Intent intent = new Intent(this, SearchableActivity.class);
        startActivityForResult(intent, 0);
    }


    public void goToPreferences(MenuItem mi){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
    }

    public void openCamera(View v){
        scannedBitmaps.clear();

        String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );
        String scanningTmpDirectory =  getApplicationContext().getString( R.string.base_scantmp_path);

        FileIOUtils.clearDirectory( stagingDirPath );
        FileIOUtils.clearDirectory( scanningTmpDirectory );


        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);

        //startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle());
    }

//    public void openGallery(View v){
//        scannedBitmaps.clear();
//
//        String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );
//        FileIOUtils.clearDirectory( stagingDirPath );
//
//        Intent intent = new Intent(this, ScanActivity.class);
//        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA);
//        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
//    }

    private void saveBitmap( final Bitmap bitmap, final boolean addMore ){

        final String baseDirectory =  getApplicationContext().getString( addMore ? R.string.base_staging_path : R.string.base_storage_path);
            final File sd = Environment.getExternalStorageDirectory();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
            final String timestamp = simpleDateFormat.format( new Date() );

            if( addMore ){

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

                }catch(IOException ioe){
                    ioe.printStackTrace();
                }

            } else {

                DialogUtil.askUserFilaname( c, null, null, new DialogUtilCallback() {

                    @Override
                    public void onSave(String textValue, String category) {

                        try {

                            final PDFWriterUtil pdfWriter = new PDFWriterUtil();

                            String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );

                            List<File> stagingFiles = FileIOUtils.getAllFiles( stagingDirPath );
                            for ( File stagedFile : stagingFiles ) {
                                pdfWriter.addFile( stagedFile );
                            }

                            pdfWriter.addBitmap(bitmap);

                            String filename = "SCANNED_" + timestamp + ".pdf";
                            FileIOUtils.writeFile( baseDirectory, filename, new FileWritingCallback() {
                                @Override
                                public void write(FileOutputStream out) {
                                    try {
                                        pdfWriter.write(out);

                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                }
                            });


                            fileAdapter.notifyDataSetChanged();

                            FileIOUtils.clearDirectory( stagingDirPath );

                            SimpleDateFormat simpleDateFormatView = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                            final String timestampView = simpleDateFormatView.format(new Date());

                            Document newDocument = new Document();
                            newDocument.setName( textValue );
                            newDocument.setCategory( category );
                            newDocument.setPath( filename );
                            newDocument.setScanned( timestampView );
                            newDocument.setPageCount( pdfWriter.getPageCount() );
                            //viewModel.saveDocument(newDocument);

                            pdfWriter.close();

                            bitmap.recycle();
                            System.gc();

                        }catch(IOException ioe){
                            ioe.printStackTrace();

                        }

                    }
                });

            }
    }

    private void savePdf() {

        final String baseDirectory = getApplicationContext().getString(R.string.base_storage_path);
        final File sd = Environment.getExternalStorageDirectory();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        final String timestamp = simpleDateFormat.format(new Date());


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

                    System.gc();

                } catch (IOException ioe) {
                    ioe.printStackTrace();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( ( requestCode == ScanConstants.PICKFILE_REQUEST_CODE || requestCode == ScanConstants.START_CAMERA_REQUEST_CODE ) &&
                resultCode == Activity.RESULT_OK) {


            boolean saveMode = data.getExtras().containsKey(ScanConstants.SAVE_PDF) ? data.getExtras().getBoolean( ScanConstants.SAVE_PDF ) : Boolean.FALSE;
            if(saveMode){
                savePdf();

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
        }
    }
}
