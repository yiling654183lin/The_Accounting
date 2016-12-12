package com.example.hsin_tingchung.htc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class AccountItem extends Fragment implements ItemAdapter.AdapterCallback{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String ID;
    LinearLayout layout;

    //Spinner
    private ArrayAdapter<String> catListAdapter;
    private Spinner CategorySpinner;
    private String CatName[];
    private String CatID[];
    private int catSelectedID;
    private ArrayAdapter<String> tagListAdapter;
    private Spinner TagSpinner;
    private String TagName[];
    private String TagID[];
    private int tagSelectedID;
    private String selecedTag_UID;
    private int hadSavedCount;
    private String lastCat;
    private String lastTag;


    //variable
    private String category_list[]=null;
    private int CurrentButtonNumber = 0;
    private String t_UID = "";
    private String c_UID = "";

    private OnFragmentInteractionListener mListener;

    //新增DB write
    private static String DATABASE_TABLE_ACC = "Account";
    private static String DATABASE_TABLE_CAT = "Category";
    private static String DATABASE_TABLE_TAG = "Tag";
    private static String DATABASE_TABLE_STAG = "A_tag";
    private static String DATABASE_TABLE_NOW = "Now";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //新增DB read
    private SQLiteDatabase dbget;

    private TextView item_amount, item_time, item_gps, item_memo;
    private Button save_new_account, back_new_account, edit_item_account;


    //edit item contain RECYCLEVIEW
    RecyclerView recyclerView;
    private ItemAdapter myAdapter;
    private ArrayList<ItemModel> myDataset = new ArrayList<>();
    private ArrayList<String> myListContain = new ArrayList<>();
    //Merge and Split
    private ArrayList<String> orgListContain = new ArrayList<>();

    public AccountItem() {
        // Required empty public constructor
    }


    public static AccountItem newInstance(String param1, String param2) {
        AccountItem fragment = new AccountItem();
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
            this.myAdapter = new ItemAdapter(myDataset,this); // this class implements callback
            //Toast.makeText(getActivity(), "AccountItem:"+ID, //Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_item, container, false);

        getViews(v);
        createReadableDB();
        createWritableDB();


        // 取基本資料
        String[] colNames=new String[]{"UID", "Money", "Time", "Memo"};
        String where = "UID =" + ID;
        Cursor c = dbget.query(DATABASE_TABLE_ACC, colNames,where, null, null, null,null);
        c.moveToFirst();
        item_amount.setText(c.getString(1));
        item_time.setText(c.getString(2));
        item_gps.setText(c.getString(3));
        c.close();

        // 看該項收支是否編輯過
        String[] STcolNames = new String[]{"T_UID"};
        where = "A_UID = " + ID;
        Cursor b = dbget.query(DATABASE_TABLE_STAG, STcolNames, where, null, null, null, null);
        hadSavedCount = 0;
        hadSavedCount = b.getCount();

        t_UID = "";
        if (hadSavedCount > 0)  // 有編輯過
        {
            // 找之前設過的tag
            b.moveToFirst();
            if (b.getCount()>0)
            {
                t_UID+=b.getString(0);
            }
            else t_UID+="-1";
            b.close();

            // 找之前設過的category
            String[] TcolNames=new String[]{"C_UID"};
            where = "T_UID = " + t_UID;
            Cursor d = dbget.query(DATABASE_TABLE_TAG, TcolNames, where, null, null, null, null);
            d.moveToFirst();  // first one
            if (d.getCount()>0) {
                c_UID += d.getString(0);
            }
            else c_UID+="-1";
            d.close();
        }
        selecedTag_UID = t_UID;
        UpdateCatSpinner();
        msView(v);

        // ==0 insert & >0 update
        /*
        save_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(hadSavedCount == 0) {
                    ContentValues nct = new ContentValues();
                    nct.put("A_UID", ID);
                    nct.put("T_UID", selecedTag_UID);
                    db.insert(DATABASE_TABLE_STAG, null, nct);
                } else {
                    ContentValues nct = new ContentValues();
                    nct.put("A_UID", ID);
                    nct.put("T_UID", selecedTag_UID);
                    db.update(DATABASE_TABLE_STAG, nct,  "A_UID = " + ID, null);
                }
                SwitchToViewAccount();

            }
        });
        */
        back_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchToViewAccount();
            }
        });
        save_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTag();
                mergeAndSplit();
                SwitchToViewAccount();

            }
        });


        return v;
    }

    private void saveTag() {
        if(hadSavedCount == 0) {
            ContentValues nct = new ContentValues();
            nct.put("A_UID", ID);
            nct.put("T_UID", selecedTag_UID);
            db.insert(DATABASE_TABLE_STAG, null, nct);
        } else {
            ContentValues nct = new ContentValues();
            nct.put("A_UID", ID);
            nct.put("T_UID", selecedTag_UID);
            db.update(DATABASE_TABLE_STAG, nct,  "A_UID = " + ID, null);
        }
    }
    private void mergeAndSplit(){

        for(int i = 0; i < myListContain.size(); i++)
        {
            if(!orgListContain.contains(myListContain.get(i)))
            {
                // merge!!!
                String nuid = myListContain.get(i);

                // 如果AccountItem只剩一筆NowAccount，則merge後要delete那筆AccountItem
                String[] colNames = new String[]{"save"};
                String where = "N_UID = " + nuid;
                String mergeSave = "";
                Cursor d = dbget.query(DATABASE_TABLE_NOW, colNames, where, null, null, null, null);

                d.moveToFirst();
                mergeSave = d.getString(0);
                d.moveToNext();

                colNames = new String[]{"save"};
                where = "save = " + mergeSave;
                d = dbget.query(DATABASE_TABLE_NOW, colNames, where, null, null, null, null);

                int mergeCount = d.getCount();
                if( mergeCount == 1) {
                    where = "UID = " + mergeSave;
                    db.delete(DATABASE_TABLE_ACC, where, null);
                    where = "A_UID = "  + mergeSave;
                    db.delete(DATABASE_TABLE_STAG, where, null);
                    mergeCount--;
                }

                // update NowItem的save為現在的ID
                ContentValues update = new ContentValues();
                where = "N_UID = " + nuid;
                update.put("save", ID);
                db.update(DATABASE_TABLE_NOW, update, where, null);
                d.close();

                // 除掉被merge後，原本剩下多少
                int lastAmount = 0;
                String firstTime = "";
                if(mergeCount != 0){
                    colNames = new String[]{"time", "total"};

                    where = "save = " + mergeSave;
                    d = dbget.query(DATABASE_TABLE_NOW, colNames, where, null, null, null, null);

                    d.moveToFirst();
                    firstTime = d.getString(0);
                    d.moveToFirst();
                    for (int j = 0; j < d.getCount(); j++)
                    {
                        lastAmount += d.getInt(1);
                        d.moveToNext();
                    }
                    d.close();
                }

                // update相關筆AccountItem金額、時間
                update = new ContentValues();
                where = "UID = " + mergeSave;
                update.put("Money", lastAmount);
                update.put("Time", firstTime);
                db.update(DATABASE_TABLE_ACC, update, where, null);

            }
        }

        for(int i = 0; i < orgListContain.size(); i++)
        {

            if(!myListContain.contains(orgListContain.get(i)))
            {
                // split!!!
                String nuid = orgListContain.get(i);

                // create新的AccountItem
                String[] colNames = new String[]{"time", "total"};
                String TIME_temp = "", TOTAL_temp = "";
                String where = "N_UID = " + nuid;
                Cursor d = dbget.query(DATABASE_TABLE_NOW, colNames, where, null, null, null, null);

                d.moveToFirst();
                TIME_temp = d.getString(0);
                TOTAL_temp = d.getString(1);
                d.moveToNext();
                d.close();

                ContentValues insert = new ContentValues();
                insert.put("Time", TIME_temp);
                insert.put("Money", TOTAL_temp);
                db.insert(DATABASE_TABLE_ACC, null, insert);

                // update NowItem的save為最新的AccountID
                ContentValues update = new ContentValues();
                where = "N_UID = " + nuid;
                update.put("save", getLastID());
                db.update(DATABASE_TABLE_NOW, update, where, null);
            }
        }

        // update這筆AccountItem金額、時間

        int newAmount = 0;
        String firstTime = "";

        String[] colNames = new String[]{"time", "total"};

        String where = "save = " + ID;
        Cursor d = dbget.query(DATABASE_TABLE_NOW, colNames, where, null, null, null, null);

        d.moveToFirst();
        firstTime = d.getString(0);
        d.moveToFirst();
        for (int j = 0; j < d.getCount(); j++)
        {
            newAmount += d.getInt(1);
            d.moveToNext();
        }
        d.close();


        ////Toast.makeText(getActivity(), Integer.toString(newAmount), //Toast.LENGTH_SHORT).show();
        ContentValues update = new ContentValues();
        where = "UID = " + ID;
        update.put("Money", newAmount);
        update.put("Time", firstTime);
        db.update(DATABASE_TABLE_ACC, update, where, null);

    }

    public int getLastID() {
        String q = "SELECT last_insert_rowid()";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        int ID = c.getInt(0);
        c.close();
        return ID;
    }

    private List<ItemModel> getModel() {


        // 找這一筆的時間，並用getTime()轉換

        String[] colNames = new String[]{"time"};
        String tTime = "";
        String where = "UID = " + ID;
        Cursor d = dbget.query(DATABASE_TABLE_ACC, colNames, where, null, null, null, null);

        d.moveToFirst();
        tTime = d.getString(0);
        d.close();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dt = null;
        try {
            dt = sdf.parse(tTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String nt = Long.toString(dt.getTime());

        // 前一筆account所含的now項目

        // 從now中找到 "小於nt的最大時間" 的account的UID
        String[] LcolNames = new String[]{"save", "second"};
        String lUID = "";
        where = "second < " + nt;
        d = dbget.query(DATABASE_TABLE_NOW, LcolNames, where, null, null, null, "second desc");

        int lastCount = d.getCount();
        d.moveToFirst();
        if(lastCount != 0)
            lUID = d.getString(0);
        d.close();

        ////Toast.makeText(getActivity(), nt, //Toast.LENGTH_SHORT).show();

        // 用lUID在now中找到前一筆account的NowItem
        String NowNUID[], NowTime[], NowTotal[];
        String NUID_temp = "", TIME_temp = "", TOTAL_temp = "";

        if (lastCount != 0){

            LcolNames = new String[]{"N_UID", "time", "total"};
            where = "save = " + lUID; //
            d = dbget.query(DATABASE_TABLE_NOW, LcolNames, where, null, null, null, null);

            d.moveToFirst();
            for (int i = 0; i < d.getCount(); i++)
            {
                NUID_temp += d.getString(0) + ",";
                TIME_temp += d.getString(1) + ",";
                TOTAL_temp += d.getString(2) + ",";
                d.moveToNext();
            }
            d.close();

            int LNowCount = 0;
            if (!NUID_temp.equals(""))
            {
                NowNUID = NUID_temp.split(",");
                LNowCount = NowNUID.length;
            }
            else
            {
                NowNUID = new String[1];
                NowNUID[0] = "";
                LNowCount = 0;
            }
            if (!TIME_temp.equals(""))
            {
                NowTime = TIME_temp.split(",");
            }
            else
            {
                NowTime = new String[1];
                NowTime[0] = "-1";
            }
            if (!TOTAL_temp.equals(""))
            {
                NowTotal = TOTAL_temp.split(",");
            }
            else
            {
                NowTotal = new String[1];
                NowTotal[0] = "-1";
            }


            for(int i = 0; i < LNowCount; i++)
            {
                ItemModel linux = new ItemModel("  "+NowNUID[i]+" "+NowTime[i]+" "+NowTotal[i], Integer.parseInt(NowNUID[i]));
                myDataset.add(linux);
            }

        }




        // 該筆account原本就有的

        NowNUID = null; NowTime = null; NowTotal = null;

        String[] NcolNames = new String[]{"N_UID", "time", "total"};
        NUID_temp = ""; TIME_temp = ""; TOTAL_temp = "";
        where = "save = " + ID;
        d = dbget.query(DATABASE_TABLE_NOW, NcolNames, where, null, null, null, null);

        d.moveToFirst();
        for (int i = 0; i < d.getCount(); i++)
        {
            NUID_temp += d.getString(0) + ",";
            TIME_temp += d.getString(1) + ",";
            TOTAL_temp += d.getString(2) + ",";
            d.moveToNext();
        }
        d.close();

        int NNowCount = 0;
        if (!NUID_temp.equals(""))
        {
            NowNUID = NUID_temp.split(",");
            NNowCount = NowNUID.length;
        }
        else
        {
            NowNUID = new String[1];
            NowNUID[0] = "";
            NNowCount = 0;
        }
        if (!TIME_temp.equals(""))
        {
            NowTime = TIME_temp.split(",");
        }
        else
        {
            NowTime = new String[1];
            NowTime[0] = "-1";
        }
        if (!TOTAL_temp.equals(""))
        {
            NowTotal = TOTAL_temp.split(",");
        }
        else
        {
            NowTotal = new String[1];
            NowTotal[0] = "-1";
        }


        for(int i = 0; i < NNowCount; i++)
        {
            ItemModel linux = new ItemModel("  "+NowNUID[i]+" "+NowTime[i]+" "+NowTotal[i], Integer.parseInt(NowNUID[i]));
            linux.setSelected(true);
            myListContain.add(NowNUID[i]);     // default (已在Item內)
            orgListContain.add(NowNUID[i]);
            myDataset.add(linux);

        }

        // 後一筆account所含的now項目

        NowNUID = null; NowTime = null; NowTotal = null;

        // 從now中找到 "大於nt+90的最小時間" 的account的UID
        String[] FcolNames = new String[]{"save", "second"};
        String fUID = "";
        where = "second > '" + nt + "' AND save != '"+ ID +"'"; // nt屬於毫秒
        d = dbget.query(DATABASE_TABLE_NOW, FcolNames, where, null, null, null, "second asc");

        ////Toast.makeText(getActivity(), Long.toString(Long.parseLong(nt)+200), //Toast.LENGTH_SHORT).show();

        int futureCount = d.getCount();
        d.moveToFirst();
        if(futureCount != 0)
            fUID = d.getString(0);
        d.close();

        if(futureCount != 0){
            FcolNames = new String[]{"N_UID", "time", "total"};
            NUID_temp = ""; TIME_temp = ""; TOTAL_temp = "";
            where = "save = " + fUID; //
            d = dbget.query(DATABASE_TABLE_NOW, FcolNames, where, null, null, null, null);

            d.moveToFirst();
            for (int i = 0; i < d.getCount(); i++)
            {
                NUID_temp += d.getString(0) + ",";
                TIME_temp += d.getString(1) + ",";
                TOTAL_temp += d.getString(2) + ",";
                d.moveToNext();
            }
            d.close();

            int FNowCount = 0;
            if (!NUID_temp.equals(""))
            {
                NowNUID = NUID_temp.split(",");
                FNowCount = NowNUID.length;
            }
            else
            {
                NowNUID = new String[1];
                NowNUID[0] = "";
                FNowCount = 0;
            }
            if (!TIME_temp.equals(""))
            {
                NowTime = TIME_temp.split(",");
            }
            else
            {
                NowTime = new String[1];
                NowTime[0] = "-1";
            }
            if (!TOTAL_temp.equals(""))
            {
                NowTotal = TOTAL_temp.split(",");
            }
            else
            {
                NowTotal = new String[1];
                NowTotal[0] = "-1";
            }


            for(int i = 0; i < FNowCount; i++)
            {
                ItemModel linux = new ItemModel("  "+NowNUID[i]+" "+NowTime[i]+" "+NowTotal[i], Integer.parseInt(NowNUID[i]));
                myDataset.add(linux);
            }

        }

        /*
        ItemModel linux = new ItemModel("Linux", 0);
        linux.setSelected(true);
        myListContain.add(linux.getName());     // default (已在Item內)
        myDataset.add(linux);

        myDataset.add(new ItemModel("Windows7", 3));
        myDataset.add(new ItemModel("Suse", 2));
        myDataset.add(new ItemModel("Eclipse", 4));
        myDataset.add(new ItemModel("Ubuntu",8));
        myDataset.add(new ItemModel("Solaris", 10));
        myDataset.add(new ItemModel("Android", 13));
        myDataset.add(new ItemModel("iPhone", 9));
        myDataset.add(new ItemModel("Java",1));
        myDataset.add(new ItemModel(".Net", 12));
        myDataset.add(new ItemModel("PHP", 5));
        */

        return myDataset;
    }

    private void getViews(View v){
        item_amount = (TextView)v.findViewById(R.id.item_amount);
        item_time = (TextView)v.findViewById(R.id.item_time);
        item_gps = (TextView)v.findViewById(R.id.item_gps);
        //item_memo = (TextView)v.findViewById(R.id.item_memo);
        save_new_account = (Button)v.findViewById(R.id.save_new_account);
        back_new_account = (Button)v.findViewById(R.id.back_new_account);
        //edit_item_account = (Button)v.findViewById(R.id.edit_item_account);


        //get Spinner
        TagSpinner = (Spinner) v.findViewById(R.id.tag_spinner);
        CategorySpinner = (Spinner) v.findViewById(R.id.category_spinner);



    }

    private void msView(View v)
    {
        recyclerView = (RecyclerView) v.findViewById(R.id.listview_ms);
        getModel();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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


    private void UpdateTagView(String ID) {


        String[] colNames = new String[]{"T_UID", "T_name"};
        String str = "", id_temp = "";
        int preloadT = -1;
        String where = "C_UID = " + ID;
        ////Toast.makeText(getActivity(), "upT:"+ID , //Toast.LENGTH_SHORT).show();
        Cursor d = dbget.query(DATABASE_TABLE_TAG, colNames, where, null, null, null, null);

        if(d.getCount() == 0 ){
            save_new_account.setEnabled(false);
        }

        d.moveToFirst();
        for (int i = 0; i < d.getCount(); i++)
        {
            id_temp += d.getString(0) + ",";
            str += d.getString(1) + ",";
            d.moveToNext();
        }
        d.close();

        int TagCount = 0;
        if (!str.equals(""))
        {
            TagName = str.split(",");
        }
        else
        {
            TagName = new String[1];
            TagName[0] = "";
        }
        if (!id_temp.equals(""))
        {
            TagID = id_temp.split(",");
            TagCount = TagID.length;
        }
        else
        {
            TagID = new String[1];
            TagID[0] = "-1";
            TagCount = 0;
        }


        if(hadSavedCount != 0)  //尚未編輯
        {
            for(int i=0;i<TagCount;i++)
            {
                if (TagID[i].equals(t_UID))
                {
                    preloadT = i;
                }
            }
        }


        tagListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, TagName);
        tagListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TagSpinner.setAdapter(tagListAdapter);
        if(hadSavedCount != 0)
            TagSpinner.setSelection(preloadT, false);
        TagSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tagSelectedID = adapterView.getSelectedItemPosition();
                selecedTag_UID = TagID[tagSelectedID];
                ////Toast.makeText(getActivity(), "你選擇 Tag spin="+TagID[tagSelectedID], //Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void UpdateCatSpinner() {

        String str = "", id_temp = "";
        int preloadC = -1;
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
        if (!str.equals(""))
        {
            CatName = str.split(",");
        }
        else
        {
            CatName = new String[1];
            CatName[0] = "";
        }
        if (!id_temp.equals(""))
        {
            CatID = id_temp.split(",");
            CatCount = CatID.length;
        }
        else
        {
            CatID = new String[1];
            CatID[0] = "-1";
            CatCount = 0;
        }


        if(hadSavedCount != 0)
        {
            for(int i = 0; i < CatCount; i++)
            {
                if(CatID[i].equals(c_UID))
                {
                    preloadC = i;
                }
            }
        }

        catListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CatName);
        catListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CategorySpinner.setAdapter(catListAdapter);
        if(hadSavedCount != 0) {
            CategorySpinner.setSelection(preloadC, false);
            UpdateTagView(CatID[preloadC]); // 如果有編輯紀錄，要按c_UID撈t_UID
        }
        CategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catSelectedID = adapterView.getSelectedItemPosition();
                ////Toast.makeText(getActivity(), "Cat spin="+CatID[catSelectedID], //Toast.LENGTH_SHORT).show();
                UpdateTagView(CatID[catSelectedID]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void SwitchToViewAccount(){
        //switch fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        AccountView nextFrag = new AccountView();
        fragmentTransaction.replace(R.id.fragment_container,nextFrag);
        //fragmentTransaction.addToBackStack(null);
        int backStackCount = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getId(); // Get the back stack fragment id.
            getActivity().getSupportFragmentManager().popBackStack(backStackId, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } //leaf!!!!!!!!!!!!!!!!!! don't have return <-

        fragmentTransaction.commit();
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
    public void onMethodCallback_CheckBox(int position, boolean b) {
        String myData = myDataset.get(position).getName();
        int myId = myDataset.get(position).getId();
        if(b){
            myListContain.add(Integer.toString(myId));
        }else{
            myListContain.remove(Integer.toString(myId));
        }

        //Toast.makeText(getActivity(), "fragment = " + position + "=>" + myId + "->" + myData + "?" + b, //Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMethodCallback_TextView(int position) {
        String myData = myDataset.get(position).getName();
        int myId = myDataset.get(position).getId();
        //Toast.makeText(getActivity(), "fragment = " + position + "=>" + myId + "->" + myData, //Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
