
package com.scanlibrary.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class SendFormRequestModel {

    @SerializedName("cNo")
    private String mCNo;
    @SerializedName("campaingId")
    private Long mCampaingId;
    @SerializedName("formDetail")
    private FormDetail mFormDetail;
    @SerializedName("forms")
    private List<Form> mForms;
    @SerializedName("productId")
    private Long mProductId;

    public String getCNo() {
        return mCNo;
    }

    public void setCNo(String cNo) {
        mCNo = cNo;
    }

    public Long getCampaingId() {
        return mCampaingId;
    }

    public void setCampaingId(Long campaingId) {
        mCampaingId = campaingId;
    }

    public FormDetail getFormDetail() {
        return mFormDetail;
    }

    public void setFormDetail(FormDetail formDetail) {
        mFormDetail = formDetail;
    }

    public List<Form> getForms() {
        return mForms;
    }

    public void setForms(List<Form> forms) {
        mForms = forms;
    }

    public Long getProductId() {
        return mProductId;
    }

    public void setProductId(Long productId) {
        mProductId = productId;
    }

}
