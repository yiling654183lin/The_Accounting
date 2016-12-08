package com.example.hsin_tingchung.htc;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;;import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class BudgetView extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //新增DB write
    private static String DATABASE_TABLE_IN = "BudgetItem";
    private static String DATABASE_TABLE_OUT = "Budget";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //新增DB read
    private SQLiteDatabase dbget;
    private TextView output;

    private Button bgtToNewButton;

    //New Button
    LinearLayout layout;

    //private Button btn;
    private int CurrentButtonNumber = 0;
    private String name;
    private int period, amount;

    public BudgetView() {
        // Required empty public constructor
    }

    public static BudgetView newInstance(String param1, String param2) {
        BudgetView fragment = new BudgetView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View v = inflater.inflate(R.layout.fragment_budget_view, container, false);
        getViews(v);
        setButtonEvents(v);

        createWritableDB();
        createReadableDB();


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        String strdate = df.format(cal.getTime());

        //Toast.makeText(getActivity(),"Today:" + strdate, //Toast.LENGTH_SHORT).show();


        String[] colNames = new String[]{"BI_UID, name, E_date, BGI.amount"};
        String table=DATABASE_TABLE_IN+
                " BG INNER JOIN " +
                DATABASE_TABLE_OUT+" BGI " +
                "ON BG.B_UID = BGI.B_UID";
        Cursor c = dbget.query(table, colNames, null, null, null, null, null);
        String str = "";
        //Toast.makeText(getActivity(),"BudgetView : " + c.getCount(), //Toast.LENGTH_SHORT).show();
        c.moveToFirst();

        /*
        String[] colNames=new String[]{"B_UID","name","period","amount"};
        String str = "";
        Cursor c = dbget.query(DATABASE_TABLE_OUT, colNames,null, null, null, null,null);
        */


        // display data
        for (int i = 0; i < colNames.length; i++)
            str += colNames[i] + "\t\t";
        str += "\n";
        //output.setText(str);
        c.moveToFirst();  // first one

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        for (int i = 0; i < c.getCount(); i++) {
            Calendar c2 = Calendar.getInstance();
            try {
                c2.setTime(df2.parse(c.getString(2)));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();}
            long dayDiff = (c2.getTimeInMillis() - cal.getTimeInMillis());
            ////Toast.makeText(getActivity(),"Diff : " + c.getInt(0) +" "+c.getString(1)+" "+c.getString(2)+" "+ dayDiff, //Toast.LENGTH_SHORT).show();
            if(dayDiff>=0) {
                str = "";
                str += c.getString(1) + "\t\t";

                Button btn = new Button(getActivity());
                CurrentButtonNumber = c.getInt(0);
                btn.setId(CurrentButtonNumber);
                btn.setText(str);
                btn.setBackgroundResource(R.drawable.button_normal_larger);
                btn.setHeight(10);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpLayout_ItemBudget(v);
                    }
                });
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(1100, 200);
                layout.addView(btn, param);
                c.moveToNext();  // last one
            }
        }

        c.close();
        dbget.close();
        // Inflate the layout for this fragment
        return v;
    }

    private void getViews(View v) {
        bgtToNewButton = (Button) v.findViewById(R.id.BGT_to_New);

        //Create new button layout scrollView
        layout = (LinearLayout) v.findViewById(R.id.budget_output);
        output = (TextView) v.findViewById(R.id.title);
    }

    private void setButtonEvents(View v) {
        bgtToNewButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {jumpLayout_NewBudget();
            }
        });
    }

    private void createWritableDB() {
        //produce DB writable object
        dbhelper = new DBhelper(getActivity());
        new Thread(){
            public void run(){
                db = dbhelper.getWritableDatabase();
            }
        }.start();
    }

    private void createReadableDB(){
        //produce DB readable object
        DBhelper dbhelper = new DBhelper(getActivity());
        dbget = dbhelper.getReadableDatabase();
    }

    public void jumpLayout_NewBudget(){

        dbget.close();
        db.close();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        BudgetNew nextFrag = new BudgetNew();

        fragmentTransaction.replace(R.id.fragment_container, nextFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    public void jumpLayout_ItemBudget(View v){

        dbget.close();
        db.close();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        BudgetItem nextFrag = new BudgetItem();
        Bundle bundle = new Bundle();
        bundle.putString("ID", Integer.toString(v.getId()));
        nextFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, nextFrag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    // TODO: Rename method, update argument and hook method into UI event
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
}
