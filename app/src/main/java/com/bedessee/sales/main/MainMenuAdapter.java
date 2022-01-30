package com.bedessee.sales.main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.core.content.ContextCompat;

import com.bedessee.sales.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MainMenuAdapter extends ArrayAdapter<SideMenu> {

    private ArrayList<SideMenu> mMenuItems;

    public MainMenuAdapter(Context context, int resource, ArrayList<SideMenu> menuItems) {
        super(context, resource, menuItems);
        mMenuItems = menuItems;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        CheckedTextView v = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

        final SideMenu sideMenu = mMenuItems.get(position);

        String text = sideMenu.getMenuTitle();

        v.setText(text);

        if (text.equals("PRODUCTS") || text.equals("BRANDS") || text.equals("CATEGORIES")) {
            v.setBackgroundColor(Color.parseColor("#d3d3d3"));
        } else if (text.equals("UPC") || text.equals("ORDER HISTORY")) {
            int color = ContextCompat.getColor(v.getContext(), R.color.colorPrimary);
            v.setBackgroundColor(color);
        }

        try {
            v.setTextColor(Color.parseColor(sideMenu.getColour()));
        } catch (IllegalArgumentException e) {
            v.setTextColor(Color.BLACK);
        }
        return v;
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

}
