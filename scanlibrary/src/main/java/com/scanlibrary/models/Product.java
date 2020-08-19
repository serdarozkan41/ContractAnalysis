package com.scanlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("campaign")
    private List<Campaign> Campaign;
    @SerializedName("id")
    private Long Id;
    @SerializedName("name")
    private String Name;

    public List<Campaign> getCampaign() {
        return Campaign;
    }

    public void setCampaign(List<Campaign> campaign) {
        Campaign = campaign;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
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
