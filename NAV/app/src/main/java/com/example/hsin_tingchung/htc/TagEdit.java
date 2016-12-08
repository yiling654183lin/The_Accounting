package com.example.hsin_tingchung.htc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;

import static android.R.attr.drawable;
import static android.R.attr.onClick;

public class TagEdit extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //新增DB write
    private static String DATABASE_TABLE_CAT = "Category";
    private static String DATABASE_TABLE_TAG = "Tag";
    private SQLiteDatabase db;
    private DBhelper dbhelper;

    //新增DB read
    private SQLiteDatabase dbget;

    //New Button
    LinearLayout layout;
    Button newCat, newTag;

    //Spinner
    private ArrayAdapter<String> listAdapter;
    private Spinner Category;
    private String CatName[];
    private String CatID[];
    private int selectedID;

    //variable
    private String category_list[]=null;
    private int CurrentButtonNumber=0;


    private OnFragmentInteractionListener mListener;

    public TagEdit() {
        // Required empty public constructor
    }

    public static TagEdit newInstance(String param1, String param2) {
        TagEdit fragment = new TagEdit();
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
        // Inflate the layout for this fragment
        View view = null;
        view = inflater.inflate(R.layout.fragment_tag_edit, container, false);

        getViews(view);
        setButtonEvents();
        createWritableDB();
        createReadableDB();

        UpdateCatSpinner();
        //dbget.close();
        //db.close();
        return view;
    }

    AdapterView.OnItemSelectedListener spinnerlistener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selectedID = adapterView.getSelectedItemPosition();

            //Toast.makeText(getActivity(),CatID[0], //Toast.LENGTH_SHORT).show();
            UpdateTagView(CatID[selectedID]);

        }
        @Override
        public void onNothingSelected(AdapterView arg0){

        }
    };

    private void getViews(View v){
        //Create new button layout scrollView
        layout = (LinearLayout) v.findViewById(R.id.tag_output);

        //get Spinner
        Category = (Spinner) v.findViewById(R.id.category_spinner);

        //get Buttons
        newCat = (Button) v.findViewById(R.id.new_category);
        newTag = (Button) v.findViewById(R.id.new_tag);

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

    private void setButtonEvents(){
        newCat.setOnClickListener(buttonListener);
        newTag.setOnClickListener(buttonListener);
    }

    private View.OnClickListener buttonListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.new_category:
                    final View item = LayoutInflater.from(getActivity()).inflate(R.layout.new_category,null);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("New Category")
                            .setView(item)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText editText = (EditText) item.findViewById(R.id.edit_text);
                                    ContentValues nct = new ContentValues();
                                    nct.put("C_Name",editText.getText().toString());
                                    db.insert(DATABASE_TABLE_CAT, null, nct);
                                    UpdateCatSpinner();
                                    ////Toast.makeText(getActivity().getApplicationContext(), editText.getText().toString(), //Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    break;
                case R.id.new_tag:
                    final View item2 = LayoutInflater.from(getActivity()).inflate(R.layout.new_category,null);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("New Tag")
                            .setView(item2)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText editText = (EditText) item2.findViewById(R.id.edit_text);
                                    ContentValues ntg = new ContentValues();
                                    ntg.put("T_Name",editText.getText().toString());
                                    ntg.put("C_UID", CatID[selectedID]);
                                    db.insert(DATABASE_TABLE_TAG, null, ntg);
                                    UpdateTagView(CatID[selectedID]);
                                    ////Toast.makeText(getActivity().getApplicationContext(), editText.getText().toString()+" C_UID=" + CatID[selectedID], //Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    break;
            }
        }
    };

    private void UpdateTagView(String ID) {
        layout.removeAllViews();
        String where = "C_UID=" + ID;
        String[] colNames=new String[]{"T_UID","T_Name"};
        Cursor d = dbget.query(DATABASE_TABLE_TAG, colNames, where, null, null, null, null);
        d.moveToFirst();  // first one
        for (int j = 0; j < d.getCount(); j++) {
            Button btn = new Button(getActivity());
            CurrentButtonNumber = Integer.parseInt(d.getString(d.getColumnIndex(colNames[0])));
            btn.setId(CurrentButtonNumber);
            btn.setText(d.getString(1));
            btn.setBackgroundResource(R.drawable.button_normal_small);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ViewGroup parentView = (ViewGroup) v.getParent();
                    //db.delete(DATABASE_TABLE_TAG,"T_UID="+v.getId(),null);
                    //parentView.removeView(v);
                }
            });
            ////Toast.makeText(getActivity(),d.getString(1), //Toast.LENGTH_SHORT).show();
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(600, 200);
            layout.addView(btn, param);
            d.moveToNext();  // last one
        }
        d.close();
    }

    private void UpdateCatSpinner() {
        String[] colNames = new String[]{"C_UID", "C_name"};
        String str = "", id_temp = "";
        Cursor c = dbget.query(DATABASE_TABLE_CAT, colNames, null, null, null, null, null);

        c.moveToFirst();  // first one
        for (int i = 0; i < c.getCount(); i++) {
            id_temp += c.getString(0) + ",";
            str += c.getString(1) + ",";
            ////Toast.makeText(getActivity(),c.getString(1), //Toast.LENGTH_SHORT).show();
            c.moveToNext();  // last one
        }
        c.close();
        if (!str.equals("")){
            CatName=str.split(",");
            //Toast.makeText(getActivity(),"CatName:"+CatName[0], //Toast.LENGTH_SHORT).show();
            newTag.setEnabled(true);
        }
        else {
            CatName=new String[1];
            CatName[0]="";
            newTag.setEnabled(false);
        }
        if (!id_temp.equals("")){
            CatID=id_temp.split(",");
            //Toast.makeText(getActivity(),"CatID:"+CatID[0], //Toast.LENGTH_SHORT).show();
            newTag.setEnabled(true);
        }
        else {
            CatID=new String[1];
            CatID[0]="-1";
            newTag.setEnabled(false);
        }

        listAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, CatName);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category.setAdapter(listAdapter);
        Category.setOnItemSelectedListener(spinnerlistener);
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
}
