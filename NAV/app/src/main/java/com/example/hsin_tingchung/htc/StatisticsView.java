package com.example.hsin_tingchung.htc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class StatisticsView extends Fragment implements MyAdapter.AdapterCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FrameLayout mainLayout;
    private PieChart mChart;
    //private float [] yData;// = {35, 15, 20, 5, 15, 10};
    //private String [] xData;// = {"食", "衣", "住", "行", "育", "樂"};

    //新增DB write
    private static String DATABASE_TABLE_Account = "Account";
    private static String DATABASE_TABLE_CAT = "Category";
    private static String DATABASE_TABLE_TAG = "Tag";
    private static String DATABASE_TABLE_STAG = "A_tag";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //新增DB read
    private SQLiteDatabase dbget;

    private String CatName[];
    private String CatID[];
    private String TagID[];
    private String A_UID[];
    private int [] totalAmountInCat;

    RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private ArrayList<String> myDataset = new ArrayList<>();

    public StatisticsView() {
        // Required empty public constructor
    }


    public static StatisticsView newInstance(String param1, String param2) {
        StatisticsView fragment = new StatisticsView();
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
            this.myAdapter = new MyAdapter(myDataset,this); // this class implements callback
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics_view, container, false);

        createReadableDB();
        createWritableDB();

        mainLayout = (FrameLayout) v.findViewById(R.id.mainLayout);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        mChart = new PieChart(getActivity());

        mainLayout.addView(mChart);
        //mainLayout.setBackgroundColor(Color.parseColor("#DCDCDC"));

        mChart.setUsePercentValues(true);
        mChart.setDescription("The Accounting");

        mChart.setDrawHoleEnabled(true);
        //mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(45);
        mChart.setTransparentCircleRadius(50);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);



        //click!!
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if(entry == null)
                    return;
                int CatIndex = entry.getXIndex();
                Toast.makeText(getActivity(), CatName[CatIndex] + "=" + entry.getVal() + "元",
                        Toast.LENGTH_SHORT).show();



                /*-------------------列出account Item View的---------------------*/

                String str = "", id_temp = "";
                int [] AccountAmount;

                String [] AccountTime;



                myDataset.clear();
                String [] TcolNames = new String[]{"T_UID"};
                    String Twhere = "C_UID =" + CatID[CatIndex];
                    Cursor t = dbget.query(DATABASE_TABLE_TAG, TcolNames, Twhere, null, null, null, null);

                    t.moveToFirst();
                    for (int ii = 0; ii < t.getCount(); ii++)
                    {
                        id_temp += t.getString(0) + ",";
                        t.moveToNext();
                    }
                    t.close();

                    int TagCount = 0;
                    /* T_UID---------------------------------------------- */
                    if (!id_temp.equals("")){
                        TagID = id_temp.split(",");
                        TagCount = TagID.length;
                    }
                    else {
                        TagID = new String[1];
                        TagID[0] = "-1";
                        TagCount = 0;
                    }
                    id_temp = "";



                    for(int j = 0; j < TagCount; j++) {
                        String[] STcolNames = new String[]{"A_UID"};
                        String STwhere = "T_UID =" + TagID[j];
                        Cursor st = dbget.query(DATABASE_TABLE_STAG, STcolNames, STwhere, null, null, null, null);

                        st.moveToFirst();
                        for (int jj = 0; jj < st.getCount(); jj++) {
                            id_temp += st.getString(0) + ",";
                            st.moveToNext();
                        }
                        st.close();

                        int A_UIDCount = 0;
                        /* A_UID---------------------------------------------- */
                        if (!id_temp.equals("")) {
                            A_UID = id_temp.split(",");
                            A_UIDCount = A_UID.length;
                        }
                        else {
                            A_UID = new String[1];
                            A_UID[0] = "-1";
                            A_UIDCount = 0;
                        }
                        id_temp = "";
                        str = "";

                        AccountAmount = new int[A_UIDCount];
                        AccountTime = new String[A_UIDCount];
                        for (int k = 0; k < A_UIDCount; k++) {   //SUID 是string 所以length會撈到-1
                            String[] ScolNames = new String[]{"Money" , "Time"};
                            String Swhere = "UID =" + A_UID[k];
                            Cursor s = dbget.query(DATABASE_TABLE_Account, ScolNames, Swhere, null, null, null, null);

                            s.moveToFirst();
                            for (int kk = 0; kk < s.getCount(); kk++) {
                                id_temp = s.getString(0); //A_UID S_Money一對一
                                str = s.getString(1);
                                s.moveToNext();
                            }
                            s.close();


                            /* S_Money---------------------------------------------- */
                            if (!id_temp.equals("")) {
                                ////Toast.makeText(getActivity(), id_temp , //Toast.LENGTH_LONG);
                                AccountAmount[k] = Integer.parseInt(id_temp);
                            }

                            if (!str.equals("")) {
                                AccountTime[k] = str;
                            }
                            id_temp = "";
                            str = "";
                            // myDataset.add(k + " " + A_UID[k] + " " + AccountAmount[k] + " " + AccountTime[k]);
                            myDataset.add(A_UID[k] + " " + AccountAmount[k] + " " + AccountTime[k]);
                        }
                    }


                /*-------------------列出account Item View的---------------------*/


                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(myAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());


            }

            @Override
            public void onNothingSelected() {

            }
        });

        calculateStatisticsData();
        addData();

        Legend legend = mChart.getLegend();     //title
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        legend.setXEntrySpace(0);   //right space
        legend.setYEntrySpace(0);   //below space



        return v;
    }




    private void addData() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < totalAmountInCat.length; i++)
            yVals.add(new Entry(totalAmountInCat[i], i));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < CatName.length; i++)
            xVals.add(CatName[i]);

        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setSliceSpace(3);      // sector size
        dataSet.setSelectionShift(10);  // click sector expend size

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        mChart.highlightValue(null);

        mChart.invalidate();
    }

    public void calculateStatisticsData(){

        String str = "", id_temp = "";

        String[] colNames = new String[]{"C_UID", "C_name"};
        Cursor c = dbget.query(DATABASE_TABLE_CAT, colNames, null, null, null, null, null);

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++)
        {
            id_temp += c.getString(0) + ",";
            str += c.getString(1) + ",";
            c.moveToNext();
        }
        c.close();

        int CatCount = 0;
        /* C_UID---------------------------------------------- */
        if (!str.equals("")) {
            CatName = str.split(",");
        }
        else {
            CatName = new String[1];
            CatName[0] = "";
        }
        if (!id_temp.equals("")) {
            CatID = id_temp.split(",");
            CatCount = CatID.length;
        }
        else {
            CatID = new String[1];
            CatID[0] = "-1";
            CatCount = 0;
        }
        str = "";
        id_temp = "";

        totalAmountInCat = new int[CatID.length];
        for (int a = 0; a < CatCount; a++)
            totalAmountInCat[a] = 0;


        /*******************SEARCH**************************/

        for(int i = 0; i < CatCount; i++){
            String [] TcolNames = new String[]{"T_UID"};
            String Twhere = "C_UID =" + CatID[i];
            Cursor t = dbget.query(DATABASE_TABLE_TAG, TcolNames, Twhere, null, null, null, null);

            t.moveToFirst();
            for (int ii = 0; ii < t.getCount(); ii++)
            {
                id_temp += t.getString(0) + ",";
                t.moveToNext();
            }
            t.close();

            int TagCount = 0;
            /* T_UID---------------------------------------------- */
            if (!id_temp.equals("")) {
                TagID = id_temp.split(",");
                TagCount = TagID.length;
            }
            else {
                TagID = new String[1];
                TagID[0] = "-1";
                TagCount = 0;
            }
            id_temp = "";

            for(int j = 0; j < TagCount; j++) {
                String[] STcolNames = new String[]{"A_UID"};
                String STwhere = "T_UID =" + TagID[j];
                Cursor st = dbget.query(DATABASE_TABLE_STAG, STcolNames, STwhere, null, null, null, null);

                st.moveToFirst();
                for (int jj = 0; jj < st.getCount(); jj++) {
                    id_temp += st.getString(0) + ",";
                    st.moveToNext();
                }
                st.close();

                int A_UIDCount = 0;
                /* A_UID---------------------------------------------- */
                if (!id_temp.equals("")) {
                    A_UID = id_temp.split(",");
                    A_UIDCount = A_UID.length;
                } else {
                    A_UID = new String[1];
                    A_UID[0] = "-1";
                    A_UIDCount = 0;
                }
                id_temp = "";


                for (int k = 0; k < A_UIDCount; k++) {
                    String[] ScolNames = new String[]{"Money"};
                    String Swhere = "UID =" + A_UID[k];
                    Cursor s = dbget.query(DATABASE_TABLE_Account, ScolNames, Swhere, null, null, null, null);

                    s.moveToFirst();
                    for (int kk = 0; kk < s.getCount(); kk++) {
                        id_temp = s.getString(0); //A_UID S_Money一對一
                        s.moveToNext();
                    }
                    s.close();

                    /* S_Money---------------------------------------------- */
                    if (!id_temp.equals("")) {
                        totalAmountInCat[i] += Integer.parseInt(id_temp);
                    }
                    id_temp = "";

                }
            }
        }
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

    @Override
    public void onMethodCallback(int position) {
        String myData = myDataset.get(position);
        int id = Integer.parseInt(myData.substring(0, myData.indexOf(" ")));

        //Toast.makeText(getActivity(), "fragment = " + position + "=>" + myData, //Toast.LENGTH_SHORT).show();
        jumpLayout_AccountItem(getView(), id);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /*改!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
    public void jumpLayout_AccountItem(View v, int index) {

        dbget.close();
        db.close();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        AccountItem nextFrag = new AccountItem();
        Bundle bundle = new Bundle();
        bundle.putString("ID", Integer.toString(index));
        nextFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, nextFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
