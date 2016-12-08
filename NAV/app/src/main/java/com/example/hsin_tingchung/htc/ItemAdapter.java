package com.example.hsin_tingchung.htc;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.hsin_tingchung.nav.R;

import java.util.List;

/**
 * Created by Hsin-Ting Chung on 2016/12/3.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<ItemModel> list;
    private AdapterCallback mAdapterCallback;

    public interface AdapterCallback  {
        void onMethodCallback_CheckBox(int position, boolean b); // create callback function
        void onMethodCallback_TextView(int position);

    }


    public ItemAdapter(List<ItemModel> data, AdapterCallback callback) {
        this.mAdapterCallback = callback;
        list = data;
    }


    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ms, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ItemModel itm = list.get(position);
        holder.text.setText(list.get(position).getName());
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(itm.isSelected());

        holder.text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String str = list.get(position).getName();
                //Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
                mAdapterCallback.onMethodCallback_TextView(position);
                return true;
            }
        });

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                itm.setSelected(b);
                mAdapterCallback.onMethodCallback_CheckBox(position, b);
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public CheckBox checkbox;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            text = (TextView) itemLayoutView.findViewById(R.id.list_item_ms_textview);
            checkbox = (CheckBox) itemLayoutView.findViewById(R.id.check);
        }
    }


}