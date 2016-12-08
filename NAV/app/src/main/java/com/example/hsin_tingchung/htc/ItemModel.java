package com.example.hsin_tingchung.htc;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Hsin-Ting Chung on 2016/12/3.
 */
public class ItemModel {
    private String name;
    private int id;
    private boolean selected;

    public ItemModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId(){
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

