package com.saher.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WatchListActivity extends AppCompatActivity {
    Toolbar watchlisttoolbar;
    ItemAdapter adapterwatchlist;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference itemRef=db.collection("Itembook");
    CollectionReference userItemRef=db.collection("useritem");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);
        watchlisttoolbar=findViewById(R.id.watchlist_toolbar);
        setSupportActionBar(watchlisttoolbar);

        readData(new MyCallback() {
            @Override
            public void onCallback(String value) {
                Query query = itemRef.whereEqualTo("uniqueID", value);
                FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item.class).build();
                adapterwatchlist = new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(String Item_Id) {
                        Intent i = new Intent(getApplicationContext(), View_Item_Activity.class);
                        i.putExtra("ITEM_ID", Item_Id);
                        startActivity(i);
                    }
                }, getBaseContext());
                adapterwatchlist.startListening();
                RecyclerView rv1 = findViewById(R.id.watchlist_rv);
                rv1.setHasFixedSize(true);
                rv1.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
                rv1.setAdapter(adapterwatchlist);
            }
        });
    }


       public void readData(final MyCallback myCallback){
       String id = FirebaseAuth.getInstance().getUid();
        userItemRef.whereEqualTo("userId", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot dd = queryDocumentSnapshots.getDocuments().get(0);
                String s = dd.getString("itemIdl");
                Toast.makeText(getApplicationContext(), "your item id is" + s, Toast.LENGTH_SHORT).show();
                myCallback.onCallback(s);
            }});}





    /*protected void onStart() {
        super.onStart();
        adapterwatchlist.startListening();
    }

    protected void onStop() {
       super.onStop();
        adapterwatchlist.stopListening();
    }*/
}