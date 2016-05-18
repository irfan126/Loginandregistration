package net.net76.lifeiq.TaskiQ;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;

/**
 * Created by Administrator on 31/10/2015.
 */



public class CreateRemYesFragment extends Fragment {
    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = CreateRemYesFragment.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String catID = null;
    private String userID = null;
    private String catTitle = null;
    private String catDesc = null;
    private String actDate = null;
    private String actDays = null;
    private String actDateTitle = null;
    private String Upload = null;
    private String uploadSum = null;
    private ProgressDialog pDialog;
    private TextView categoryTitle;
    private TextView categoryDesc;
    private EditText reminderTitle;
    private TextView ActivationDaysTitle;
    private TextView ActDateTitle;
    private TextView DaysDateText;
    private EditText reminderView;
    private TextView DaysExpiryText;
    private EditText reminderExpiry;
    private EditText reminderNotes;
    private String reminderDateString = null;
    private long reminderDate1 = 0;
    private Button btnCreate;

    ImageView _ImageViewA;
    ImageView _ImageViewB;

    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_REQUEST1 = 2;

    private Boolean imageButton1 = false;
    private Boolean imageButton2 = false;


    private String imageStringA = null;
    private String imageStringB = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        catID = bundle.getString("catID");
        catTitle = bundle.getString("categoryTitle");
        catDesc = bundle.getString("categoryDescription");
        actDate = bundle.getString("activationDate");
        actDays = bundle.getString("actDays");
        actDateTitle = bundle.getString("activationDateTitle");
        Upload = bundle.getString("Upload");
        uploadSum = bundle.getString("uploadSum");



        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        userID = user.get("userID");
        Log.d(TAG, "1234567: " + userID);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.createremyes_fragment, container, false);

        categoryTitle = (TextView) view.findViewById(R.id.CategoryTitle);
        categoryTitle.setText("Category Title: " + catTitle);

        categoryDesc = (TextView) view.findViewById(R.id.CategoryDesc);
        categoryDesc.setText("Category Description: " + catDesc);

        reminderTitle = (EditText) view.findViewById(R.id.ReminderTitle);

        _ImageViewA = (ImageView)view.findViewById(R.id.imageView1);

        _ImageViewA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }

        });

        _ImageViewA.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //your stuff

                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove image?")
                        .setMessage(
                                "Would you like to remove the selected image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                imageButton1 = false;
                                _ImageViewA.setImageResource(R.drawable.add_image_black);


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                return true;
            }
        });

        _ImageViewB = (ImageView)view.findViewById(R.id.imageView2);
        _ImageViewB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST1);

            }

        });

        _ImageViewB.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //your stuff

                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove image?")
                        .setMessage(
                                "Would you like to remove the selected image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                imageButton2 = false;
                                _ImageViewB.setImageResource(R.drawable.add_image_black);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                return true;
            }
        });

        ActDateTitle = (TextView) view.findViewById(R.id.ActivationDateTitle);
        ActDateTitle.setText("Activation Date Title: " + actDateTitle);

        Long remViewLong = (Long.parseLong(actDays))*1000;
        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(remViewLong);

        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date = day+"-"+month_name+"-"+year;

        ActivationDaysTitle = (TextView) view.findViewById(R.id.ActivationDays);
        ActivationDaysTitle.setText("Activation Date: " + date);

        DaysDateText = (TextView) view.findViewById(R.id.DaysRemDateText);

        SpannableString content = new SpannableString(DaysDateText.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        DaysDateText.setText(content);
        DaysDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Days to Reminder");
                alertDialog.setMessage("Enter the number of days you would like the Reminder to appear after the Activation date.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });

        reminderView = (EditText) view.findViewById(R.id.reminderView);


        DaysExpiryText = (TextView) view.findViewById(R.id.DaysRemExpiryText);
        SpannableString content1 = new SpannableString(DaysExpiryText.getText().toString());
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        DaysExpiryText.setText(content1);
        DaysExpiryText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Days to Expiry");
                alertDialog.setMessage("Enter the number of days you would like the Expiry date to be set after the Activation date.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });




        reminderExpiry = (EditText) view.findViewById(R.id.reminderExpiry);


        reminderNotes = (EditText) view.findViewById(R.id.ReminderNotes);

        btnCreate = (Button) view.findViewById(R.id.BtnCreate);


        // Login button Click Event
        btnCreate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String act_Rem = reminderView.getText().toString();

                String act_Expiry = reminderExpiry.getText().toString();



                String Reminder = reminderTitle.getText().toString();

                if (!Reminder.isEmpty()) {

                    if(!act_Expiry.isEmpty() || !act_Expiry.isEmpty()){

                        Long act_RemLong = (Long.parseLong(act_Rem));
                        Long act_ExpiryLong = (Long.parseLong(act_Expiry));
                    if (act_RemLong<7501 &&  act_ExpiryLong<7501) {

                        saveProduct();

                    }


                    else {
                        Toast.makeText(getActivity(),
                                "For either Reminder or Expiry the maximum number of days you can enter is 7500", Toast.LENGTH_SHORT)
                                .show();
                    }}


                    else {
                        Toast.makeText(getActivity(),
                                "Please enter Reminder days and Expiry days", Toast.LENGTH_SHORT)
                                .show();
                    }



                }

                else {
                    Toast.makeText(getActivity(),
                            "Please enter a Reminder Title!", Toast.LENGTH_SHORT)
                            .show();
                }


            }

        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  PICK_IMAGE_REQUEST  && null != data) {

            Log.d(TAG, "Iamge");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageButton1 = true;
            _ImageViewA = (ImageView) getActivity().findViewById(R.id.imageView1);



            Bitmap myImg = BitmapFactory.decodeFile(picturePath);

            _ImageViewA.setImageBitmap(myImg);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
// Must compress the Image to reduce image size to make upload easy
            myImg.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            byte[] bb = stream.toByteArray();
// Encode Image to String
            imageStringA = Base64.encodeToString(bb, Base64.DEFAULT);
        }

        if (requestCode ==  PICK_IMAGE_REQUEST1  && null != data) {

            Log.d(TAG, "Iamge");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageButton2 = true;
            _ImageViewB = (ImageView) getActivity().findViewById(R.id.imageView2);

            Bitmap myImgB = BitmapFactory.decodeFile(picturePath);

            _ImageViewB.setImageBitmap(myImgB);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
// Must compress the Image to reduce image size to make upload easy
            myImgB.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            byte[] bb = stream.toByteArray();
// Encode Image to String
            imageStringB = Base64.encodeToString(bb, Base64.DEFAULT);

        }


    }

    public void saveProduct(){

        String imageA = "0";
        if (imageButton1){
            imageA =imageStringA;
        }

        String imageB = "0";
        if (imageButton2){
            imageB =imageStringB;

        }


        String remTitle = reminderTitle.getText().toString();
        String act_Rem = reminderView.getText().toString();

        String act_Expiry = reminderExpiry.getText().toString();
        String remNotes = reminderNotes.getText().toString();

        Long act_RemLong = (Long.parseLong(act_Rem))*86400;
        Long act_ExpiryLong = (Long.parseLong(act_Expiry))*86400;
        Long act_DaysLong = (Long.parseLong(actDays));
        Long rem_date = act_DaysLong + act_RemLong;
        Long rem_Expiry = act_DaysLong + act_ExpiryLong;

        String red_dateString = Long.toString(rem_date);
        String red_ExpiryString = Long.toString(rem_Expiry);
        Log.d(TAG, "123456: " + rem_date+ " "+actDays+ " "+act_Rem+ " "+act_RemLong+ " "+red_dateString+ " "+userID);

        if (isNetworkAvaliable(getActivity())) { if (catID.equals("New")) {
            createCategory("createCat",email,userID,"0", catTitle, catDesc, actDate,actDays,act_Rem,act_Expiry, actDateTitle, remTitle, red_dateString,red_ExpiryString, remNotes, "0","No Summary",imageA,imageB);
        }
        else {
            Log.d(TAG, "Create Category TAG: " + email + " "+ userID +" "+catID+" "+catTitle+" "+catDesc+" "+actDate+" "+actDays+" "+act_Rem+" "+act_Expiry+" "+actDateTitle+" "+remTitle+" "+red_dateString+" "+red_ExpiryString+" "+remNotes+" "+Upload+" "+uploadSum);
            createCategory("createRem",email,userID,catID, catTitle, catDesc, actDate,actDays,act_Rem,act_Expiry, actDateTitle, remTitle, red_dateString,red_ExpiryString, remNotes, Upload,uploadSum,imageA,imageB);
        }}
        else {Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
        }







    }





    public void createCategory(final String tag,final String email,final String userID, final String catID,final String catTitle, final String catDesc, final String actDate,final String actDays,final String act_Rem,final String act_Expiry, final String actDateTitle,
                               final String remTitle,final String remDate,final String remExpiry,final String remNotes,final String Upload,final String uploadSum,final String imageA,final String imageB){


        String tag_string_req = "req_EditProduct";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Product Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject (response);
                    JSONArray genreArry = jObj.getJSONArray("order");
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.d(TAG, "Create Category JSON Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite

                            //   JSONObject p = (JSONObject)genreArry.get(h);
                            JSONObject obj = genreArry.getJSONObject(h);

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

                            dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder,  rem_Archived,imageA,imageB,  rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                            Intent intent = new Intent(getActivity(), MainActivity.class);

                         // intent.putExtra("catID", cat_ID);
                        // intent.putExtra("Category", Category);
                        // intent.putExtra("actDate", act_Date);
                         // intent.putExtra("userID", userID);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                           // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                           //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           // intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("selectTab", "1");
                            Toast.makeText(getActivity(), "Select Category and then + to add more Reminders", Toast.LENGTH_SHORT).show();
                            startActivity(intent);


                        }
                        hideDialog();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
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
                if(error.getMessage().equals("null")){ Toast.makeText(getActivity(),
                        "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getActivity(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", tag);
                params.put("email", email);
                params.put("userID", userID);
                params.put("cat_ID", catID);
                params.put("Category", catTitle);
                params.put("catDesc", catDesc);
                params.put("actDate", "1");
                params.put("act_Days", actDays);
                params.put("act_Rem", act_Rem);
                params.put("act_Expiry", act_Expiry);
                params.put("actDateTitle", actDateTitle);
                params.put("Reminder", remTitle);
                params.put("imageA", imageA);
                params.put("imageB", imageB);
                params.put("remDate", remDate);
                params.put("remExpiry", remExpiry);
                params.put("remNotes", remNotes);
                params.put("Upload", Upload);
                params.put("uploadSum", uploadSum);


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





}
