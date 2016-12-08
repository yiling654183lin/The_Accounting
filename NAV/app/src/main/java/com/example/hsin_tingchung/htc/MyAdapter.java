package com.example.hsin_tingchung.htc;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsin_tingchung.nav.R;

import java.util.List;

/**
 * Created by Hsin-Ting Chung on 2016/11/25.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> mData;
    private AdapterCallback mAdapterCallback;

    public interface AdapterCallback  {
        void onMethodCallback(int position); // create callback function
    }


    public MyAdapter(List<String> data, AdapterCallback callback) {
        this.mAdapterCallback = callback;
        mData = data;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mData.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = mData.get(position);
                //Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
                mAdapterCallback.onMethodCallback(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // inner class to hold a reference to each item of RecyclerView """"static""""
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public View view;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mTextView = (TextView) itemLayoutView.findViewById(R.id.info_text);
            view = itemLayoutView;
        }

    }

}
