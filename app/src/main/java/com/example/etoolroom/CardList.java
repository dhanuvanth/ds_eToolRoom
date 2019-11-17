package com.example.etoolroom;

public class CardList {
    private String ID;
    private String size;
    private String plant;

    public String getID() {
        return ID;
    }

    public String getSize() {
        return size;
    }

    public String getPlant() {
        return plant;
    }

    public CardList(String ID, String size, String plant) {
        this.ID = ID;
        this.size = size;
        this.plant = plant;
    }
}
