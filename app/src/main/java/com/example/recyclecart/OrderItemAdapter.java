package com.example.recyclecart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclecart.databinding.OrderItemListBinding;
import com.example.recyclecart.databinding.OrdersItemViewBinding;
import com.example.recyclecart.fcmsender.FCMSender;
import com.example.recyclecart.fcmsender.MsgFormatter;
import com.example.recyclecart.models.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public List<Order> orderList;
    private Order order;
//    OrdersActivity ordersActivity = (OrdersActivity)Activity

    public OrderItemAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrdersItemViewBinding b = OrdersItemViewBinding.inflate(
                LayoutInflater.from(context)
                ,parent
                , false);

        return new OrderItemViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        OrdersItemViewBinding b = ((OrderItemViewHolder) holder).b;
        b.qtyTextView.setText("Items:"+order.totlItems+"\nRs. "+order.totlPrice);

        b.userIdTextView.setText(order.userName+"\n"+order.userPhoneNo+"\n"+order.orderId);
        setupOrderStatus(order.status,b,order);
        setupOrderList(b,order);


    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void setupOrderStatus(final int status, final OrdersItemViewBinding b, final Order order) {

        if (status == Order.OrderStatus.PLACED){
            b.orderStatus.setVisibility(View.GONE);
            b.deliveredBtn.setVisibility(View.VISIBLE);
            b.declineBtn.setVisibility(View.VISIBLE);
            b.deliveredBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.orderStatus.setVisibility(View.VISIBLE);
                    b.declineBtn.setVisibility(View.GONE);
                    b.deliveredBtn.setVisibility(View.GONE);
                    b.orderStatus.setText("ACCEPTED");
                    b.orderStatus.setTextColor(Color.GREEN);
                    updateStatusOnFirestore(Order.OrderStatus.DELIVERED,order);
                }
            });
            b.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.orderStatus.setVisibility(View.VISIBLE);
                    b.declineBtn.setVisibility(View.GONE);
                    b.deliveredBtn.setVisibility(View.GONE);
                    b.orderStatus.setText("DECLINED");
                    b.orderStatus.setTextColor(Color.RED);
                    updateStatusOnFirestore(Order.OrderStatus.DECLINED,order);
                }
            });

        }else if (status == Order.OrderStatus.DECLINED){
            b.orderStatus.setVisibility(View.VISIBLE);
            b.declineBtn.setVisibility(View.GONE);
            b.deliveredBtn.setVisibility(View.GONE);
            b.orderStatus.setText("DECLINED");
            b.orderStatus.setTextColor(Color.RED);
        }else{
            b.orderStatus.setVisibility(View.VISIBLE);
            b.declineBtn.setVisibility(View.GONE);
            b.deliveredBtn.setVisibility(View.GONE);
            b.orderStatus.setText("ACCEPTED");
            b.orderStatus.setTextColor(Color.GREEN);
        }
    }

    private void updateStatusOnFirestore(final int status, final Order order) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String orderId = order.orderId;
        order.status = status;

        db.collection("Orders").document(orderId)
                .set(order, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendNotification(status,order);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "FAILED!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void sendNotification(int status, Order order) {

        String body = "Your Order is "+((status==Order.OrderStatus.DELIVERED)?"ACCEPTED \n will be delivered soon":"DECLINED");

        String message = MsgFormatter.getSampleMessage("users","Admin", order.userName,body);
        new FCMSender().send(message
                , new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        ((OrdersActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "FAILURE!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        ((OrdersActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

    }


    private void setupOrderList(OrdersItemViewBinding b, Order order) {
        b.allOrdersItemsList.removeAllViews();
        for (int i = 0;i<order.cartItemList.size();i++){
            OrderItemListBinding binding = OrderItemListBinding.inflate(LayoutInflater.from(context));
            binding.orderItemName.setText(order.cartItemList.get(i).name+"");
            binding.orderItemQty.setText("Qty:"+order.cartItemList.get(i).qty);
            binding.orderItemPrice.setText("Price:"+order.cartItemList.get(i).price);

            b.allOrdersItemsList.addView(binding.getRoot());
        }
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder{
        OrdersItemViewBinding b;

        public OrderItemViewHolder(@NonNull OrdersItemViewBinding b) {
            super(b.getRoot());
            this.b=b;
        }
    }
}
