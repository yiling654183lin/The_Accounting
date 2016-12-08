package com.example.hsin_tingchung.htc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class BudgetItemEdit extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Bundle get
    private String ID;

    //新增DB write
    private static String DATABASE_TABLE_BUD = "Budget";
    private static String DATABASE_TABLE_BGI = "BudgetItem";
    private static String DATABASE_TABLE_TAG = "Tag";
    private static String DATABASE_TABLE_BTAG = "B_tag";
    private static String DATABASE_TABLE_SPT = "Account";
    private static String DATABASE_TABLE_STAG = "A_tag";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //新增DB read
    private SQLiteDatabase dbget;

    //Layout Items
    private TextView name;
    private TextView amount;
    private TextView period;
    private GridLayout tag_output;
    private Button save;
    private Button cancel;

    private String str;
    private int CurrentButtonNumber;
    private int[] clickCount;

    //Values
    private String NAME;
    private int Amount;
    private int Period;

    private OnFragmentInteractionListener mListener;

    public BudgetItemEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BudgetItemEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetItemEdit newInstance(String param1, String param2) {
        BudgetItemEdit fragment = new BudgetItemEdit();
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
            ID = getArguments().getString("ID");
            //Toast.makeText(getActivity(), "BudgetItemEdit:"+ID, //Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=null;
        v=inflater.inflate(R.layout.fragment_budget_item_edit, container, false);
        // Inflate the layout for this fragment
        createReadableDB();
        createWritableDB();
        getViews(v);
        name.setText("Budget Item ID:"+ID);

        String[] colNames=new String[]{"BI_UID","E_date","BGI.amount","name"};
        String table=DATABASE_TABLE_BUD+
                " BG INNER JOIN " +
                DATABASE_TABLE_BGI+" BGI " +
                "ON BG.B_UID = BGI.B_UID";
        String where = "BI_UID =" + ID;
        Cursor c = dbget.query(table, colNames, where, null, null, null,null);
        c.moveToFirst();
        name.setText(c.getString(3));
        period.setText(c.getString(1));
        amount.setText(c.getString(2));
        c.close();

        colNames = new String[]{"T_UID","T_Name"};
        c = dbget.query(DATABASE_TABLE_TAG, colNames,null, null, null, null,null);
        c.moveToFirst();


        clickCount = new int[c.getCount()];

        for (int i = 0; i < c.getCount(); i++) {
            str = "";
            str += c.getString(1) + "\t\t";
            clickCount[i] = 0;


            //////////New tag button
            final Button btn = new Button(getActivity());
            CurrentButtonNumber=Integer.parseInt(c.getString(c.getColumnIndex(colNames[0])));
            btn.setId(CurrentButtonNumber);
            btn.setText(str);

            String[] BTcolNames = new String[]{"T_UID"};
            where = "BI_UID = " + ID + " AND T_UID = " + c.getString(0);
            final String btTag = c.getString(0);
            Cursor bt =  dbget.query(DATABASE_TABLE_BTAG, BTcolNames, where, null, null, null,null);
            final int btCount = bt.getCount();

            if (btCount > 0) {
                btn.setBackgroundResource(R.drawable.button_budget_tag_selected);
            } else {
                btn.setBackgroundResource(R.drawable.button_budget_tag_unselect);
            }

            clickCount[btn.getId() - 1] = btCount;

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(clickCount[v.getId() - 1] == 0){
                        v.setBackgroundResource(R.drawable.button_budget_tag_selected);
                        ContentValues nct = new ContentValues();
                        nct.put("BI_UID", ID);
                        nct.put("T_UID", btTag);
                        db.insert(DATABASE_TABLE_BTAG, null, nct);
                        clickCount[v.getId() - 1] = 1;

                    } else {
                        v.setBackgroundResource(R.drawable.button_budget_tag_unselect);
                        db.delete(DATABASE_TABLE_BTAG, "BI_UID = " + ID + " AND T_UID = " + btTag, null);
                        clickCount[v.getId() - 1] = 0;

                    }
                }
            });
            LinearLayout.LayoutParams param =new LinearLayout.LayoutParams(500,200);
            tag_output.addView(btn, param);

            bt.close();

            c.moveToNext();  // last one
        }
        c.close();

        return v;
    }

    private void getViews(View v){
        name=(TextView)v.findViewById(R.id.BEI_name);
        amount=(TextView)v.findViewById(R.id.BEI_money);
        period=(TextView)v.findViewById(R.id.BEI_period);
        tag_output=(GridLayout)v.findViewById(R.id.BEI_tag_output);
        save=(Button)v.findViewById(R.id.BEI_save);
        cancel=(Button)v.findViewById(R.id.BEI_cancel);
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

    private void createReadableDB(){
        //produce DB readable object
        DBhelper dbhelper = new DBhelper(getActivity());
        dbget = dbhelper.getReadableDatabase();
    }

    private void setEvents(View v){
        //doneButton.setOnClickListener(buttonListener);
        save.setOnClickListener(buttonListener);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private View.OnClickListener buttonListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.BEI_cancel:
                    SwitchToBudgetItem();
                    break;
                case R.id.BEI_save:
                    if (  ( !amount.getText().toString().equals("")) && ( !name.getText().toString().equals("") ) ) {
                    NAME = name.getText().toString();
                    Amount = Integer.parseInt(amount.getText().toString());
                    Period = Integer.parseInt(period.getText().toString());
                    //Toast.makeText(getActivity(),NAME, //Toast.LENGTH_SHORT).show();
                    ContentValues nbg = new ContentValues();
                    nbg.put("name",NAME);
                    nbg.put("period",Period);
                    nbg.put("amount",Amount);
                    db.insert(DATABASE_TABLE_BUD, null, nbg);

                    String[] colNames=new String[]{"B_UID"};
                    String where = "name='"+ NAME +"'";
                    Cursor c = dbget.query(DATABASE_TABLE_BUD, colNames, where, null, null, null,null);
                    c.moveToFirst();
                    String bid=c.getString(0);
                    c.close();

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    String str = df.format(cal.getTime());
                    cal.add(Calendar.DAY_OF_MONTH, Period);
                    ContentValues nbgi = new ContentValues();
                    nbgi.put("amount", Amount);
                    nbgi.put("S_date", str);
                    cal.add(Calendar.DAY_OF_MONTH, Period);
                    String str2 = df.format(cal.getTime());
                    nbgi.put("E_date", str2);
                    nbgi.put("B_UID", bid);
                    db.insert(DATABASE_TABLE_BGI, null, nbgi);

                    SwitchToBudgetItem();
                }
                else{
                    Log.e("NB", "Both inputs are required");
                    //Toast.makeText(getActivity(),"Both inputs are required!" , //Toast.LENGTH_SHORT).show();
                }
                    break;
            }
        }
    };

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void SwitchToBudgetItem(){
        db.close();
        dbget.close();
        ////Toast.makeText(getActivity(),"Item:"+ID, //Toast.LENGTH_SHORT).show();
        //switch fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        BudgetItem nextFrag = new BudgetItem();
        Bundle bundle = new Bundle();
        bundle.putString("ID", ID);
        nextFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container,nextFrag);

        //provide the fragment ID of your first fragment which you have given in
        //fragment_layout_example.xml file in place of first argument
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
