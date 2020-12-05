package com.example.recyclecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.recyclecart.databinding.ActivityOrdersBinding;
import com.example.recyclecart.models.Order;
import com.example.recyclecart.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding b;
    private MyApp app;

    private OrderItemAdapter adapter;
    private SharedPreferences mSharedPref;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b= ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        app = (MyApp) getApplicationContext();
        showOrders();
    }

    private void showOrders() {

        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH");
        String format = simpleDateFormat.format(new Date());*/

        app.showLoadingDialog(this);

        app.db.collection("Orders")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots){

                            Order order = doc.toObject(Order.class);
                            orderList.add(order);

                        }
                        setupAdapter();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.hideLoadingDialog();
                        app.showToast(OrdersActivity.this,"Error:\n"+e);
                    }
                });
    }

    private void setupAdapter() {
        adapter = new OrderItemAdapter(this,orderList);
        b.orderItemRecyclerView.setAdapter(adapter);
        b.orderItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        b.orderItemRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
}