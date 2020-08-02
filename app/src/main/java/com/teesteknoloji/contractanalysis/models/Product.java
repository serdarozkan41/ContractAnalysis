
package com.teesteknoloji.contractanalysis.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Product {

    @SerializedName("campaign")
    private List<Campaign> Campaign;
    @SerializedName("id")
    private String Id;
    @SerializedName("name")
    private String Name;

    public List<Campaign> getCampaign() {
        return Campaign;
    }

    public void setCampaign(List<Campaign> campaign) {
        Campaign = campaign;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
