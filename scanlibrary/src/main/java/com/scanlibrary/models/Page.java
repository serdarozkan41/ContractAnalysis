
package com.scanlibrary.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class Page {

    @Expose
    private String id;
    @Expose
    private String pageNo;
    @Expose
    private String sigRegion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getSigRegion() {
        return sigRegion;
    }

    public void setSigRegion(String sigRegion) {
        this.sigRegion = sigRegion;
    }

}
