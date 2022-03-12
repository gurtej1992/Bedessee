package com.bedessee.salesca.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.reportsmenu.ReportAdapter;

import java.util.List;

public abstract class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ViewHolder> {

    private Context context;
    private List<String> titles;

    public ToolAdapter(Context context, List<String> titles) {
        this.context = context;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ToolAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolAdapter.ViewHolder holder, int position) {
        holder.v.setText(titles.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickView(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView v;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            v =(TextView)itemView.findViewById(R.id.drawer_txt);
        }
    }
    protected abstract void onClickView(int pos);
}
