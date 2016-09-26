package com.example.todor.restourantexample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by todor on 14.07.14.
 */
public class CategoryActivity extends Activity {

    ArrayList<Category> catList = new ArrayList<Category>();
    CategoryAdapter arrayAdpt;

    //@Override
    //public void onBackPressed() {
    //    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    //        @Override
    //        public void onClick(DialogInterface dialog, int which) {
    //            switch (which){
    //                case DialogInterface.BUTTON_POSITIVE: {
    //                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    //                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //                    intent.putExtra("LOGOUT", true);
    //                    startActivity(intent);
    //                }
    //                break;
//
    //                case DialogInterface.BUTTON_NEGATIVE: {
    //                    //No button clicked
    //                }
    //                break;
    //            }
    //        }
    //    };
//
    //    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //    builder.setMessage("Exit app?").setPositiveButton("Yes", dialogClickListener)
    //            .setNegativeButton("No", dialogClickListener).show();
    //}

    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
            if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
                toast = Toast.makeText(this, "Press again if you are sure", Toast.LENGTH_LONG);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("LOGOUT", true);
                startActivity(intent);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        Button buttonCart = (Button)this.findViewById(R.id.buttonCart);
        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intBut = new Intent(CategoryActivity.this, CartActivity.class);
                intBut.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intBut);
            }
        });

        menuAsync ma = new menuAsync();
        ma.execute(LoginActivity.loginID);

        findViewById(R.id.imageButtonBack).setVisibility(View.INVISIBLE);

//        XMLPullParser xmlPullParser = new XMLPullParser();
//
//        try {
//            catList = xmlPullParser.parse(getAssets().open("db.xml"));
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }



        ListView listView = (ListView) findViewById(R.id.listView);
        arrayAdpt = new CategoryAdapter(catList, this);
        listView.setAdapter(arrayAdpt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(), ProductActivity.class);
                in.putExtra(ProductActivity.KEY_PRODUCTS, catList.get(position).getProductList());
                startActivity(in);
            }
        });
    }
    class menuAsync extends AsyncTask<Integer, Void, ArrayList<Category>> {

        @Override
        protected ArrayList<Category> doInBackground(Integer... params) {
            WebService webService = new WebService();
            webService.setMethod("GetData");
            webService.setParameter("UserID", params[0]);
            ArrayList<Category> catListTemp = null;
            try {
                catListTemp = webService.InvokeAsList();
            } catch (WebService.LicensingException e) {
                e.printStackTrace();
            }
            if (catListTemp != null) {
                catList.addAll(catListTemp);
            }
            return catList;
        }

        protected void onPostExecute(ArrayList<Category> result) {
            arrayAdpt.notifyDataSetChanged();
        }
    }
}