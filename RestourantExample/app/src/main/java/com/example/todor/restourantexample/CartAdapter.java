package com.example.todor.restourantexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CartAdapter extends ArrayAdapter<Product> {

    public ArrayList<Product> chosenItemList;
    private Context context;

    public CartAdapter(ArrayList<Product> menuCatList, Context ctx) {
        super(ctx, R.layout.cart_row_layout, menuCatList);
        this.chosenItemList = menuCatList;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ChosenItemHolder chosenItemHolder = new ChosenItemHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.cart_row_layout, null);
            TextView nameView = (TextView) v.findViewById(R.id.name);
            TextView priceView = (TextView) v.findViewById(R.id.price);
            TextView descriptionView = (TextView) v.findViewById(R.id.description);
            ImageView imageView = (ImageView) v.findViewById(R.id.img);
            chosenItemHolder.nameView = nameView;
            chosenItemHolder.priceView = priceView;
            chosenItemHolder.descriptionView = descriptionView;
            chosenItemHolder.imageView = imageView;
            v.setTag(chosenItemHolder);
        }
        else {
            chosenItemHolder = (ChosenItemHolder) v.getTag();
        }

        float density = context.getResources().getDisplayMetrics().density;

        final Product menuCat = chosenItemList.get(position);
        chosenItemHolder.nameView.setText(menuCat.getName());
        chosenItemHolder.priceView.setText(menuCat.getPrice());
        chosenItemHolder.descriptionView.setText(menuCat.getDescription());
        AsyncListImage ai = new AsyncListImage(menuCat.getThumbnail(), chosenItemHolder.imageView, context);
        ai.execute();
//        try {
//            InputStream inputStream = context.getAssets().open(menuCat.getThumbnail());
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            chosenItemHolder.imageView.setImageBitmap
//                    (Round.getRoundedCornerImage(bitmap, (int) (100*density),
//                            (int) (70*density), (int) (2*density), (int) (10*density)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        final TextView cartQuantityText = (TextView) v.findViewById(R.id.cartQuantityText);
        cartQuantityText.setText(String.valueOf(menuCat.getCount()));

        ImageButton imageButtonX = (ImageButton) v.findViewById(R.id.imageButtonX);
        imageButtonX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE: {
                            CartActivity.total = 0;

                            ItemActivity.ChosenProduct.remove(menuCat);
                            notifyDataSetChanged();

                            for (int i = 0; i < ItemActivity.ChosenProduct.size(); i++) {
                                double count = ItemActivity.ChosenProduct.get(i).getCount();
                                String priceString = ItemActivity.ChosenProduct.get(i).getPrice();
                                //String noDolarPrice = priceString.substring(1, priceString.length());
                                double price = Double.parseDouble(priceString);
                                CartActivity.total += count * price;
                            }
                            String formattedTotal = String.format("%.2f", CartActivity.total);
                            final TextView totalPriceText = (TextView) ((Activity) context).findViewById(R.id.totalPriceText);
                            totalPriceText.setText(formattedTotal);
                        }
                        break;

                        case DialogInterface.BUTTON_NEGATIVE: {
                            //No button clicked
                        }
                        break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            }
        });

        ImageButton imageButtonPlus = (ImageButton) v.findViewById(R.id.imageButtonPlus);
        imageButtonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(cartQuantityText.getText().toString());
                q++;
                for (int i = 0; i < ItemActivity.ChosenProduct.size(); i++){
                    if (ItemActivity.ChosenProduct.get(i).getName().equals(menuCat.getName())) {
                        ItemActivity.ChosenProduct.get(i).setCount(q);
                        break;
                    }
                }

                String priceString = menuCat.getPrice();
                //String noDolarPrice = priceString.substring(1, priceString.length());
                double price = Double.parseDouble(priceString);
                CartActivity.total += price;

                String formattedTotal = String.format("%.2f", CartActivity.total);
                final TextView totalPriceText = (TextView) ((Activity)context).findViewById(R.id.totalPriceText);
                totalPriceText.setText(formattedTotal);

                notifyDataSetChanged();
            }
        });

        ImageButton imageButtonMinus = (ImageButton) v.findViewById(R.id.imageButtonMinus);
        imageButtonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(cartQuantityText.getText().toString());
                q--;
                if (q != 0) {
                    for (int i = 0; i < ItemActivity.ChosenProduct.size(); i++) {
                        if (ItemActivity.ChosenProduct.get(i).getName().equals(menuCat.getName())) {
                            ItemActivity.ChosenProduct.get(i).setCount(q);
                            break;
                        }
                    }

                    String priceString = menuCat.getPrice();
                    //String noDolarPrice = priceString.substring(1, priceString.length());
                    double price = Double.parseDouble(priceString);
                    CartActivity.total -= price;

                    String formattedTotal = String.format("%.2f", CartActivity.total);
                    final TextView totalPriceText = (TextView) ((Activity)context).findViewById(R.id.totalPriceText);
                    totalPriceText.setText(formattedTotal);
                }
                else { }
                notifyDataSetChanged();
            }
        });
        return v;
    }

    private static class ChosenItemHolder {
        public ImageView imageView;
        public TextView priceView;
        public TextView nameView;
        public TextView descriptionView;
    }
}
