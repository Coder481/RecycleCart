package com.example.recyclecart;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclecart.databinding.VariantBasedProductBinding;
import com.example.recyclecart.databinding.WeightBasedProductBinding;
import com.example.recyclecart.models.Product;

import java.util.List;

// Adapter for List of Products
public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Needed for Inflating Data
    private Context context;

    // List of Data
    private List<Product> productList;

    int lastSelectedItemPosition;

    public ProductsAdapter(Context context , List<Product> productList){
        this.context = context;
        this.productList = productList;
    }

    // Inflate the view for item and create a ViewHolder object based on ViewType( View to Java Code)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==Product.WEIGHT_BASED){
            // Inflate the layout for weight_based_product.xml
            WeightBasedProductBinding b = WeightBasedProductBinding.inflate(
                    LayoutInflater.from(context)
                    ,parent
                    ,false);

            // Create and return WeightBasedProductViewHolder
            // Child -> Parent
            return new WeightBasedProductViewHolder(b);
        }else{
            // Inflate the Layout for variant_based_product.xml
            VariantBasedProductBinding b = VariantBasedProductBinding.inflate(LayoutInflater.from(context)
            ,parent
            ,false);

            // Create and Return VariantBasedProductViewHolder
            return new VariantBasedProductViewHolder(b);
        }

    }



    // Return ViewType Based on position
    @Override
    public int getItemViewType(int position) {
        return productList.get(position).type;
    }



    // Binds the data to view
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        // Get the data at position
        final Product product = productList.get(position);

        if (product.type == Product.WEIGHT_BASED){
            //Get Binding
            WeightBasedProductBinding b = ((WeightBasedProductViewHolder) holder).b;

            //Bind data
            b.weightBasedProductName.setText(product.name);
            b.weightBasedPricePerKg.setText("Rs. "+product.pricePerkg+"/kg");
            b.weightBasedMinQty.setText("MinQty-"+product.minQty);

            // Setup Contextual Menu inflation
            setupContextMenu(b.getRoot());

        }else{
            // Get Binding
            VariantBasedProductBinding b = ((VariantBasedProductViewHolder) holder).b;

            // Bind Data
            b.variantBasedProductName.setText(product.name);
            b.variantsBasedVariantName.setText(product.variantsString());

            // Setup Contextual Menu inflation
            setupContextMenu(b.getRoot());
        }

        // Save dynamic position of selected item to access it in Activity
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });


    }

    private void setupContextMenu(ConstraintLayout root) {
        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if(!(context instanceof CatalogActivity))
                    return;
                ((CatalogActivity) context).getMenuInflater().inflate(R.menu.product_contextual_menu,menu);
            }
        });
    }


    // Recycler View get to know how many Data are there to bind
    @Override
    public int getItemCount() {
        return productList.size();
    }


    // ViewHolder for Weight Based Product
    public static class  WeightBasedProductViewHolder extends RecyclerView.ViewHolder {
        WeightBasedProductBinding b;
        public WeightBasedProductViewHolder(@NonNull WeightBasedProductBinding b){
            super(b.getRoot());
            this.b=b;
        }

    }

    // ViewHolder for Variant Based Product
    public static class  VariantBasedProductViewHolder extends RecyclerView.ViewHolder {
        VariantBasedProductBinding b ;
        public VariantBasedProductViewHolder(@NonNull VariantBasedProductBinding b){
            super(b.getRoot());
            this.b=b;
        }
    }
}
