package com.example.hsin_tingchung.htc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class BudgetItem extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FrameLayout mainLayout;
    private BarChart mChart;
    private int DATA_COUNT;
    private float [] yData = {35, 15, 20, 5, 15, 10, 35, 15, 20, 5, 15, 10};
    private String [] xData = {"食", "衣", "住", "行", "育", "樂", "1", "2", "3", "4", "5", "6"};

    //新增DB write
    private static String DATABASE_TABLE_BUD = "Budget";
    private static String DATABASE_TABLE_BUDI = "BudgetItem";
    private static String DATABASE_TABLE_TAG = "Tag";
    private static String DATABASE_TABLE_BTAG = "B_tag";
    private static String DATABASE_TABLE_SPT = "Account";
    private static String DATABASE_TABLE_STAG = "A_tag";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //新增DB read
    private SQLiteDatabase dbget;

    //claim button
    private Button backButton;
    private Button editButton;

    //claim TextView
    private TextView itemName;
    private TextView itemAmount;
    private TextView itemPeriod;
    private TextView itemUsed;

    //claim Layout
    private GridLayout tagsLayout;

    //claim variable
    public String ID;
    private String NAME;
    private int amount;
    private int period;
    private String str;
    private int CurrentButtonNumber;
    private int[] clickCount;
    private int btu_total;

    private OnFragmentInteractionListener mListener;

    public BudgetItem() {
        // Required empty public constructor
    }


    public static BudgetItem newInstance(String param1, String param2) {
        BudgetItem fragment = new BudgetItem();
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
            //Toast.makeText(getActivity(), "BudgetItem:"+ID, //Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=null;
        v=inflater.inflate(R.layout.fragment_budget_item, container, false);

        getViews(v);
        setEvents(v);
        createWritableDB();
        createReadableDB();

        String[] colNames=new String[]{"BI_UID","E_date","BGI.amount","name"};
        String table=DATABASE_TABLE_BUD+
                " BG INNER JOIN " +
                DATABASE_TABLE_BUDI+" BGI " +
                "ON BG.B_UID = BGI.B_UID";
        String where = "BI_UID =" + ID;
        Cursor c = dbget.query(table, colNames, where, null, null, null,null);

        if(c.getCount()>0) {
            c.moveToFirst();
            itemName.setText(c.getString(3));
            itemPeriod.setText(c.getString(1));
            itemAmount.setText(c.getString(2));
            c.close();
        }
            String[] colNames1 = new String[]{"T_UID", "T_Name"};
            c = dbget.query(DATABASE_TABLE_TAG, colNames1, null, null, null, null, null);
            c.moveToFirst();


            clickCount = new int[c.getCount()];

            btu_total = 0;

            for (int i = 0; i < c.getCount(); i++) {
                str = "";
                //str += c.getString(c.getColumnIndex(colNames[0])) + "筆\t\t";
                str += c.getString(1) + "\t\t";
                //str += c.getString(2) + "\t\t";
                //str += c.getString(3);//+ "\n";
                clickCount[i] = 0;


                //////////New tag button
                final Button btn = new Button(getActivity());
                CurrentButtonNumber =c.getInt(0);
                btn.setId(CurrentButtonNumber);
                //CurrentButtonNumber++;
                btn.setText(str);

                String[] BTcolNames = new String[]{"T_UID"};
                where = "BI_UID = " + ID + " AND T_UID = " + c.getString(0);
                final String btTag = c.getString(0);
                Cursor bt = dbget.query(DATABASE_TABLE_BTAG, BTcolNames, where, null, null, null, null);
                final int btCount = bt.getCount();

                if (btCount > 0) {
                    btn.setBackgroundResource(R.drawable.button_budget_tag_selected);
                } else {
                    btn.setBackgroundResource(R.drawable.button_budget_tag_unselect);
                }

                clickCount[btn.getId() - 1] = btCount;
                ////Toast.makeText(getActivity(),c.getString(1), //Toast.LENGTH_SHORT).show();
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(500, 200);
                tagsLayout.addView(btn, param);

                if (btCount > 0) {
                    String[] BTUcolNames = new String[]{"T_UID, A_UID, UID, Money"};
                    where = "T_UID = " + c.getString(0);
                    table = DATABASE_TABLE_STAG +
                            " STAG INNER JOIN " +
                            DATABASE_TABLE_SPT + " SPT " +
                            "ON STAG.A_UID = SPT.UID";
                    Cursor btu = dbget.query(table, BTUcolNames, where, null, null, null, null);
                    //final String MY_QUERY = "SELECT T_UID, UID, Money FROM "+DATABASE_TABLE_TAG+" INNER JOIN "+DATABASE_TABLE_SPT+" ON A_UID=UID WHERE T_UID="+bt.getString(0);
                /*
                dbget.rawQuery("SELECT a.T_UID, a.A_UID, b.UID, b.Money" +
                        "FROM"+DATABASE_TABLE_STAG+
                        " a INNER JOIN "+DATABASE_TABLE_SPT+
                        " ON a.A_UID=b.UID " +
                        "WHERE a.T_UID="+c.getString(0), new String[]{"test"});
                        */
                    //Toast.makeText(getActivity(), "AmountC :" + btu.getCount(), //Toast.LENGTH_SHORT).show();
                    btu.moveToFirst();
                    for (int j = 0; j < btu.getCount(); j++) {
                        btu_total += btu.getInt(3);
                        //Toast.makeText(getActivity(), "Amount :" + btu.getString(3), //Toast.LENGTH_SHORT).show();
                        btu.moveToNext();
                    }
                    btu.close();
                    ////Toast.makeText(getActivity(), "BT:" + c.getString(0), //Toast.LENGTH_SHORT).show();
                }
                bt.close();

                c.moveToNext();  // last one
            }
        c.close();

        itemUsed.setText(Integer.toString(btu_total));

        /*-----------------------Barchart-----------------------*/
        mainLayout = (FrameLayout) v.findViewById(R.id.mainLayout);
        mChart = new BarChart(getActivity());

        mainLayout.addView(mChart);
        mChart.setDescription("The Accounting");
        DATA_COUNT = 12;
        addData();

        /*Legend legend = mChart.getLegend();     //title
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        legend.setXEntrySpace(0);   //right space
        legend.setYEntrySpace(0);   //below space
*/



        //click!!
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if(entry == null)
                    return;
                ////Toast.makeText(getActivity(), entry.getVal() + "元",//Toast.LENGTH_SHORT).show();
                db.close();
                dbget.close();

                //switch fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();
                BudgetItemHistory nextFrag = new BudgetItemHistory();
                fragmentTransaction.replace(R.id.fragment_container,nextFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return v;
    }

    private void addData() {

        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        for (int i = 0; i < DATA_COUNT; i++)
            yVals.add(new BarEntry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < DATA_COUNT; i++)
            xVals.add(xData[i]);

        BarDataSet dataSet = new BarDataSet(yVals, "hello");

        ArrayList<Integer> colors = new ArrayList<Integer>();

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());*/
        colors.add(Color.argb(255, 242, 156, 92));
        dataSet.setColors(colors);

        BarData data = new BarData(xVals, dataSet);
        //data.setValueFormatter(new PercentFormatter());
        //data.setValueTextSize(12f);
        //data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

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

    private void getViews(View v){

        itemName=(TextView)v.findViewById(R.id.budget_name);
        itemAmount=(TextView)v.findViewById(R.id.budget_item_amount);
        itemPeriod=(TextView)v.findViewById(R.id.budget_period);
        itemUsed=(TextView)v.findViewById(R.id.budget_item_used);

        backButton=(Button)v.findViewById(R.id.budget_item_back);
        editButton=(Button)v.findViewById(R.id.budget_item_edit);


        tagsLayout=(GridLayout)v.findViewById(R.id.layout_tags);
    }
    private void setEvents(View v){
        backButton.setOnClickListener(buttonListener);
        editButton.setOnClickListener(buttonListener);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private View.OnClickListener buttonListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.budget_item_edit:
                    if (  ( !itemAmount.getText().toString().equals("")) && ( !itemName.getText().toString().equals("") ) ) {
                        NAME = itemName.getText().toString();
                        amount = Integer.parseInt(itemAmount.getText().toString());
                        //period = Integer.parseInt(itemPeriod.getText().toString());
                        ////Toast.makeText(getActivity(),NAME, //Toast.LENGTH_SHORT).show();
                        ContentValues nbg = new ContentValues();
                        nbg.put("name",NAME);
                        nbg.put("period",itemPeriod.getText().toString());
                        nbg.put("amount",amount);
                        //db.update(DATABASE_TABLE_OUT, null, nbg,");
                        SwitchToEditBudget();
                    }
                    else{
                        Log.e("NB", "Both inputs are required");
                        //Toast.makeText(getActivity(),"Both inputs are required!" , //Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.budget_item_back:
                    SwitchToEditBudget();
                    break;
            }
        }
    };


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public void SwitchToEditBudget(){
        db.close();
        dbget.close();
        ////Toast.makeText(getActivity(),"Item:"+ID, //Toast.LENGTH_SHORT).show();
        //switch fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        BudgetItemEdit nextFrag = new BudgetItemEdit();
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
