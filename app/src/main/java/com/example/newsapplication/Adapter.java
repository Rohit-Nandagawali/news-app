package com.example.newsapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<Model> list;

    public Adapter(ArrayList<Model> list) {
        this.list = list;
        this.notifyDataSetChanged(); //when new data added it will be refreshed
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Model model = list.get(position);
        holder.title.setText(model.getTittle());
        holder.date.setText(model.getDate());
        holder.share_count.setText(model.getShare_count());
        holder.author.setText(model.getAuthor());
        holder.category.setText(model.getCategory());

        Glide.with(holder.author.getContext()).load(model.getImg()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, share_count, author,category;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            date = itemView.findViewById(R.id.t_date);
            title = itemView.findViewById(R.id.textView9);
            share_count = itemView.findViewById(R.id.textView10);
            author = itemView.findViewById(R.id.textView8);
            category = itemView.findViewById(R.id.category);
        }
    }
}
