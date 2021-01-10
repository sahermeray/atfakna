package com.saher.authapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.activity.ViewItemActivity;
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.model.Item;

public class HomeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference itemsCollectionReference = db.collection(Item.COLLECTION_NAME);
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.home_fragment);
        firebaseAuth = FirebaseAuth.getInstance();
        setUpRecyclerView();
        return root;
    }

    private void setUpRecyclerView() {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Query query = itemsCollectionReference.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        ItemAdapter adapter = new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String Item_Id) {
                if (currentUser != null) {
                    Intent i = new Intent(getContext(), ViewItemActivity.class);
                    i.putExtra("COMING_FROM_USER_ACTIVITY", 1);
                    i.putExtra("ITEM_ID", Item_Id);
                    startActivity(i);
                } else if (currentUser == null) {
                    Toast.makeText(getContext(), "you have to create account or login first", Toast.LENGTH_LONG).show();
                }
            }

        }, getContext());
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }
}