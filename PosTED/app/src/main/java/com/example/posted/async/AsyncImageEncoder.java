package com.example.posted.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.posted.interfaces.Laptop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class AsyncImageEncoder extends AsyncTask<String, Integer, String> {

    public interface Listener {

        void onImageEncoded(String base64str, Laptop laptop);
    }

    private Listener mListener;
    // TODO: implement mProgressBar
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private Laptop mLaptop;

    public AsyncImageEncoder(Listener listener, Laptop laptop) {
        super();
        this.mListener = listener;
        this.mLaptop = laptop;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.mProgressBar = progressBar;
    }

    public void setTextView(TextView textView) {
        this.mTextView = textView;
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
    protected String doInBackground(String... params) {
        if (params == null) {
            return null;
        }
        if (params.length < 2) {
            return null;
        }
        String path = params[0];
        if (path == null) {
            return null;
        }
        String imgName = params[1];
        if (imgName == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            File file = new File(path, imgName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            boolean isDeleted = file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
//        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String result = CustomBase64.encode(byteArray);
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
