package com.example.recyclecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.recyclecart.databinding.ActivityOrdersBinding;
import com.example.recyclecart.databinding.OrdersItemViewBinding;
import com.example.recyclecart.models.CartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding b;
    private MyApp app;

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

        app.db.collection("Orders")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        app.showLoadingDialog(OrdersActivity.this);
                        for (DocumentSnapshot doc : queryDocumentSnapshots){
                            OrdersItemViewBinding ib = OrdersItemViewBinding.inflate(getLayoutInflater());


                            String customerId = doc.getId();
                            List<CartItem> cartItemList = (List<CartItem>) doc.get("Items Ordered");


                            //HashMap<String, CartItem> prdItem = (HashMap<String, CartItem>) doc.get("Items Ordered");
                            ib.idTextView.setText(customerId+"\nOrders: "+cartItemList);
                            b.orderItemView.addView(ib.getRoot());
                        }
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(OrdersActivity.this,"Error:\n"+e);
                    }
                });

       /* app.db.collection("Orders").document(format)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            //String orders = documentSnapshot.get("Items Ordered").toString();
                            String customerId = documentSnapshot.get("customerId").toString();
                            HashMap<String, CartItem> prdItem = (HashMap<String, CartItem>) documentSnapshot.get("Items Ordered");


                            new AlertDialog.Builder(OrdersActivity.this)
                                    .setTitle("Order Summary")
                                    .setMessage("Customer Id:"+customerId+"\nOrders:"+prdItem.values().toString())
                                    .show();
                        }else{
                            app.showToast(OrdersActivity.this,"No orders are placed!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(OrdersActivity.this,"Error:\n"+e);
                    }
                });*/
    }
}