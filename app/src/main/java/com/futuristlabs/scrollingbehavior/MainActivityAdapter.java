package com.futuristlabs.scrollingbehavior;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MainViewHolder) viewHolder).bind();
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind() {
            if (getAdapterPosition() % 2 == 0) {
                itemView.setBackgroundColor(Color.WHITE);
            } else {

                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.design_default_color_primary_dark));
            }
        }
    }
}
