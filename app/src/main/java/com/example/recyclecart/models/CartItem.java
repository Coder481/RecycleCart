package com.example.recyclecart.models;

public class CartItem {

    public String name;
    public int price;
    public float qty;

    public CartItem(String name, int price, float qty) {
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "name='" + name +
                ", price=" + price +
                ", qty=" + qty ;
    }
}
