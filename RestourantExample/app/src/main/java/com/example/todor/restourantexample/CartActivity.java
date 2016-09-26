package com.example.todor.restourantexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by todor on 28.07.14.
 */
public class CartActivity extends Activity {
    CartAdapter arrayAdpt;
    public static double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        Button buttonMenu = (Button) this.findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton imageButtonBack = (ImageButton) this.findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView lv = (ListView) findViewById(R.id.listView);
        arrayAdpt = new CartAdapter(ItemActivity.ChosenProduct, this);
        lv.setAdapter(arrayAdpt);

        total = 0;

        for (int i = 0; i < ItemActivity.ChosenProduct.size(); i++) {
            double count = ItemActivity.ChosenProduct.get(i).getCount();
            String priceString = ItemActivity.ChosenProduct.get(i).getPrice();
            //String noDolarPrice = priceString.substring(1, priceString.length());
            double price = Double.parseDouble(priceString);
            total += count * price;
        }

        String formattedTotal = String.format("%.2f", total);
        final TextView totalPriceText = (TextView) findViewById(R.id.totalPriceText);
        totalPriceText.setText(formattedTotal);
    }
}
