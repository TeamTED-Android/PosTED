package com.example.todor.restourantexample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        if (getIntent().getBooleanExtra("LOGOUT", false)) {
            finish();
        }

        final EditText editText2 = (EditText)this.findViewById(R.id.editText2);
        final EditText editText = (EditText)this.findViewById(R.id.editText);
        Button buttonLogin = (Button)this.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsync la = new loginAsync();
                la.execute(editText2.getText().toString(), editText.getText().toString());
            }
        });
    }

    class loginAsync extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            WebService webService = new WebService();
            webService.setMethod("Login");
            webService.setParameter("Username", params[0]);
            webService.setParameter("Password", params[1]);
            int result = -1;
            try {
                result = webService.InvokeAsInt();
            } catch (WebService.LicensingException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(Integer result) {
            if (result == -1) {
                Toast.makeText(LoginActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intBut = new Intent(LoginActivity.this, CategoryActivity.class);
                startActivity(intBut);
            }
            loginID = result;
        }
    }

    public static int loginID;
}

