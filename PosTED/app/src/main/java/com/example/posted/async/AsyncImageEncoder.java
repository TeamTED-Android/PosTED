package com.example.posted.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.posted.interfaces.Laptop;

import java.io.ByteArrayOutputStream;


public class AsyncImageEncoder extends AsyncTask<Bitmap, Integer, String> {

    public interface Listener {

        void onImageEncoded(String base64str, Laptop laptop);
    }

    private Listener mListener;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private Laptop mLaptop;

    public AsyncImageEncoder(Listener listener, Laptop laptop) {
        super();
        this.mListener = listener;
        this.mLaptop = laptop;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.mProgressBar != null) {
            this.mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.mProgressBar != null) {
            this.mProgressBar.setProgress(values[0]);
        }
        if (this.mTextView != null) {
            this.mTextView.setText(Integer.toString(values[0]));
        }
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        if (params == null) {
            return null;
        }
        if (params.length < 1) {
            return null;
        }
        Bitmap bitmap = params[0];
        if (bitmap == null) {
            return null;
        }
        this.publishProgress(2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.publishProgress(4);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        this.publishProgress(6);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        this.publishProgress(8);
        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        this.publishProgress(10);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.mProgressBar != null) {
            this.mProgressBar.setVisibility(View.GONE);
        }
        if (result != null) {
            this.mListener.onImageEncoded(result, this.mLaptop);
        }
    }
}
