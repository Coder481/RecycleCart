package com.example.recyclecart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.recyclecart.databinding.WeightPickerDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class WeightPicker {
    /**
     * There are 9 TODOs in this file, locate them using the window for it given at the bottom.
     * Also, complete them in order.
     *
     * TODO 1 : Design layout weight_picker_dialog.xml for this WeightPicker Dialog
     *          with 2 NumberPickers (for kg & g)
     */

    //private  ProductsAdapter adapter;

    public static void show(Context context, final OnWeightPickedListener listener , ProductsAdapter adapter, final int KG, final int GM){
        final WeightPickerDialogBinding b = WeightPickerDialogBinding.inflate(
                LayoutInflater.from(context)
        );

        new AlertDialog.Builder(context)
                .setTitle("Pick Weight")
                .setView(b.getRoot())
                .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO 3 : Replace 0s & assign kg & g values from respective NumberPickers
                        int kg ,g;
                        kg=b.numberPickerForKG.getValue();
                        g=b.numberPickerForGm.getValue();
                        if(kg==KG)
                            g=g-1;


                        //TODO 4 : Add GuardCode to prevent user from selecting 0kg 0g. If so, then return
                        if (kg==0 && g==0){return;}
                        listener.onWeightPicked(kg, g);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        listener.onWeightPickerCancelled();
                    }
                })
                .show();



        // Setup KG Weight Picker
        setupKgPicker(b, KG);
        // Setup GM Weight Picker for MinQty
        setupGmPickerForMinQty(GM, b);


        b.numberPickerForKG.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal==KG){
                    // Setup GM Weight Picker for MinQty
                    setupGmPickerForMinQty(GM, (WeightPickerDialogBinding) b);
                }else{
                    // Setup GM Weight Picker
                    setupGmPicker(b);
                }
            }
        });
        //setupNumberPickers(context);

        //TODO 5 : Call new WeightPicker().show() in MainActivity and pass (this, new OnWeight...)

        //TODO 6 : Show toast of selected weight in onWeightPicked() method

        //TODO 7 : Find appropriate solution for : NumberPicker not formatting the first item

        //TODO 8 : Test your code :)

        //TODO 9 : Try to understand the flow as to how our Listener interface is working

    }



    // Setup GM Weight Picker for regular GM Weight
    private static void setupGmPicker(com.example.recyclecart.databinding.WeightPickerDialogBinding b) {
        b.numberPickerForGm.setMaxValue(19);
        b.numberPickerForGm.setMinValue(0);
        b.numberPickerForGm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value*50+" gm";
            }
        });
    }

    // Setup KG Weight Picker
    private static void setupKgPicker(com.example.recyclecart.databinding.WeightPickerDialogBinding b, int KG) {
        b.numberPickerForKG.setMinValue(KG);
        b.numberPickerForKG.setMaxValue(10);
        b.numberPickerForKG.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value+" KG";
            }
        });
    }

    // Setup GM Weight Picker for MinQty
    private static void setupGmPickerForMinQty(final int GM, WeightPickerDialogBinding b) {
        b.numberPickerForGm.setMaxValue(20);
        b.numberPickerForGm.setMinValue((int) (GM / 50) + 1);
        b.numberPickerForGm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                if (value == (int) (GM / 50) + 1) {
                    return GM + " gm";
                }
                return (value - 1) * 50 + " gm";
            }
        });
    }

    /*private static void setupNumberPickers(Context context) {
        //TODO 2 : Define this method to setup kg & g NumberPickers as per the given ranges
        //kg Range - 0kg to 10kg
        //g Range - 0g to 950g
        WeightPickerDialogBinding b ;
        b= WeightPickerDialogBinding.inflate(LayoutInflater.from(context));
        b.numberPickerForKG.setMinValue(0);
        b.numberPickerForKG.setMaxValue(10);
        b.numberPickerForGm.setMaxValue(19);
        b.numberPickerForGm.setMinValue(0);
        b.numberPickerForKG.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value+" KG";
            }
        });
        b.numberPickerForGm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value*50 + " gm";
            }
        });
    }*/

    interface OnWeightPickedListener{
        void onWeightPicked(int kg, int g);
        void onWeightPickerCancelled();
    }
}
