package com.saher.authapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.model.Item;

import java.util.ArrayList;

public class ItemCustomAdapter extends ArrayAdapter<Item> {

    private final Context mContext;
    private ArrayList<Item> items;
    OnRecyclerViewItemClickListener listener;

    public ItemCustomAdapter(@NonNull Context context, ArrayList<Item> list) {
        super(context, 0, list);

        mContext = context;
        items = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_list_item, parent, false);
        }

        Item currentItem = items.get(position);
        TextView tv_name = listItem.findViewById(R.id.item_layout_name);
        tv_name.setText(currentItem.getName());
        TextView tv_location = listItem.findViewById(R.id.item_layout_location);
        tv_location.setText(currentItem.getLocation());
        TextView tv_price = listItem.findViewById(R.id.item_layout_price);
        tv_price.setText(currentItem.getPrice());
        ImageView iv_item = listItem.findViewById(R.id.item_layout_iv);
        iv_item.setImageURI(Uri.parse(currentItem.getImage()));

        return listItem;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
