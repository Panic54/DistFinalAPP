package com.example.madsfinnerup.distfinalapp;

/**
 * Created by madsfinnerup on 01/08/2018.
 */

public class ItemsForSale {

    public String personName;
    public String itemName;
    public String price;

    public ItemsForSale (String personName, String itemName, String price) {
        this.personName = personName;
        this.itemName = itemName;
        this.price = price;

    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



}
