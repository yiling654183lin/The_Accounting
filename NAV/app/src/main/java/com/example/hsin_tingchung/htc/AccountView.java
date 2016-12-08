package com.example.hsin_tingchung.htc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;



public class AccountView extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    //new DB write
    private static String DATABASE_TABLE_IN = "Now";
    private static String DATABASE_TABLE_OUT = "Account";
    private static String DATABASE_TABLE_STAG = "A_tag";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //new DB read
    private SQLiteDatabase dbget;
    private TextView output, output_total;

    //New Button
    LinearLayout layout;
    Button newItem;

    //private Button btn;
    private int CurrentButtonNumber;
    private TextView time, amount, content;

    public AccountView() {
        // Required empty public constructor
    }


    public static AccountView newInstance(String param1, String param2) {
        AccountView fragment = new AccountView();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_view, container, false);

        getViews(v);

        createReadableDB();
        createWritableDB();
        //variables
        int total=0;
        String[] count = new String[]{"total"};
        //print out total money
        Cursor am = dbget.query("Now", count,null,null,null,null,null);
        am.moveToFirst();
        for (int i = 0; i < am.getCount(); i++) {
            total=total+am.getInt(0);
            am.moveToNext();  //move next
        }
        am.close();
        output_total.setText(Integer.toString(total));

        updateTable();

        //For test
        Button alertDialog;
        alertDialog = (Button) v.findViewById(R.id.check);
        alertDialog.setOnClickListener(buttonListener);
        Button alertDialog1;
        alertDialog1 = (Button) v.findViewById(R.id.check_now);
        alertDialog1.setOnClickListener(buttonListener);
        //For test

        //For new an Account/income item //for test
        newItem = (Button) v.findViewById(R.id.new_item);
        newItem.setOnClickListener(buttonListener);

        //db.close();
        // Inflate the layout for this fragment
        return v;
    }

    private void getViews(View v){
        output = (TextView)v.findViewById(R.id.act_title);
        output_total = (TextView)v.findViewById(R.id.now_money);
        //Create new button layout scrollView
        layout = (LinearLayout)v.findViewById(R.id.act_output_space);
    }

    private void createReadableDB(){
        //read data
        DBhelper dbhelper = new DBhelper(getActivity());
        dbget = dbhelper.getReadableDatabase();
    }

    private void createWritableDB(){
        //produce DB writable object
        dbhelper = new DBhelper(getActivity());
        new Thread(){
            public void run(){
                db = dbhelper.getWritableDatabase();
            }
        }.start();
    }

    private void updateTable(){
        layout.removeAllViews();

        /* 手機輸入的資料 -----------------------------------*/
        // /List of the amount data
        String[] colNames=new String[]{"UID","Time","Money","Memo"};
        String str = "";
        Cursor c = dbget.query(DATABASE_TABLE_OUT, colNames,null, null, null, null,null);

        // display name
        for (int i = 0; i < colNames.length; i++)
            str += colNames[i] + "\t\t";
        str += "\n";
        //output.setText(str);
        c.moveToFirst();  // first one
        for (int i = 0; i < c.getCount(); i++) {
            str="第";
            str += c.getString(c.getColumnIndex(colNames[0])) + "筆\t\t";
            str += c.getString(1) + "\t\t";
            str += c.getString(2) + "元\t\t";
            //str += c.getString(3);//+ "\n";

            Button btn = new Button(getActivity());
            CurrentButtonNumber=c.getInt(0);

            btn.setId(CurrentButtonNumber);
            btn.setText(str);

            String[] STcolNames = new String[]{"T_UID"};
            String where = "A_UID = " + c.getString(c.getColumnIndex(colNames[0]));
            Cursor st =  dbget.query(DATABASE_TABLE_STAG, STcolNames, where, null, null, null,null);

            int  stCount = 0;
            stCount = st.getCount();
            if(stCount > 0){
                if(c.getInt(2)<=0)
                    btn.setBackgroundResource(R.drawable.button_red);
                else
                    btn.setBackgroundResource(R.drawable.button_green);
            } else {
                btn.setBackgroundResource(R.drawable.button_blue);
            }

            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpLayout_AccountItem(v);

                }
            });
            LinearLayout.LayoutParams param =new LinearLayout.LayoutParams(1200,200);
            layout.addView(btn, param);
            c.moveToNext();
        }
        c.close();

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //For test
    private View.OnClickListener buttonListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.check:
                    new AlertDialog.Builder(getActivity())
                            .setTitle("New Expenses")
                            .setMessage("You have 1 new expenses record")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    break;
                case R.id.check_now:
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Alert")
                            .setMessage("The amount inside is below the limit you set")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setNegativeButton("Remind Me later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    break;
                case R.id.new_item:
                    final View item2 = LayoutInflater.from(getActivity()).inflate(R.layout.new_account_item,null);
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.new_account_item)
                            .setView(item2)
                            .setPositiveButton(R.string.ok , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText newDate = (EditText) item2.findViewById(R.id.new_account_date);
                                    EditText newAmount = (EditText) item2.findViewById(R.id.new_account_amount);
                                    ContentValues ntg = new ContentValues();
                                    ntg.put("Time",newDate.getText().toString());
                                    ntg.put("Money", newAmount.getText().toString());
                                    ntg.put("Memo", "Account");
                                    db.insert(DATABASE_TABLE_OUT, null, ntg);
                                    //Toast.makeText(getActivity().getApplicationContext(), newDate.getText().toString()+", "+newAmount.getText().toString(), //Toast.LENGTH_SHORT).show();
                                    updateTable();
                                }
                            })
                            .setNegativeButton(R.string.cancel , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    break;
            }
        }
    };

    public void jumpLayout_AccountItem(View v){

        dbget.close();
        db.close();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        AccountItem nextFrag = new AccountItem();
        Bundle bundle = new Bundle();
        bundle.putString("ID", Integer.toString(v.getId()));
        nextFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, nextFrag);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
