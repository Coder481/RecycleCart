package com.example.recyclecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.example.recyclecart.databinding.ActivityCatalogBinding;
import com.example.recyclecart.models.CartItem;
import com.example.recyclecart.models.Inventory;
import com.example.recyclecart.models.Order;
import com.example.recyclecart.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
    private  List<Product> products;
    private ProductsAdapter adapter;
    private SearchView searchView;

    // Drag And Drop
    public boolean isDragOn=false;
    private ItemTouchHelper itemTouchHelper;

    // SharedPreferences
    private SharedPreferences mSharedPref;
    private final String MY_DATA="myData";
    private MyApp app;
    private CartItem cartItem;








    /** Options Menu**/
    // Inflating the menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_catalog_options,menu);

        searchView =  (SearchView) menu.findItem(R.id.search_btn).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    // OnItem Click Listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Showing the dialog to add new Product
        switch (item.getItemId()){
            case R.id.add_btn:
                addProduct();
                return true;
            case R.id.dragAndDropBtn:
                toggleDragAndDropBtn(item);
                isDragOn = !isDragOn;
                return true;
            case R.id.showOrders:
                moveToOrdersActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void moveToOrdersActivity() {
        startActivity(new Intent(CatalogActivity.this,OrdersActivity.class));
    }







    /** Drag And Drop**/
    private void toggleDragAndDropBtn(MenuItem item) {
        Drawable icon = item.getIcon();
        if(isDragOn){
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }else{
            icon.setColorFilter(getResources().getColor(R.color.black),PorterDuff.Mode.SRC_ATOP);
        }
        item.setIcon(icon);
        if(isDragOn){
            itemTouchHelper.attachToRecyclerView(null);
        } else{
            itemTouchHelper.attachToRecyclerView(b.recyclerView);
        }
    }
    private void dragAndDropProduct(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN |
                ItemTouchHelper.START | ItemTouchHelper.END , 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(adapter.visibleProducts,fromPosition,toPosition);
                b.recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }

        };
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
    }







    /** Contextual Menu**/
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
            case R.id.weigth_picker:
                showWeightPickerForWBP(adapter.visibleProducts.get(adapter.lastSelectedItemPosition).type);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    // Weight Picker Dialog for weightBased Product
    private void showWeightPickerForWBP(int type) {
        if (type==0){
            float minQ = adapter.visibleProducts.get(adapter.lastSelectedItemPosition).minQty;
            final int KG = extractCredentialsFromFloat(minQ).get(0);
            final int GM = extractCredentialsFromFloat(minQ).get(1);

            WeightPicker.show(CatalogActivity.this, new WeightPicker.OnWeightPickedListener() {
                @Override
                public void onWeightPicked(int kg, int g) {

                    Toast.makeText(CatalogActivity.this,"Picked values\n"+kg+" kg and "+g*50+" gm",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onWeightPickerCancelled() {
                    Toast.makeText(CatalogActivity.this,"Cancelled!!",Toast.LENGTH_SHORT).show();

                }
            },adapter,KG,GM);
        }
        else{
            Toast.makeText(CatalogActivity.this,"Not Available for Variant Based Product",Toast.LENGTH_SHORT).show();
        }

    }
    private static ArrayList<Integer> extractCredentialsFromFloat(float minQ){
        ArrayList<Integer> cred = new ArrayList<>();
        final int KG ,GM;
        if (minQ<0){
            KG=0;
            cred.add(KG);
            GM=(int)(minQ*1000);
            cred.add(GM);
            return cred;
        }else{
            KG=(int)(minQ);
            cred.add(KG);
            GM=(int)((minQ-KG)*1000);
            cred.add(GM);
            return cred;
        }
    }







    /** Product Editor**/
    // Edit the details of Product
    private void editLastSelectedItem() {
        // Get data to be edited
        Product lastSelectedProduct = adapter.visibleProducts.get(adapter.lastSelectedItemPosition);

        ProductEditorDialog productEditorDialog = new ProductEditorDialog(ProductEditorDialog.PRODUCT_EDIT);

        // Set lastSelectedProduct to product
        productEditorDialog.product = lastSelectedProduct;

        // Show editor dialog
        productEditorDialog
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
                        Product productToBERemove = adapter.visibleProducts.get(adapter.lastSelectedItemPosition);
                        adapter.visibleProducts.remove(adapter.lastSelectedItemPosition);

                        // products.remove(adapter.lastSelectedItemPosition);
                        adapter.allProducts.remove(productToBERemove);
                        adapter.notifyItemRemoved(adapter.lastSelectedItemPosition);
                        Toast.makeText(CatalogActivity.this, "Item Removed!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL",null)
                .show();
    }
    // Add new product
    private void addProduct() {
        new ProductEditorDialog(ProductEditorDialog.PRODUCT_ADD)
                .show(this, new Product(), new ProductEditorDialog.OnProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        adapter.allProducts.add(product);
                        adapter.visibleProducts.add(product);
                        adapter.notifyItemInserted(products.size()-1);
                        Toast.makeText(CatalogActivity.this,"Item Added!",Toast.LENGTH_SHORT).show();
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

        setup();
        loadSavedData();
        setupTopic();

    }

    private void setupTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("admin");
    }

    private void setup() {
        app = (MyApp) getApplicationContext();
    }







    /** Shared Preferences**/
    private void loadSavedData() {
        // Try to use sharedPreferences
        Gson gson = new Gson();
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        String json =  mSharedPref.getString(MY_DATA,null);

        if(json!=null){
            products = gson.fromJson(json,new TypeToken<List<Product>>(){}.getType());
            setupProductsList();
        }
        else{
            fetchDataFromCloud();
        }
    }

    private void fetchDataFromCloud() {
        if (app.isOffline()){
            app.showToast(this,"You are offline. Please check your connection");
            return;
        }

        app.showLoadingDialog(this);

        app.db.collection("Inventory").document("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Inventory inventory = documentSnapshot.toObject(Inventory.class);
                            products = inventory.products;
                            saveDataLocally();
                        }
                        else{
                            products = new ArrayList<>();
                        }
                        setupProductsList();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(CatalogActivity.this,"Failed to get Data");
                        app.hideLoadingDialog();
                    }
                });
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Do you want to save data?")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveData();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }

    /*@Override
        protected void onPause() {
            super.onPause();
        }*/
    private void saveDataLocally() {
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        Gson gson = new Gson();
        mSharedPref.edit()
                .putString(MY_DATA,gson.toJson(adapter.visibleProducts)) //TODO
                .apply();
    }

    private void saveData(){
        if (app.isOffline()){
            app.showToast(this,"Can't save. You are offline!!");
            return;
        }

        app.showLoadingDialog(this);
        Inventory inventory = new Inventory(products);

        app.db.collection("Inventory").document("Products")
                .set(inventory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveDataLocally();
                        app.hideLoadingDialog();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(CatalogActivity.this,"Failed to save data on Cloud");
                        app.hideLoadingDialog();
                    }
                });

    }

    private void setupProductsList() {
        // Create DataSet



        // Create adapter object
        adapter = new ProductsAdapter(this,products);

        // Set the adapter & LayoutManager to recyclerView
        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        // Drag And Drop
        dragAndDropProduct();
    }
}