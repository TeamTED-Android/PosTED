package com.example.posted.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Todor Ilchev on 2016-10-03.
 */

public class AsyncImageEncoder extends AsyncTask<Bitmap, Integer, String> {

    public interface Listener {

        void onImageEncoded(String base64str);
    }

    private Listener mListener;
    // TODO: implement progressBar
    private ProgressBar progressBar;
    private TextView textView;

    public AsyncImageEncoder(Listener listener) {
        super();
        this.mListener = listener;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.progressBar != null) {
            this.progressBar.setProgress(values[0]);
        }
        if (this.textView != null) {
            this.textView.setText(Integer.toString(values[0]));
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
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.GONE);
        }
        if (result != null) {
            this.mListener.onImageEncoded(result);
        }
    }
}
