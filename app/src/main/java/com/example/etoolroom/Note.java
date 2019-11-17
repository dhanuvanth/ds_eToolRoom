package com.example.etoolroom;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String name;
    private String DocumentId;

    private String tool_txt;
    private String plant_txt;
    private String extr_txt;
    private String size;
    private String remarks1;
    private String remarks2;
    private String htime;
    private String hdate;
    private String radio;
    private String cdate;
    private String ctime;
    private String a;

    public Note() {
    }

    public Note(String name) {
        this.name = name;
    }

    @Exclude
    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        DocumentId = documentId;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getTool_txt() {
        return tool_txt;
    }

    public String getPlant_txt() {
        return plant_txt;
    }

    public String getExtr_txt() {
        return extr_txt;
    }

    public String getSize() {
        return size;
    }

    public String getRemarks1() {
        return remarks1;
    }

    public String getRemarks2() {
        return remarks2;
    }

    public String getHtime() {
        return htime;
    }

    public String getHdate() {
        return hdate;
    }

    public String getRadio() {
        return radio;
    }

    public String getCdate() {
        return cdate;
    }

    public String getCtime() {
        return ctime;
    }

    public Note(String tool_txt, String plant_txt, String extr_txt, String size, String remarks1,String remarks2, String htime, String hdate, String radio, String cdate, String ctime) {
        this.tool_txt = tool_txt;
        this.plant_txt = plant_txt;
        this.extr_txt = extr_txt;
        this.size = size;
        this.remarks1 = remarks1;
        this.remarks2 = remarks2;
        this.htime = htime;
        this.hdate = hdate;
        this.radio = radio;
        this.cdate = cdate;
        this.ctime = ctime;
    }

    public String getName() {
        return name;
    }

    public Note(String tool_txt, String plant_txt, String extr_txt, String size, String remarks1, String htime, String hdate, String radio) {
        this.tool_txt = tool_txt;
        this.plant_txt = plant_txt;
        this.extr_txt = extr_txt;
        this.size = size;
        this.remarks1 = remarks1;
        this.htime = htime;
        this.hdate = hdate;
        this.radio = radio;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
