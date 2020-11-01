package com.example.recyclecart.models;

import java.io.Serializable;

public class Product implements Serializable {
    public  String name;
    public int qty,price;
    public Product(String name , int price){
        this.name=name;
        this.price=price;
    }
}
