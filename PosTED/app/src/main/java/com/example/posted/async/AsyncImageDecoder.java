//package com.example.posted.async;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.util.Base64;
//
//public class AsyncImageDecoder extends AsyncTask<String, Void, Bitmap> {
//
//    public interface Listener {
//
//        void onImageSaved(Bitmap bitmap);
//    }
//
//    private Listener mListener;
//
//    public AsyncImageDecoder(Listener listener) {
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
//        String base64ImgStr = params[0];
//        if (base64ImgStr == null) {
//            return null;
//        }
//        byte[] decodedString = Base64.decode(base64ImgStr, Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        return bitmap;
//    }
//
//    protected void onPostExecute(Bitmap resultBitmap) {
//        if (this.mListener != null) {
//            this.mListener.onImageSaved(resultBitmap);
//        }
//    }
//}
