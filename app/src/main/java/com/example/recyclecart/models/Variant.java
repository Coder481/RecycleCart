package com.example.recyclecart.models;

import androidx.annotation.NonNull;

public class Variant {
    public  String name;
    public int price;

    public Variant(String name,int price){
        this.name=name;
        this.price=price;
    }

    @NonNull
    @Override
    public String toString() {
        return name+" -Rs. "+price;
    }
}
