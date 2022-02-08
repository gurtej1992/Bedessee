package com.bedessee.salesca.reportsmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bedessee.salesca.R;

import java.util.List;


public class ReportsMenuAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> titles;

    public ReportsMenuAdapter(final Context context, final List<String> titles) {
        super(context, android.R.layout.simple_spinner_dropdown_item, titles);
        this.context = context;
        this.titles = titles;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    public View getCustomView(int position, ViewGroup parent) {
        final View row = LayoutInflater.from(context).inflate(R.layout.reports_menu_item_view, parent, false);
        ((TextView)row.findViewById(android.R.id.text1)).setText(titles.get(position));
        return row;
    }
}
