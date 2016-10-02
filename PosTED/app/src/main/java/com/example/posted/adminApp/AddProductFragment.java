package com.example.posted.adminApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.posted.R;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static android.app.Activity.RESULT_OK;


public class AddProductFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText model;
    private EditText price;
    private EditText hdd;
    private EditText ram;
    private EditText displaySize;
    private EditText processor;
    private EditText videoCard;
    private EditText currency;
    private Button addImageButton;
    private Button addProductButton;
    private Button cancelButton;
    private Context context;
    private String imageAsString;




    private DatabaseManager databaseManager;
    private LaptopsDatabaseManager laptopsDatabaseManager;

    public AddProductFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.databaseManager = new DatabaseManager(context);
        this.laptopsDatabaseManager = new LaptopsDatabaseManager(this.databaseManager);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_add_product_fragment, container, false);
        this.model = (EditText) view.findViewById(R.id.laptopModelEditText);
        this.price = (EditText) view.findViewById(R.id.priceEditText);
        this.hdd = (EditText) view.findViewById(R.id.hddEditText);
        this.ram = (EditText) view.findViewById(R.id.ramEditText);
        this.displaySize = (EditText) view.findViewById(R.id.displaySizeEditText);
        this.processor = (EditText) view.findViewById(R.id.processorEditText);
        this.videoCard = (EditText) view.findViewById(R.id.videoCardEditText);
        this.currency = (EditText) view.findViewById(R.id.currencyEditText);

        this.addImageButton = (Button) view.findViewById(R.id.addImageButton);
        this.addProductButton = (Button) view.findViewById(R.id.addProductButton);
        this.cancelButton = (Button) view.findViewById(R.id.cancelButton);

        this.addProductButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.addImageButton.setOnClickListener(this);
        this.imageAsString = "";
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addProductButton) {
            if (this.checkInputInfo()) {
                HashMap<String, String> laptopData = new LinkedHashMap<String, String>();
              //  laptopData.put(ConstantsHelper.ID_COLUMN,String.valueOf(50));
                laptopData.put(ConstantsHelper.MODEL_COLUMN, this.model.getText().toString());
                laptopData.put(ConstantsHelper.RAM_COLUMN, this.ram.getText().toString());
                laptopData.put(ConstantsHelper.HDD_COLUMN, this.hdd.getText().toString());
                laptopData.put(ConstantsHelper.PROCESSOR_COLUMN, this.processor.getText().toString());
                laptopData.put(ConstantsHelper.VIDEO_CARD_COLUMN, this.videoCard.getText().toString());
                laptopData.put(ConstantsHelper.DISPLAY_COLUMN, this.displaySize.getText().toString());
                laptopData.put(ConstantsHelper.CURRENCY_COLUMN, this.currency.getText().toString());
                laptopData.put(ConstantsHelper.PRICE_COLUMN, this.price.getText().toString());
                laptopData.put(ConstantsHelper.IMAGE_COLUMN, this.imageAsString);
                this.laptopsDatabaseManager.insertRecord(laptopData);
                Toast.makeText(context,"Laptop added to database",Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.cancelButton) {

        } else if (v.getId() == R.id.addImageButton) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);
                Toast.makeText(this.context, "Image getted", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                this.imageAsString = Base64.encodeToString(b, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkInputInfo() {

        if (this.model == null || this.model.getText().toString().equals("")) {
            this.model.setError("Laptop model cannot be empty");
            this.model.requestFocus();
            return false;
        } else if (this.hdd == null || this.hdd.getText().toString().equals("")) {
            this.hdd.setError("HDD cannot be empty");
            this.hdd.requestFocus();
            return false;
        } else if (this.displaySize == null || this.displaySize.getText().toString().equals("")) {
            this.displaySize.setError("Display size cannot be empty");
            this.displaySize.requestFocus();
            return false;
        } else if (this.processor == null || this.processor.getText().toString().equals("")) {
            this.processor.setError("Processor cannot be empty");
            this.processor.requestFocus();
            return false;
        } else if (this.videoCard == null || this.videoCard.getText().toString().equals("")) {
            this.videoCard.setError("Video card cannot be empty");
            this.videoCard.requestFocus();
            return false;
        } else if (this.currency == null || this.currency.getText().toString().equals("")) {
            this.currency.setError("Currency cannot be empty");
            this.currency.requestFocus();
            return false;
        } else if (this.price == null || this.price.getText().toString().equals("")) {
            this.price.setError("Price cannot be empty");
            this.price.requestFocus();
            return false;
        }

        return true;
    }

}
