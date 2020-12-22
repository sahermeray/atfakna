package com.saher.authapp.ui.watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.activity.ViewItemActivity;
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.model.Item;

public class WatchlistFragment extends Fragment {

    private WatchlistViewModel watchlistViewModel;
    ItemAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference itemRef = db.collection("Itembook");
    View root;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        watchlistViewModel =
                new ViewModelProvider(this).get(WatchlistViewModel.class);

        root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        recyclerView = root.findViewById(R.id.watched_list);
        setUpRecyclerView();

//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        watchlistViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void setUpRecyclerView() {
        String id = FirebaseAuth.getInstance().getUid();
        Query query = itemRef.whereEqualTo("id", id);;
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item.class).build();
        adapter = new ItemAdapter(R.layout.item_list_item, options, new OnRecyclerViewItemClickListener() {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}