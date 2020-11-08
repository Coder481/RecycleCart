package com.example.recyclecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.recyclecart.databinding.ActivityCatalogBinding;
import com.example.recyclecart.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    /**
     * 1. Create layout for each item
     * 2. Add RecyclerView to the layout
     * 3. Create Adapter for RecyclerView(RV)
     * 4. Initialize the Adapter
     * 5. Set the Adapter to RV
     */

    private ActivityCatalogBinding b;
    private  ArrayList<Product> products;
    private ProductsAdapter adapter;

    // Inflating the menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_catalog_options,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // OnItem Click Listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_btn){
            showProductEditorDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // OnClick handler for ContextualMenu of Product
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.contextualMenuEdit:
                editLastSelectedItem();
                return true;
            case R.id.contextualMenuRemove:
                removeLastSelectedItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    // Edit the details of Product
    private void editLastSelectedItem() {
        // Get data to be edited
        Product lastSelectedProduct = products.get(adapter.lastSelectedItemPosition);

        // Show editor dialog
        new ProductEditorDialog()
                .show(this, lastSelectedProduct, new ProductEditorDialog.OnProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        // Replace old Data
                        products.set(adapter.lastSelectedItemPosition,product);

                        // Update View
                        adapter.notifyItemChanged(adapter.lastSelectedItemPosition);
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(CatalogActivity.this, "Cancelled!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Remove the product
    private void removeLastSelectedItem() {
        new AlertDialog.Builder(this)
                .setTitle("Do You really want to remove the Item?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        products.remove(adapter.lastSelectedItemPosition);
                        adapter.notifyItemRemoved(adapter.lastSelectedItemPosition);
                        Toast.makeText(CatalogActivity.this, "Item Removed!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL",null)
                .show();
    }


    private void showProductEditorDialog() {
        new ProductEditorDialog()
                .show(this, new Product(), new ProductEditorDialog.OnProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        products.add(product);
                        adapter.notifyItemInserted(products.size()-1);
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(CatalogActivity.this,"Cancelled!!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        b= ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());



        // Showing the Dialog of Weight Picker
        b.showWeightPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Weight Picker Dialog
                WeightPicker.show(CatalogActivity.this, new WeightPicker.OnWeightPickedListener() {
                    @Override
                    public void onWeightPicked(int kg, int g) {
                        Toast.makeText(CatalogActivity.this,"Picked values\n"+kg+" kg and "+g*50+" gm",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWeightPickerCancelled() {
                        Toast.makeText(CatalogActivity.this,"Cancelled!!",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        setupProductsList();
    }

    private void setupProductsList() {
        // Create DataSet
        products = new ArrayList<>();

        // Create adapter object
        adapter = new ProductsAdapter(this,products);

        // Set the adapter & LayoutManager to recyclerView
        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }
}