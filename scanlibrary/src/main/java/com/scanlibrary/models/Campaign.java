package com.scanlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Campaign {

    @SerializedName("form")
    private List<FormDetail> Form;
    @SerializedName("id")
    private Long Id;
    @SerializedName("name")
    private String Name;

    public List<FormDetail> getForm() {
        return Form;
    }

    public void setForm(List<FormDetail> form) {
        Form = form;
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
