package com.example.todor.restourantexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by todor on 21.07.14.
 */
public class ItemActivity extends Activity {
    static final String KEY_ITEMS = "items";
    public static Product item;
    public static ArrayList<Product> ChosenProduct = new ArrayList<Product>();
    public static TextView quantityText;

    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        String isit = quantityText.getText().toString();
        if (!isit.equals("1")) {
            if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
                toast = Toast.makeText(this, "Press again if you are sure", Toast.LENGTH_LONG);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                super.onBackPressed();
            }
        }
        else {
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_layout);

        Button buttonCart = (Button)this.findViewById(R.id.buttonCart);
        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intBut = new Intent(ItemActivity.this, CartActivity.class);
                intBut.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intBut);
            }
        });

        Button buttonMenu = (Button) this.findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton imageButtonBack = (ImageButton)this.findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        item = getIntent().getExtras().getParcelable(KEY_ITEMS);

        TextView Name = (TextView) findViewById(R.id.name);
        final TextView Price = (TextView) findViewById(R.id.price);
        TextView Description = (TextView) findViewById(R.id.description);
        ImageView Thumbnail = (ImageView) findViewById(R.id.thumbnail);

        double screen = getResources().getDisplayMetrics().widthPixels;

        Name.setText(item.getName());
        Price.setText(item.getPrice());
        Description.setText(item.getDescription());
        AsyncImage ai = new AsyncImage(item.getThumbnail(), Thumbnail, ItemActivity.this);
        ai.execute();
//        try {
//            InputStream inputStream = getAssets().open(item.getThumbnail());
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            Thumbnail.setImageBitmap(Round.getRoundedCornerImage(bitmap, (int) (screen - 20*density),
//                    (int) (230*density), (int) (5*density), (int) (20*density)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        ImageButton imageButtonCheck = (ImageButton) this.findViewById(R.id.imageButtonCheck);
        imageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String priceString = item.getPrice();
                //String noDolarPrice = priceString.substring(1, priceString.length());
                double price = 0;
                try {
                    price = Double.parseDouble(priceString);
                }
                catch (Exception exception) {

                }
                if (price != 0) {
                    Product p = null;
                    for (int i = 0; i < ChosenProduct.size(); i++) {
                        if (ChosenProduct.get(i).getName().equals(item.getName())) {
                            p = ChosenProduct.get(i);
                            break;
                        }
                    }
                    if (p == null) {
                        int q = Integer.parseInt(quantityText.getText().toString());
                        item.setCount(q);
                        ChosenProduct.add(item);
                    } else {
                        int q = Integer.parseInt(quantityText.getText().toString()) + p.getCount();
                        item.setCount(q);
                        ChosenProduct.remove(p);
                        ChosenProduct.add(item);
                    }

                    toast = Toast.makeText(ItemActivity.this, "Item(s) added to CART", Toast.LENGTH_SHORT);
                    toast.show();
                    ItemActivity.quantityText.setText(String.valueOf(1));

                } else {
                    toast = Toast.makeText(ItemActivity.this, "Sorry, this item isn't currently available", Toast.LENGTH_LONG);
                    toast.show();
                }
                // TODO
            }
        });
        this.quantityText = (TextView)this.findViewById(R.id.quantityText);

        ImageButton imageButtonPlus = (ImageButton) this.findViewById(R.id.imageButtonPlus);
        imageButtonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(quantityText.getText().toString());
                q++;
                quantityText.setText(String.valueOf(q));
            }
        });
        ImageButton imageButtonMinus = (ImageButton) this.findViewById(R.id.imageButtonMinus);
        imageButtonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(quantityText.getText().toString());
                if (q > 1) {
                    q--;
                    quantityText.setText(String.valueOf(q));
                }
            }
        });
    }
}
