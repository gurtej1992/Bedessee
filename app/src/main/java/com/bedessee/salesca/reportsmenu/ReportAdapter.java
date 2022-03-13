package com.bedessee.salesca.reportsmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.main.MainDrawerAdapter;

import java.util.List;

public abstract class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context context;
    private List<String> titles;

    public ReportAdapter(Context context, List<String> titles) {
        this.context = context;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_report, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {

        String whole = titles.get(position);
      //  holder.title.setText(whole);
        String[] parts = whole.split("  #");
        if(parts.length == 2){
            holder.sub.setText(parts[1]);
        }
        holder.title.setText(parts[0]);
        holder.img.setImageDrawable(null);
        if(titles.get(position).contains("PDF")){
            holder.img.setBackgroundResource(R.drawable.ic_pdf);
        }
        else if(titles.get(position).contains("CSV")){
            holder.img.setBackgroundResource(R.drawable.ic_csv);
        }
        else if(titles.get(position).contains("HTM") || titles.get(position).contains("HTML")){
            holder.img.setBackgroundResource(R.drawable.ic_html);
        }
        else if(titles.get(position).contains("TXT")){
            holder.img.setBackgroundResource(R.drawable.ic_txt);
        }
        else if(titles.get(position).contains("UPDATED")){
            holder.sub.setText("");
            holder.img.setImageDrawable(null);
        }
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
        TextView title , sub;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title =(TextView)itemView.findViewById(R.id.txt_head);
            sub =(TextView)itemView.findViewById(R.id.txt_sub);
            img =(ImageView) itemView.findViewById(R.id.img_file);
        }
    }

    protected abstract void onClickView(int pos);
}
