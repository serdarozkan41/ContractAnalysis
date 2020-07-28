
package com.teesteknoloji.contractanalysis.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class AuthRequestModel {

    @SerializedName("phone")
    private String mPhone;

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

}
