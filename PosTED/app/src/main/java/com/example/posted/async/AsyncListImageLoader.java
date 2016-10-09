package com.example.posted.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.example.posted.constants.ConstantsHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AsyncListImageLoader extends AsyncTask<String, Void, Bitmap> {

    public interface Listener {

        void onImageLoaded(Bitmap bitmap, long execTime, int position);
    }

    private Listener mListener;
    private int mPosition;
    private long mExecTime;

    public AsyncListImageLoader(Listener listener, int position) {
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
        if (params.length < 2) {
            return null;
        }
        String path = params[0];
        String imgName = params[1];
        if (imgName == null || path == null || path.equals(ConstantsHelper.NO_IMAGE_TAG)) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            File file = new File(path, imgName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap resultBitmap) {
        if (this.mListener != null) {
            this.mListener.onImageLoaded(resultBitmap, this.mExecTime, this.mPosition);
        }
    }
}
