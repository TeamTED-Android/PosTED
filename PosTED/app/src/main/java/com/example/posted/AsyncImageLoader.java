package com.example.posted;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {

    public interface Listener {

        void onImageLoaded(Bitmap bitmap, long execTime, int position);
    }

    private Listener mListener;
    private long mExecTime;
    private int mPosition;

    public AsyncImageLoader(Listener listener, int position) {
        this.mListener = listener;
        this.mPosition = position;
    }

    @Override
    protected void onPreExecute() {
        this.mExecTime = System.currentTimeMillis();
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
        return bitmap;
    }

    protected void onPostExecute(Bitmap resultBitmap) {
        if (this.mListener != null) {
            this.mListener.onImageLoaded(resultBitmap, this.mExecTime, this.mPosition);
        }
    }
}
