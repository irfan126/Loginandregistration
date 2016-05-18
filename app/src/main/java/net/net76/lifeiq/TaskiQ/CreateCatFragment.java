package net.net76.lifeiq.TaskiQ;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import helper.SQLiteHandler;

/**
 * Created by Administrator on 12/10/2015.
 */
public class CreateCatFragment extends Fragment {
    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = CreateCatFragment.class.getSimpleName();

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private ProgressDialog pDialog;
    private Button btnLinkToRegister;
    private Button btnLinkToPasswReset;
    private EditText categoryTitle;
    private EditText categoryDesc;
    private Spinner activationDate;
    private EditText activationDateTitle;
    private Button btnSubmit;
    private TextView ActivationDate1;
    private TextView activationDateTitleExplained;

    private String Category;
    private String catDesc;
    private String actDate;
    private String actDateTitle;

    public static String DATA_Category;
    public static String DATA_CatDesc;
    public static String DATA_ActDate;
    public static String DATA_ActDateTitle;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize all your visual fields
     //   if (savedInstanceState != null) {

       //     categoryTitle.setText(DATA_Category);
         //   categoryDesc.setText(DATA_CatDesc);

           // activationDate.setSelection(savedInstanceState.getInt(DATA_ActDate, 0));
            //activationDateTitle.setText(DATA_ActDateTitle);
       // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");


        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.createcat_fragment, container, false);
       // init(view);

        categoryTitle = (EditText) view.findViewById(R.id.CategoryTitle);
        categoryDesc = (EditText) view.findViewById(R.id.CategoryDesc);
        ActivationDate1 = (TextView) view.findViewById(R.id.activationDate1);

        SpannableString content = new SpannableString(ActivationDate1.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        ActivationDate1.setText(content);
        ActivationDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Activation date");
                alertDialog.setMessage("Activation date will allow you to set Reminders x number of days from a given date. It is mainly used for Categories you intend to Upload.\n\nFor example if you create a Category to Upload which sets Reminders X days after a date another Task iQ user sets then you will need to use the Activation date and title.\n\nNote if you create a Category using the Activation date the Reminders will not show in your Reminders Tab in the homepage. To view the Reminders select the Category from the Category Tab.\n");


                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });

        activationDate = (Spinner)view.findViewById(R.id.ActivationDateSpinner);
        String[] items = new String[]{"No","Yes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        activationDate.setAdapter(adapter);
        activationDateTitle = (EditText) view.findViewById(R.id.ActivationDateTitle);

        activationDateTitleExplained = (TextView) view.findViewById(R.id.activationDateTitleExplained);

        SpannableString content1 = new SpannableString(activationDateTitleExplained.getText().toString());
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        activationDateTitleExplained.setText(content1);
        activationDateTitleExplained.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Title for Activation date");
                alertDialog1.setMessage("Set a title for the Activation date. If you upload the Category it will help a other users to set a their Activation date when downloading.\n\nNote if you select No to Activation Date required the title can be left blank.\n");


                alertDialog1.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.show();
            }

        });

        btnSubmit = (Button) view.findViewById(R.id.BtnSubmit);

        // Login button Click Event
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Category = categoryTitle.getText().toString();
                catDesc = categoryDesc.getText().toString();
                actDate = String.valueOf(activationDate.getSelectedItem());
                actDateTitle = activationDateTitle.getText().toString();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (!Category.isEmpty()) {

                    if (actDate.equals("No")) {

                        CreateRemNoFragment pm_fragment = new CreateRemNoFragment();


                        Intent intent = new Intent(getActivity(), CreateRemNoActivity.class);

                        intent.putExtra("catID", "New");
                        intent.putExtra("categoryTitle", Category);
                        intent.putExtra("categoryDescription", catDesc);
                        intent.putExtra("activationDate", actDate);
                        intent.putExtra("activationDateTitle", actDateTitle);
                        startActivity(intent);


                    }

                    else if (actDate.equals("Yes") && !actDateTitle.isEmpty()) {

                        Intent intent = new Intent(getActivity(), ActDateActivity.class);
                        intent.putExtra("categoryTitle", Category);
                        intent.putExtra("categoryDescription", catDesc);
                        intent.putExtra("activationDate", actDate);
                        intent.putExtra("activationDateTitle", actDateTitle);
                        startActivity(intent);
                    }

                    else {
                        Toast.makeText(getActivity(),
                                "Activation date set to Yes. Please enter a title for Activation Date", Toast.LENGTH_SHORT)
                                .show();
                    }
                }


                    else {
                        Toast.makeText(getActivity(),
                                "Please enter a Category Title!", Toast.LENGTH_SHORT)
                                .show();
                    }




            }

        });


        return view;



    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
      //  outState.putString(DATA_Category, Category);
       // outState.putString(DATA_CatDesc,catDesc);
       // outState.putInt(DATA_ActDate, activationDate.getSelectedItemPosition());
       // outState.putString(DATA_ActDateTitle,actDateTitle);
    }

    @Override
    public void onResume() {
        super.onResume();


    }
    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("To add a reminder under a existing Category on the homepage, select the Category Tab, the Category and then +.\n\n");
        builder.append("Activation date will allow you to set Reminders x number of days from a given date. It is mainly used for Categories you intend to Upload.\n\n");
        builder.append("For example if you create a Category to Upload which sets Reminders X days after a date another Task iQ user sets then you will need to use the Activation date and title.\n\n");
        builder.append("Note if you create a Category using the Activation date the Reminders will not show in your Reminders Tab in the homepage. To view the Reminders select the Category from the Category Tab.\n\n");


        new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage(builder)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_addcat, menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_info:
                // Information
                info();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
