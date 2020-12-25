package com.saher.authapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.model.Item;
import com.squareup.picasso.Picasso;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemViewHolder> {
    OnRecyclerViewItemClickListener listener;
    Context context;
    int layout;

    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options, OnRecyclerViewItemClickListener listener, Context context) {
        super(options);
        this.listener = listener;
        this.context = context;
        layout = R.layout.item_card;
    }

    public ItemAdapter(int layout, @NonNull FirestoreRecyclerOptions<Item> options, OnRecyclerViewItemClickListener listener, Context context) {
        super(options);
        this.listener = listener;
        this.context = context;
        this.layout = layout;
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int i, @NonNull Item item) {
        holder.tv_name.setText(item.getName());
        holder.tv_location.setText(item.getLocation());
        holder.tv_price.setText(item.getPrice());
        holder.tv_name.setTag(item.getUniqueID());
        if (null != item.getImage()) {
            Picasso.with(context).load(Uri.parse(item.getImage())).fit().centerCrop().into(holder.iv_item);
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ItemViewHolder(v);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_location, tv_price;
        ImageView iv_item;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.item_layout_name);
            tv_location = itemView.findViewById(R.id.item_layout_location);
            tv_price = itemView.findViewById(R.id.item_layout_price);
            iv_item = itemView.findViewById(R.id.item_layout_iv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String itemid = String.valueOf(tv_name.getTag());
                    listener.onItemClick(itemid);
                }
            });
        }
    }
}
