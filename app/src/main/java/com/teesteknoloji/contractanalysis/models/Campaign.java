
package com.teesteknoloji.contractanalysis.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.scanlibrary.models.Form;

@SuppressWarnings("unused")
public class Campaign {

    @SerializedName("form")
    private List<Form> Form;
    @SerializedName("id")
    private String Id;
    @SerializedName("name")
    private String Name;

    public List<Form> getForm() {
        return Form;
    }

    public void setForm(List<Form> form) {
        Form = form;
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
