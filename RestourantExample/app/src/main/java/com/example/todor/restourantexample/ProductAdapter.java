package com.example.todor.restourantexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by todor on 17.07.14.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    public ArrayList<Product> productList;
    private Context context;

    public ProductAdapter(ArrayList<Product> menuCatList, Context ctx) {
        super(ctx, R.layout.menu_row_layout, menuCatList);
        this.productList = menuCatList;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ProductHolder productHolder = new ProductHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.menu_row_layout, null);
            TextView nameView = (TextView) v.findViewById(R.id.name);
            TextView priceView = (TextView) v.findViewById(R.id.price);
            TextView descriptionView = (TextView) v.findViewById(R.id.description);
            ImageView imageView = (ImageView) v.findViewById(R.id.img);
            productHolder.nameView = nameView;
            productHolder.productsCountView = priceView;
            productHolder.descriptionView = descriptionView;
            productHolder.imageView = imageView;
            v.setTag(productHolder);
        } else
            productHolder = (ProductHolder) v.getTag();

        float density = context.getResources().getDisplayMetrics().density;

        Product menuCat = productList.get(position);
        productHolder.nameView.setText(menuCat.getName());
        productHolder.productsCountView.setText(menuCat.getPrice());
        productHolder.descriptionView.setText(menuCat.getDescription());
        AsyncListImage ai = new AsyncListImage(menuCat.getThumbnail(), productHolder.imageView, context);
        ai.execute();
//        try {
//            InputStream inputStream = context.getAssets().open(menuCat.getThumbnail());
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            productHolder.imageView.setImageBitmap(Round.getRoundedCornerImage(bitmap, (int) (100*density),
//                    (int) (70*density), (int) (2*density), (int) (10*density)));
//        }
//        catch (IOException e) {
//            productHolder.imageView.setImageResource(R.drawable.noimage1);
//            e.printStackTrace();
//        }
        return v;
    }

    private static class ProductHolder {
        public ImageView imageView;
        public TextView productsCountView;
        public TextView nameView;
        public TextView descriptionView;
    }
}
