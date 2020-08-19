
package com.scanlibrary.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Form {

    @SerializedName("formBarcode")
    private String mFormBarcode;
    @SerializedName("formId")
    private Long mFormId;
    @SerializedName("formNo")
    private String mFormNo;
    @SerializedName("isControl")
    private Boolean mIsControl;
    @SerializedName("pages")
    private List<Page> mPages;

    public String getFormBarcode() {
        return mFormBarcode;
    }

    public void setFormBarcode(String formBarcode) {
        mFormBarcode = formBarcode;
    }

    public Long getFormId() {
        return mFormId;
    }

    public void setFormId(Long formId) {
        mFormId = formId;
    }

    public String getFormNo() {
        return mFormNo;
    }

    public void setFormNo(String formNo) {
        mFormNo = formNo;
    }

    public Boolean getIsControl() {
        return mIsControl;
    }

    public void setIsControl(Boolean isControl) {
        mIsControl = isControl;
    }

    public List<Page> getPages() {
        return mPages;
    }

    public void setPages(List<Page> pages) {
        mPages = pages;
    }

}
