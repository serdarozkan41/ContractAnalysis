
package com.teesteknoloji.contractanalysis.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class AuthResponseModel {

    @SerializedName("bayiId")
    private Long mBayiId;
    @SerializedName("status")
    private Boolean mStatus;
    @SerializedName("token")
    private String mToken;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("userId")
    private Long mUserId;

    public Long getBayiId() {
        return mBayiId;
    }

    public void setBayiId(Long bayiId) {
        mBayiId = bayiId;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

}
