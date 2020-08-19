package com.scanlibrary;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.scanlibrary.models.Campaign;
import com.scanlibrary.models.Form;
import com.scanlibrary.models.FormDetail;
import com.scanlibrary.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhansi on 15/03/15.
 */
public class ScanConstants {

    public final static int PICKFILE_REQUEST_CODE = 1;
    public final static int START_CAMERA_REQUEST_CODE = 2;
    public final static String OPEN_INTENT_PREFERENCE = "selectContent";
    public final static String IMAGE_BASE_PATH_EXTRA = "ImageBasePath";
    public final static int OPEN_CAMERA = 4;
    public final static int OPEN_MEDIA = 5;
    public final static String SCANNED_RESULT = "scannedResult";
    public final static String SCANNED_QR = "scannedQR";
    public final static String SCANNED_List = "scannedList";
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/ContractAnalysis/tmp";
    public static List<BitmapTransporter> bitmapTransporterList = new ArrayList<>();
    public final static String SELECTED_BITMAP = "selectedBitmap";
    public final static String SCAN_MORE = "scanMore";
    public final static String SAVE_PDF = "savePdf";
    public static FormDetail Selected_Form = null;
    public static boolean Skip=false;
    public static String CNo="";
    public static Product Selected_Product;
    public static Campaign Selected_Campaing;
    //public final static
}

