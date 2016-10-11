//package com.example.posted.async;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
//public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {
//
//    public interface Listener {
//
//        void onImageLoaded(Bitmap bitmap);
//    }
//
//    private Listener mListener;
//
//    public AsyncImageLoader(Listener listener) {
//        this.mListener = listener;
//    }
//
//    @Override
//    protected Bitmap doInBackground(String... params) {
//        if (params == null) {
//            return null;
//        }
//        if (params.length < 1) {
//            return null;
//        }
//        String path = params[0];
//        if (path == null) {
//            return null;
//        }
//        String imgName = params[1];
//        if (imgName == null) {
//            return null;
//        }
//        Bitmap bitmap = null;
//        try {
//            File file = new File(path, imgName);
//            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    protected void onPostExecute(Bitmap resultBitmap) {
//        if (this.mListener != null) {
//            this.mListener.onImageLoaded(resultBitmap);
//        }
//    }
//}
