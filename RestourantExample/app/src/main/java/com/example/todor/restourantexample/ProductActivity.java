package com.example.todor.restourantexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * Created by todor on 23.07.14.
 */
public class ProductActivity extends Activity {

    ArrayList<Product> productList = new ArrayList<Product>();
    ProductAdapter arrayAdpt;
    public static final String KEY_PRODUCTS = "products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.menu_layout);

        Button buttonCart = (Button)this.findViewById(R.id.buttonCart);
        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intBut = new Intent(ProductActivity.this, CartActivity.class);
                intBut.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ProductActivity.this.startActivity(intBut);
            }
        });

        Button buttonMenu = (Button) this.findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductActivity.this.finish();
            }
        });

        ImageButton imageButtonBack = (ImageButton)this.findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductActivity.this.finish();
            }
        });

        this.productList = (ArrayList<Product>) this.getIntent().getExtras().getSerializable(KEY_PRODUCTS);
        ListView listView = (ListView) this.findViewById(R.id.listView);
        this.arrayAdpt = new ProductAdapter(this.productList, this);
        listView.setAdapter(this.arrayAdpt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(ProductActivity.this.getApplicationContext(), ItemActivity.class);
                in.putExtra(ItemActivity.KEY_ITEMS, ProductActivity.this.productList.get(position));
                ProductActivity.this.startActivity(in);
            }
        });
    }
}
