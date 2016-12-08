package com.example.hsin_tingchung.htc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;



public class BlankFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private View view;
    private Button sendButton;
    private EditText moneyEditText;

    public BlankFragment() {
        // Required empty public constructor
    }


    public static BlankFragment newInstance(int index) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);

        fragment.setArguments(args);
        return fragment;
    }

    public int getShownIndex(){
        return getArguments().getInt("index", 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blank, container, false);

        moneyEditText = (EditText) view.findViewById(R.id.moneyEditText);

        sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                String message = moneyEditText.getText().toString();
                if (null != view && message != null) {
                    BluetoothChatFragment bluetooth_Chat_fragment = (BluetoothChatFragment)getFragmentManager().findFragmentByTag("bluetooth_Chat_fragment_TAG");
                    bluetooth_Chat_fragment.sendMessage(message);
                    //Toast.makeText(getActivity(), "送出囉", //Toast.LENGTH_SHORT).show();
                    //here!!!! FA-->FB FA use getTag find FB then call sendMessage!
                }
            }
        });
        return view;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
