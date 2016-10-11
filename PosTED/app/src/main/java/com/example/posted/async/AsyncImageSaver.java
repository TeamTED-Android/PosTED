package com.example.posted.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.posted.interfaces.Laptop;

public class AsyncImageSaver extends AsyncTask<String, Void, Bitmap> {

    public interface Listener {

        void onImageSaved(Bitmap bitmap, Laptop laptop);
    }

    private Listener mListener;
    private Laptop mLaptop;

    public AsyncImageSaver(Listener listener, Laptop laptop) {
        this.mListener = listener;
        this.mLaptop = laptop;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        if (params == null) {
            return null;
        }
        if (params.length < 1) {
            return null;
        }
        String base64ImgStr = params[0];
        if (base64ImgStr == null) {
            return null;
        }
        byte[] decodedString = Base64.decode(base64ImgStr, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        // TODO fix image dimentions
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap resultBitmap) {
        if (this.mListener != null) {
            this.mListener.onImageSaved(resultBitmap, this.mLaptop);
        }
    }
}
