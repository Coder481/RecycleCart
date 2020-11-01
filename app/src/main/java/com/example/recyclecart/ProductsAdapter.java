package com.example.recyclecart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclecart.databinding.ProductItemBinding;
import com.example.recyclecart.models.Product;

import java.util.List;

// Adapter for List of Products
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductsAdapter(Context context , List<Product> productList){
        this.context = context;
        this.productList = productList;
    }

    // Inflate the view for item abd create a ViewHolder object( View to Java Code)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //1. Inflate the layout for product_item.xml
        ProductItemBinding b = ProductItemBinding.inflate(LayoutInflater.from(context)
                ,parent ,
                false);

        //2. Create ViewHolder object  and return
        return new ViewHolder(b);
    }

    // Binds the data to view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // Get the data at position
        final Product product = productList.get(position);

        //Bind data
        // Name and Price
        holder.b.name.setText(String.format("%s (Rs. %d)",product.name,product.price));

        // Quantity
        holder.b.quantity.setText(product.qty+"");

        // decrementButton &  Quantity TV Visibility
        holder.b.decrementBtn.setVisibility(product.qty > 0 ? View.VISIBLE : View.GONE);
        holder.b.quantity.setVisibility(product.qty > 0 ? View.VISIBLE : View.GONE);

        // Configure increment
        holder.b.incrementBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                product.qty++;
                notifyItemChanged(position);
            }
        });
        // Configure decrement
        holder.b.decrementBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                product.qty--;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Holds the view for each item
    public class ViewHolder extends RecyclerView.ViewHolder{


        private com.example.recyclecart.databinding.ProductItemBinding b;

        public ViewHolder(@NonNull ProductItemBinding b) {
            super(b.getRoot());
            this.b = b;

        }
    }
}
