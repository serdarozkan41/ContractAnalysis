
package com.scanlibrary.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class Image {

    @Expose
    private String imageBase64;
    @Expose
    private String imageQR;

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageQR() {
        return imageQR;
    }

    public void setImageQR(String imageQR) {
        this.imageQR = imageQR;
    }

}
