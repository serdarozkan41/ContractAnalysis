
package com.scanlibrary.models;

import java.util.List;
import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class SendFormRequestModel {

    @Expose
    private String cNo;
    @Expose
    private Form formDetail;
    @Expose
    private List<Image> images;

    public String getCNo() {
        return cNo;
    }

    public void setCNo(String cNo) {
        this.cNo = cNo;
    }

    public Form getFormDetail() {
        return formDetail;
    }

    public void setFormDetail(Form formDetail) {
        this.formDetail = formDetail;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}
