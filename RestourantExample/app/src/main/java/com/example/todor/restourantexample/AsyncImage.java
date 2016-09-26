package com.example.todor.restourantexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by todor on 14.08.14.
 */
public class AsyncImage extends AsyncTask<Void, Void, Bitmap> {

    private String imgString;
    private ImageView imageView;
    private Context context;
    private static Map<String, Bitmap> hashMap = new HashMap<>();

    public AsyncImage(String imgString, ImageView imageView, Context context) {
        this.imgString = imgString;
        this.imageView = imageView;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        if (hashMap.containsKey(this.imgString)) {
            bitmap = hashMap.get(this.imgString);
        }
        else {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(this.imgString).getContent());
                hashMap.put(this.imgString, bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap resultBitmat) {
        double density = this.context.getResources().getDisplayMetrics().density;
        double screen = this.context.getResources().getDisplayMetrics().widthPixels;

        if (resultBitmat != null) {
            this.imageView.setImageBitmap(Round.getRoundedCornerImage(resultBitmat, (int) (screen - 20*density),
                            (int) (230*density), (int) (5*density), (int) (20*density)));
        }
    }
}
