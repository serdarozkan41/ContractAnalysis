
package com.scanlibrary.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Page {

    @SerializedName("barcode")
    private String mBarcode;
    @SerializedName("id")
    private String mId;
    @SerializedName("imageBase64")
    private String mImageBase64;
    @SerializedName("pageFile")
    private String mPageFile;
    @SerializedName("pageNo")
    private String mPageNo;
    @SerializedName("sigRegion")
    private String mSigRegion;

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImageBase64() {
        return mImageBase64;
    }

    public void setImageBase64(String imageBase64) {
        mImageBase64 = imageBase64;
    }

    public String getPageFile() {
        return mPageFile;
    }

    public void setPageFile(String pageFile) {
        mPageFile = pageFile;
    }

    public String getPageNo() {
        return mPageNo;
    }

    public void setPageNo(String pageNo) {
        mPageNo = pageNo;
    }

    public String getSigRegion() {
        return mSigRegion;
    }

    public void setSigRegion(String sigRegion) {
        mSigRegion = sigRegion;
    }

}
