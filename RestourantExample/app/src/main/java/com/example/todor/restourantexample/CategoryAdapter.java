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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by todor on 17.07.14.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {

    public ArrayList<Category> categoryList;
    private Context context;

    public CategoryAdapter(ArrayList<Category> menuCatList, Context ctx) {
        super(ctx, R.layout.menu_row_layout, menuCatList);
        this.categoryList = menuCatList;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        CategoryHolder categoryHolder = new CategoryHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.menu_row_layout, null);
            TextView nameView = (TextView) v.findViewById(R.id.name);
            TextView productsCountView = (TextView) v.findViewById(R.id.price);
            ImageView imageView = (ImageView) v.findViewById(R.id.img);
            categoryHolder.nameView = nameView;
            categoryHolder.productsCountView = productsCountView;
            categoryHolder.imageView = imageView;
            v.setTag(categoryHolder);
        }
        else {
            categoryHolder = (CategoryHolder) v.getTag();
        }

        float density = context.getResources().getDisplayMetrics().density;

        Category menuCat = categoryList.get(position);
        categoryHolder.nameView.setText(menuCat.getName());
        categoryHolder.productsCountView.setText(menuCat.getPoductsCount());
        AsyncListImage ai = new AsyncListImage(menuCat.getThumbnail(), categoryHolder.imageView, context);
        ai.execute();
//        try {
//            InputStream inputStream = context.getAssets().open(menuCat.getThumbnail());
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            categoryHolder.imageView.setImageBitmap(Round.getRoundedCornerImage(bitmap, (int) (100*density),
//                    (int) (70*density), (int) (2*density), (int) (10*density)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return v;
    }
    
    private static class CategoryHolder {
        public ImageView imageView;
        public TextView productsCountView;
        public TextView nameView;
    }
}
