
package com.scanlibrary.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class FormDetail {

    @SerializedName("documentid")
    private String mDocumentid;
    @SerializedName("formType")
    private String mFormType;
    @SerializedName("hasBarcode")
    private String mHasBarcode;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("page")
    private List<Page> mPage;
    @SerializedName("pageControl")
    private String mPageControl;
    @SerializedName("pageCount")
    private String mPageCount;
    @SerializedName("required")
    private String mRequired;
    @SerializedName("sigRegion")
    private String mSigRegion;

    public String getDocumentid() {
        return mDocumentid;
    }

    public void setDocumentid(String documentid) {
        mDocumentid = documentid;
    }

    public String getFormType() {
        return mFormType;
    }

    public void setFormType(String formType) {
        mFormType = formType;
    }

    public String getHasBarcode() {
        return mHasBarcode;
    }

    public void setHasBarcode(String hasBarcode) {
        mHasBarcode = hasBarcode;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<Page> getPage() {
        return mPage;
    }

    public void setPage(List<Page> page) {
        mPage = page;
    }

    public String getPageControl() {
        return mPageControl;
    }

    public void setPageControl(String pageControl) {
        mPageControl = pageControl;
    }

    public String getPageCount() {
        return mPageCount;
    }

    public void setPageCount(String pageCount) {
        mPageCount = pageCount;
    }

    public String getRequired() {
        return mRequired;
    }

    public void setRequired(String required) {
        mRequired = required;
    }

    public String getSigRegion() {
        return mSigRegion;
    }

    public void setSigRegion(String sigRegion) {
        mSigRegion = sigRegion;
    }
    @Override
    public String toString() {
        return mName;
    }
}
