package com.bedessee.salesca.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;

import java.util.List;

public abstract class MainDrawerAdapter extends RecyclerView.Adapter<MainDrawerAdapter.ViewHolder> {

    Context mContext;
    public List<SideMenu> mMenuItems;

    public MainDrawerAdapter(Context mContext, List<SideMenu> mMenuItems) {
        this.mContext = mContext;
        this.mMenuItems = mMenuItems;
    }

    @NonNull
    @Override
    public MainDrawerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainDrawerAdapter.ViewHolder holder, int position) {
        final SideMenu sideMenu = mMenuItems.get(position);

        String text = sideMenu.getMenuTitle();

        holder.v.setText(text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onClickView(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
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
