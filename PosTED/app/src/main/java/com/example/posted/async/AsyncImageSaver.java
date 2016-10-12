package com.example.posted.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
        if (params.length < 2) {
            return null;
        }
        String base64ImgStr = params[0];
        if (base64ImgStr == null) {
            return null;
        }
        float screenDensity = Float.parseFloat(params[1]);
        byte[] decodedString = Base64.decode(base64ImgStr, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if (bitmap != null) {
//            bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
            bitmap = this.scaleBitmap(bitmap, screenDensity);
        }
        return bitmap;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, float screenDensity) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // TODO fix image dimentions DONE, V beware of big number, may triggers OutOfMemoryError
        int bounding = Math.round((float) 192 * screenDensity);

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = xScale <= yScale ? xScale : yScale;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format more attune to the ImageView
        // TODO triggers OutOfMemoryError
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return scaledBitmap;
    }

    protected void onPostExecute(Bitmap resultBitmap) {
        if (this.mListener != null) {
            this.mListener.onImageSaved(resultBitmap, this.mLaptop);
        }
    }
}
