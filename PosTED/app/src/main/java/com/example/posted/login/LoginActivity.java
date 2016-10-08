package com.example.posted.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.example.posted.MainActivity;
import com.example.posted.R;
import com.example.posted.admin.AdminMainActivity;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.UsersDatabaseManager;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private User mUser;
    private LoginManager mLoginManager;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        this.mLoginManager = new LoginManager(this);

//        if (this.mLoginManager.isLoggedIn()){
//
//            finish();
//        }

        Intent intent = null;
        if (this.mLoginManager.isLoggedIn()) {
            User currentUser = this.mLoginManager.getLoginUser();
            if (currentUser.getUsername().equalsIgnoreCase(ConstantsHelper.ADMIN_USERNAME)) {
                intent = new Intent(this, AdminMainActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            this.startActivity(intent);
            this.finish();
        }


        // Set up the login form.
        this.mEmailView = (AutoCompleteTextView) this.findViewById(R.id.email);
        this.populateAutoComplete();

        this.mPasswordView = (EditText) this.findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    LoginActivity.this.attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) this.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.attemptLogin();
            }
        });

        this.mLoginFormView = this.findViewById(R.id.login_form);
        this.mProgressView = this.findViewById(R.id.login_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void populateAutoComplete() {
        if (!this.mayRequestContacts()) {
            return;
        }

        this.getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (this.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (this.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(this.mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            LoginActivity.this.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            this.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (this.mAuthTask != null) {
            return;
        }

        // Reset errors.
        this.mEmailView.setError(null);
        this.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = this.mEmailView.getText().toString();
        String password = this.mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !this.isPasswordValid(password)) {
            this.mPasswordView.setError(this.getString(R.string.error_invalid_password));
            focusView = this.mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            this.mEmailView.setError(this.getString(R.string.error_field_required));
            focusView = this.mEmailView;
            cancel = true;
        } else if (!this.isEmailValid(email)) {
            this.mEmailView.setError(this.getString(R.string.error_invalid_email));
            focusView = this.mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            this.showProgress(true);
            this.mAuthTask = new UserLoginTask(email, password, this);
            this.mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);

            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            this.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        this.addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        int a = 5;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        this.mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {

        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;
        private DatabaseManager mDatabaseManager;

        UserLoginTask(String email, String password, Context context) {
            this.mEmail = email;
            this.mPassword = password;
            this.mContext = context;
            this.mDatabaseManager = new DatabaseManager(LoginActivity.this.getApplicationContext());
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            UsersDatabaseManager usersDatabaseManager = null;
            try {
                usersDatabaseManager = new UsersDatabaseManager(this.mDatabaseManager);
                LoginActivity.this.mUser = usersDatabaseManager.getUser(this.mEmail);

                if (LoginActivity.this.mUser.getId() > 0) {
                    // Account exists, check password.
                    if (LoginActivity.this.mUser.getPassword().equals(this.mPassword))
                        return true;
                    else
                        return false;
                } else {
                    LoginActivity.this.mUser.setPassword(this.mPassword);
                    return true;
                }
            } finally {
                if (this.mDatabaseManager != null) {
                    this.mDatabaseManager.close();
                }

            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            LoginActivity.this.mAuthTask = null;
            LoginActivity.this.showProgress(false);

            if (success) {
                if (LoginActivity.this.mUser.getId() > 0) {
                    LoginActivity.this.finish();
                    this.succsesLogin();
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    UsersDatabaseManager usersDatabaseManager = null;
                                    try {
                                        LoginActivity.this.finish();
                                        usersDatabaseManager = new UsersDatabaseManager(UserLoginTask.this
                                                .mDatabaseManager);
                                        LoginActivity.this.mUser = usersDatabaseManager.insertUser(LoginActivity.this
                                                .mUser);
                                        UserLoginTask.this.succsesLogin();
                                        LoginActivity.this.finish();
                                    } finally {
                                        if (UserLoginTask.this.mDatabaseManager != null)
                                            UserLoginTask.this.mDatabaseManager.close();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    LoginActivity.this.mPasswordView.setError(LoginActivity.this.getString(R.string
                                            .error_incorrect_password));
                                    LoginActivity.this.mPasswordView.requestFocus();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                    builder.setMessage(R.string.register_question).setPositiveButton(R.string.yes, dialogClickListener)
                            .setNegativeButton(R.string.no, dialogClickListener);
                    Dialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawableResource(R.color.logo_blue);

                }
            } else {
                LoginActivity.this.mPasswordView.setError(LoginActivity.this.getString(R.string.error_incorrect_password));
                LoginActivity.this.mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            LoginActivity.this.mAuthTask = null;
            LoginActivity.this.showProgress(false);
        }

        public void succsesLogin() {
            LoginActivity.this.mLoginManager.loginUser(LoginActivity.this.mUser);
            if (LoginActivity.this.mLoginManager.getLoginUser().getUsername().equals(ConstantsHelper.ADMIN_USERNAME)) {
                Intent myIntent = new Intent(LoginActivity.this, AdminMainActivity.class);
                LoginActivity.this.startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        }
    }
}

