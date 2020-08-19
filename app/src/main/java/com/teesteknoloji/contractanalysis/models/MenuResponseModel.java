
package com.teesteknoloji.contractanalysis.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.scanlibrary.models.Product;

@SuppressWarnings("unused")
public class MenuResponseModel {

    @SerializedName("message")
    private Object mMessage;
    @SerializedName("product")
    private List<Product> mProduct;
    @SerializedName("status")
    private Boolean mStatus;

    public Object getMessage() {
        return mMessage;
    }

    public void setMessage(Object message) {
        mMessage = message;
    }

    public List<Product> getProduct() {
        return mProduct;
    }

    public void setProduct(List<Product> product) {
        mProduct = product;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }
}
