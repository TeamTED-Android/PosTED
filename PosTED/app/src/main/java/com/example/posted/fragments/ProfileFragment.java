package com.example.posted.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posted.R;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.UsersDatabaseManager;
import com.example.posted.login.LoginManager;
import com.example.posted.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProfileFragment  extends Fragment implements View.OnClickListener{
    private DatabaseManager mDatabaseManager;
    private UsersDatabaseManager mUserDatabaseManager;
    private LoginManager mLoginManeger;
    private Context mContext;
    private User mCurrentUser;

    private TextView mUsername;
    private EditText mOldPass;
    private EditText mNewPass;
    private Button mUpdatePasswordButoon;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mDatabaseManager = new DatabaseManager(context);
        this.mUserDatabaseManager = new UsersDatabaseManager(this.mDatabaseManager);
        this.mLoginManeger = new LoginManager(context);
        this.mCurrentUser = this.mLoginManeger.getLoginUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        this.mUsername = (TextView) view.findViewById(R.id.profile_username);
        this.mOldPass = (EditText) view.findViewById(R.id.profile_old_pass);
        this.mNewPass = (EditText) view.findViewById(R.id.profile_new_pass);
        this.mUpdatePasswordButoon = (Button) view.findViewById(R.id.profile_update_pass);
        this.mUpdatePasswordButoon.setOnClickListener(this);

        this.mUsername.setText(this.mCurrentUser.getUsername());
        return view;
    }

    @Override
    public void onClick(View v) {
        String password = this.encryptPassword(this.mOldPass.getText().toString());
        if (password.equals(this.mCurrentUser.getPassword())){
            if (this.mNewPass.getText().toString().length() > ConstantsHelper.MIN_PASSWORD_LENGTH){
                String newPass = this.encryptPassword(this.mNewPass.getText().toString());
                this.mCurrentUser.setPassword(newPass);
                this.mUserDatabaseManager.updateUserPassword(this.mCurrentUser);
                Toast.makeText(this.mContext,this.getResources().getString(R.string.password_updated),Toast.LENGTH_SHORT).show();
                this.mLoginManeger.logoutUser();
                this.mLoginManeger.loginUser(this.mCurrentUser);
            } else {
                this.mNewPass.setError(this.getResources().getString(R.string.password_short));
                this.mNewPass.requestFocus();
            }

        } else {
            this.mOldPass.setError(this.getResources().getString(R.string.password_mismatch));
            this.mOldPass.requestFocus();
        }
    }

    private String encryptPassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
