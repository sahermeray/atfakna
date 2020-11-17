package com.saher.authapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    FloatingActionButton fabadd;
    Toolbar profiletoolbar;
    ItemAdapter adapter1;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference itemRef=db.collection("Itembook");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fabadd=findViewById(R.id.activity_profile_fab);
        profiletoolbar=findViewById(R.id.profile_activity_toolbar);
        setSupportActionBar(profiletoolbar);

        setUpRecyclerView();

        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ProfileActivity.this,View_Item_Activity.class);
                startActivity(i);
            }
        });
    }
    private void setUpRecyclerView(){
       // Query query=itemRef.orderBy("name",Query.Direction.DESCENDING);
        //FirestoreRecyclerOptions<Item>options=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query,Item.class).build();
        String id= FirebaseAuth.getInstance().getUid();

        Query query=itemRef.whereEqualTo("id",id);
        FirestoreRecyclerOptions<Item>options=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query,Item.class).build();




        adapter1=new ItemAdapter(options);
        RecyclerView rv1=findViewById(R.id.activity_profile_rv);
        rv1.setHasFixedSize(true);
        rv1.setLayoutManager(new GridLayoutManager(this,2));
        rv1.setAdapter(adapter1);
    }
    protected void onStart() {

        super.onStart();
        adapter1.startListening();
    }
    protected void onStop() {

        super.onStop();
        adapter1.stopListening();
    }
}