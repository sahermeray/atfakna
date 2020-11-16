package com.saher.authapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item,ItemAdapter.ItemViewHolder> {


    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int i, @NonNull Item item) {
        holder.tv_name.setText(item.getName());
        holder.tv_location.setText(item.getLocation());
        holder.tv_price.setText(item.getPrice());
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ItemViewHolder(v);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name,tv_location,tv_price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.item_layout_name);
            tv_location=itemView.findViewById(R.id.item_layout_location);
            tv_price=itemView.findViewById(R.id.item_layout_price);
        }
    }
}
