package com.eighttechprojects.propertytaxshirol.Model;

import java.util.ArrayList;
import java.util.List;

public class FormModel {

    private FormFields form;
    private ArrayList<FormTableModel> detais;

//------------------------------------------------------- Constructor ---------------------------------------------------------------------------------------------------------------------------

    public FormModel() {}

//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------


    public FormFields getForm() {
        return form;
    }

    public ArrayList<FormTableModel> getDetais() {
        return detais;
    }


//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setForm(FormFields form) {
        this.form = form;
    }

    public void setDetais(List<FormTableModel> detais) {
        this.detais = (ArrayList<FormTableModel>) detais;
    }


}



