package com.saher.authapp.ui.myItems;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saher.authapp.model.Item;
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.activity.ViewItemActivity;

public class MyItemsFragment extends Fragment {

    FloatingActionButton addItemButton;
    ItemAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference itemRef = db.collection("Itembook");
    View root;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyItemsViewModel galleryViewModel = new ViewModelProvider(this).get(MyItemsViewModel.class);
        root = inflater.inflate(R.layout.fragment_my_items, container, false);
        recyclerView = root.findViewById(R.id.my_items_list);
        setUpRecyclerView();

        addItemButton = root.findViewById(R.id.activity_profile_fab);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ViewItemActivity.class);
                startActivity(i);
            }
        });

        return root;
    }

    private void setUpRecyclerView() {
        String id = FirebaseAuth.getInstance().getUid();
        Query query = itemRef.whereEqualTo("id", id);
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item.class).build();
        adapter = new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String Item_Id) {
                Intent i = new Intent(getContext(), ViewItemActivity.class);
                i.putExtra("ITEM_ID", Item_Id);
                startActivity(i);
            }
        }, getContext());
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }
}