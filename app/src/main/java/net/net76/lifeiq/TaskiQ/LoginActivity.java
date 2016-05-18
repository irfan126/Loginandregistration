package net.net76.lifeiq.TaskiQ;

/**
 * Created by Administrator on 08/04/2015.
 */
import app.AppConfig;
import app.AppController;

import helper.SQLiteHandler;
import helper.SessionManager;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


public class LoginActivity extends Activity {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    // LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnContactUs;
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button btnLinkToPasswReset;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Show EULA

        new TaskiQEULA(this).show();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnContactUs = (Button) findViewById(R.id.btnContactUs);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnLinkToPasswReset = (Button) findViewById(R.id.btnLinkToPasswordReset);



        // Progress dialog
        pDialog = new ProgressDialog(this);
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("selectTab", "0");
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user

                    if (isNetworkAvaliable(getApplicationContext())) {
                        checkLogin(email, password);}
                    else {Toast.makeText(getApplicationContext(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_SHORT)
                            .show();
                }
            }

        });


        // Contact Us button Click Event
        btnContactUs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                // User is already logged in. Take him to main activity
                Intent intent = new Intent(LoginActivity.this, ContactUsLoginActivity.class);
                startActivity(intent);
                finish();

            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Link to Password Reset Screen
        btnLinkToPasswReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        PasswordResetActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {


                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String firstname = user.getString("firstname");
                        String surname = user.getString("surname");
                        String userID = user.getString("userID");
                        String email = user.getString("email");
                        String country = user.getString("country");
                        String created_at = user.getString("created_at");
                        String password_reset = user.getString("password_reset");


                        // Inserting row in users table

                      if (password_reset.equals("1")){


                          // Launch password change activity
                          Intent intent = new Intent(LoginActivity.this,
                          PasswordChangeActivity.class);

                          intent.putExtra("Key",email);

                          startActivity(intent);
                          finish();


                      }

                     else {

                          // user successfully logged in
                          // Create login session
                         // session.setLogin(true);

                          db.addUser(firstname, surname, userID, email,country, uid, created_at);
                          Log.e(TAG, "userID: " + userID);
                          if (isNetworkAvaliable(getApplicationContext())) {populateReminders(email);
                          }
                          else {Toast.makeText(getApplicationContext(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                          }
                           }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    /**
     * function to verify login details in mysql db
     * */
    private void populateReminders(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Populate Products Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject (response);
                 //   JSONObject result = jObj.getJSONObject("order");
                   // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");
                    String errorMsg = jObj.getString("error_msg");

                    // Check for error node in json
                    if (!error) {


                        JSONArray genreArry = jObj.getJSONArray("order");

                        Log.d(TAG, "genreArry Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite

                         //   JSONObject p = (JSONObject)genreArry.get(h);
                            JSONObject obj = genreArry.getJSONObject(h);

                          //  String uid = genreArry.get(h).getJSONObject("prd_int");

                            String rem_Int =obj.getString("rem_Int");
                            String cat_ID = obj.getString("cat_ID");
                            String Category = obj.getString("Category");
                            String category_Archive = obj.getString("category_Archive");
                            String cat_Desc = obj.getString("cat_Desc");
                            String act_Date = obj.getString("act_Date");
                            String act_Days = obj.getString("act_Days");
                            String act_Rem = obj.getString("act_Rem");
                            String act_Expiry = obj.getString("act_Expiry");
                            String act_Title = obj.getString("act_Title");
                            String Reminder = obj.getString("Reminder");
                            String rem_Archived = obj.getString("rem_Archived");
                            String imageA = obj.getString("imageA");
                            String imageB = obj.getString("imageB");
                            Integer rem_Date = obj.getInt("rem_Date");
                            Integer rem_Expiry = obj.getInt("rem_Expiry");
                            String rem_Notes = obj.getString("rem_Notes");
                            String upload = obj.getString("upload");
                            String userID = obj.getString("userID");
                            String cat_UploadID = obj.getString("cat_UploadID");
                            String rem_UploadID = obj.getString("rem_UploadID");
                            String uploadSum = obj.getString("uploadSum");

                            db.addReminders(rem_Int,cat_ID, Category,category_Archive,cat_Desc, act_Date, act_Days,act_Rem,act_Expiry, act_Title,Reminder,rem_Archived,imageA,imageB,rem_Date,rem_Expiry,rem_Notes,upload,userID,cat_UploadID,rem_UploadID,uploadSum);

                            //user successfully logged in
                            // Create login session Z\
                            session.setLogin(true);

                            // Launch main activity

                           Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("selectTab", "0");
                            startActivity(intent);
                            finish();
                        }
                        hideDialog();
                    }


                    // Check for error node in json
                 else if (error && errorMsg.equals("No categories found")) {

                        Log.d(TAG, "genreArry Response: no reminders " + errorMsg );

                            // User successfully stored in MySQL
                            // Now store the user in sqlite

                            //user successfully logged in
                            // Create login session Z\
                            session.setLogin(true);

                            // Launch main activity

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("selectTab", "0");
                            startActivity(intent);
                            finish();

                        hideDialog();
                    }


                    else {
                        // Error in login. Get the error message
                        String errorMsg1 = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg1, Toast.LENGTH_SHORT).show();
                        hideDialog();
                        db.deleteUsers();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "productList");
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}