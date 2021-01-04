package com.saher.authapp.ui.watchlist;

import android.os.Bundle;
import android.util.Log;
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
import com.saher.authapp.model.UserItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class WatchlistFragment extends Fragment {

    ItemCustomAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View root;
    RecyclerView recyclerView;
    ArrayList<Item> items = new ArrayList<>();
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState
    ) {
        adapter = new ItemCustomAdapter(getContext(), items);
        root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        recyclerView = root.findViewById(R.id.watched_list);
        listView = root.findViewById(R.id.watched_list_view);
        setUpRecyclerView();

        return root;
    }

    private void setUpRecyclerView() {
        String id = FirebaseAuth.getInstance().getUid();
//        Query query = itemRef.whereEqualTo("id", id);
        CollectionReference userItemReference = db.collection("useritem");
        Query userItemQuery = userItemReference;
        userItemQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                task.onSuccessTask(new SuccessContinuation<QuerySnapshot, UserItem>() {
                    @NonNull
                    @Override
                    public Task<UserItem> then(@Nullable QuerySnapshot queryDocumentSnapshots) throws Exception {

                        for (DocumentSnapshot userItem : queryDocumentSnapshots.getDocuments()) {
                            String itemId = userItem.get("itemIdl").toString();
                            Log.e("AHMAD", itemId);
                            CollectionReference storageReference = FirebaseFirestore.getInstance().collection("Itembook");
                            Query itemReference = storageReference.whereEqualTo("id", itemId);
                            itemReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                                        Item item = queryDocumentSnapshots.getDocuments().iterator().next().toObject(Item.class);

                                        items.add(item);
                                        adapter.setItems(items);
                                        listView.setAdapter(adapter);
//                                        Log.e("DEBUG", item.getDescription());
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