package com.saher.authapp.ui.watchlist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.saher.authapp.R;
import com.saher.authapp.adapter.ItemCustomAdapter;
import com.saher.authapp.model.Item;
import com.saher.authapp.model.WatchedItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WatchlistFragment extends Fragment {

    private static final int PICK_IMAGE_REQ_CODE = 1;
    ItemCustomAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View root;
    ArrayList<Item> items = new ArrayList<>();
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState
    ) {
        adapter = new ItemCustomAdapter(getContext(), items);
        root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        listView = root.findViewById(R.id.watched_list_view);
        setUpRecyclerView();

        return root;
    }

    private void setUpRecyclerView() {
        String userId = FirebaseAuth.getInstance().getUid();
        CollectionReference userItemReference = db.collection(WatchedItem.COLLECTION_NAME);
        Query userItemQuery = userItemReference.whereEqualTo("userId", userId);
        userItemQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                task.onSuccessTask(new SuccessContinuation<QuerySnapshot, WatchedItem>() {
                    @NonNull
                    @Override
                    public Task<WatchedItem> then(@Nullable QuerySnapshot queryDocumentSnapshots) throws Exception {

                        for (DocumentSnapshot userItem : queryDocumentSnapshots.getDocuments()) {
                            final String itemId = userItem.get("itemIdl").toString();
                            CollectionReference storageReference = FirebaseFirestore.getInstance().collection(Item.COLLECTION_NAME);
                            Task<DocumentSnapshot> itemReference = storageReference.document(itemId).get();
                            itemReference.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        Item item = documentSnapshot.toObject(Item.class);
                                        item.setUniqueID(itemId);
                                        items.add(item);
                                        adapter.setItems(items);
                                        listView.setAdapter(adapter);
                                    }
                                }
                            });
                        }

                        listView.setAdapter(adapter);

                        return null;
                    }
                });
            }
        });
    }
}