package com.example.posted.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
        float screenDensity = Float.parseFloat(params[2]);
        int bound = Integer.parseInt(params[3]);
        Bitmap bitmap = null;
        try {
            File file = new File(path, imgName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            // TODO scale bitmap NEEDS MORE TUNING, using this may cause performance issues on slower devices :(
//            if (bitmap != null) {
//                bitmap = this.scaleBitmap(bitmap, screenDensity, bound);
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, float screenDensity, int bound) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = Math.round((float)bound * screenDensity);

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = xScale <= yScale ? xScale : yScale;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format more attune to the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return scaledBitmap;
    }

    protected void onPostExecute(Bitmap resultBitmap) {
        if (this.mListener != null) {
            this.mListener.onImageLoaded(resultBitmap, this.mExecTime, this.mPosition);
        }
    }
}
