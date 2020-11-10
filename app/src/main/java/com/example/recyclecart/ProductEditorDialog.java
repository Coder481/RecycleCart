package com.example.recyclecart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.recyclecart.databinding.DialogProductEditBinding;
import com.example.recyclecart.models.Product;

import java.util.regex.Pattern;

import static com.example.recyclecart.models.Product.VARIANTS_BASED;
import static com.example.recyclecart.models.Product.WEIGHT_BASED;

public class ProductEditorDialog {

    private DialogProductEditBinding b;
    public Product product;

    public static final byte PRODUCT_ADD =0 , PRODUCT_EDIT =1;
    byte whyProduct;
    public ProductEditorDialog(byte type){
        whyProduct = type;
    }

    void show(final Context context, final Product product, final OnProductEditedListener listener){

        // Inflate
        b = DialogProductEditBinding.inflate(LayoutInflater.from(context));


        // Show Dialog when OptionsMenu (+) button is clicked
        new AlertDialog.Builder(context)
                .setTitle("Edit Product")
                .setView(b.getRoot())
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (areProductDetailsValid(whyProduct))
                            listener.onProductEdited(ProductEditorDialog.this.product);

                        else
                            Toast.makeText(context,"Invalid Details!!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelled();
                    }
                })
                .show();

        setupRadioButton();
        if(whyProduct==PRODUCT_EDIT){
            preFillPreviousDetails();
        }
    }

    // Show previously filled details when Edit button (in contextual Menu) is clicked
    private void preFillPreviousDetails() {
        // Set name
        b.name.setText(product.name);

        // Change Radio Selection
        b.productType.check(product.type == WEIGHT_BASED ? R.id.weightBasedRadioBtn : R.id.variantBasedRadioBtn);

        // Setup views according to type of Product
        if (product.type == WEIGHT_BASED){
            b.pricePerKgEditText.setText(product.pricePerkg+"");
            b.minQtyEditText.setText(product.minQtyToString());
        }
        else{
            b.variantsEditText.setText(product.variantsString());
        }
    }


    // Set visibility of either Variants or WeightBased
    private void setupRadioButton() {
        b.productType.clearCheck();

        b.productType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.weightBasedRadioBtn){
                    b.weightBasedMinQtyRoot.setVisibility(View.VISIBLE);
                    b.weightBasedPricePerKgRoot.setVisibility(View.VISIBLE);
                    b.variantsRoot.setVisibility(View.GONE);
                }
                else{
                    b.variantsRoot.setVisibility(View.VISIBLE);
                    b.weightBasedMinQtyRoot.setVisibility(View.GONE);
                    b.weightBasedPricePerKgRoot.setVisibility(View.GONE); }
            }
        });
    }

    // Check for Product Details Validation
    private boolean areProductDetailsValid(byte type) {
        // checking for name
        String name = b.name.getText().toString().trim();
        if (name.isEmpty())
            return false;

        // Set name to product
        //product.name=name;


        switch(b.productType.getCheckedRadioButtonId()){
            case R.id.weightBasedRadioBtn:

                // Get Values from Views
                String pricePerKg = b.pricePerKgEditText.getText().toString().trim()
                        , minQty = b.minQtyEditText.getText().toString().trim();
                // Check inputs
                if(pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)"))
                    return false;

                // If All Good,set Values to the product
                if(type == PRODUCT_EDIT){
                    product.initWeightBasedProduct(name
                            ,Integer.parseInt(pricePerKg)
                            ,extractMinQtyFromString(minQty));
                    return true;
                }
                product = new Product(name,Integer.parseInt(pricePerKg),extractMinQtyFromString(minQty));
                return true;

            case R.id.variantBasedRadioBtn:

                // Get Values from views
                String variants = b.variantsEditText.getText().toString().trim();

                if(type == PRODUCT_ADD){
                    product = new Product(name);
                }else{
                    product.initVarientBasedProduct(name);
                }
                return areVariantsValid(variants);
        }
        return  false;
    }

    // Return float value of MinQty from string like "12kg" "400g"
    private float extractMinQtyFromString(String minQty) {

        /*if(minQty.contains("kg") || minQty.contains("Kg") || minQty.contains("KG"))
            return Integer.parseInt(minQty.replaceAll("kg|KG|Kg",""));*/
        if(minQty.contains("kg"))
            return Integer.parseInt(minQty.replace("kg",""));
        else
            return Integer.parseInt(minQty.replace("g",""))/1000f;
    }

    // Checks for valid variants
    private boolean areVariantsValid(String variants) {
        if (variants.length()==0)
            return false;

        String[] vs = variants.split("\n");
        Pattern pattern = Pattern.compile("^\\w+(\\s|\\w)+,(\\s+\\d+$|\\d+$)");
        for (String variant : vs) {
            if (!pattern.matcher(variant).matches())
                return false;
        }

        // Extracts Variants from String[]
        product.fromVariantStrings(vs);
        return true;
    }

    // Listener Interface to notify Activity of Dialog events
    interface OnProductEditedListener{
        void onProductEdited(Product product);
        void onCancelled();
    }
}
