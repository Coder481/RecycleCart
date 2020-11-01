package com.example.recyclecart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.recyclecart.ProductsAdapter;
import android.app.Activity;
import android.os.Bundle;

import com.example.recyclecart.databinding.ActivityCatalogBinding;
import com.example.recyclecart.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    /**
     * 1.
     * 2.
     * 3.
     * 4.
     * 5.
     */

    private ActivityCatalogBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        b= ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setupProductsList();
    }

    private void setupProductsList() {
        // Create DataSet
        List<Product> products = new ArrayList<>(
                Arrays.asList(
                        new Product("Tomato",20)
                        ,new Product("Potato",30)
                        , new Product("Apple",100)
                        ,new Product("Banana",35)
                        ,new Product("Milk",55)
                        , new Product("Cheese",70)
                        ,new Product("Brocoli",50)
                        ,new Product("Carrots",40)
                        , new Product("Rice",60)
                        ,new Product("Butter",65)
                        ,new Product("Spinach",40)
                        , new Product("Strawberries",120)
                )
        );

        // Create adapter object
        ProductsAdapter adapter = new ProductsAdapter(this,products);

        // Set the adapter & LayoutManager to recyclerView
        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}