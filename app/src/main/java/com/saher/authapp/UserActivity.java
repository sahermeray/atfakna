package com.saher.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Toolbar usertoolbar;
    ItemAdapter adapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference itemRef=db.collection("Itembook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        usertoolbar=findViewById(R.id.user_activity_toolbar);
        setSupportActionBar(usertoolbar);
        setUpRecyclerView();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu,menu);
        MenuItem profile=menu.findItem(R.id.profile);
        MenuItem logout=menu.findItem(R.id.logout);
        SearchView searchView=(SearchView)menu.findItem(R.id.user_search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(UserActivity.this,"submit clicked",Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(UserActivity.this,"text changed",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(UserActivity.this,"search closed",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return true;
    }
    private void setUpRecyclerView(){
        Query query=itemRef.orderBy("name",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Item> options=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query,Item.class).build();
        adapter=new ItemAdapter(options);
        RecyclerView rv=findViewById(R.id.activity_user_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        rv.setAdapter(adapter);
    }
    protected void onStart() {

        super.onStart();
        adapter.startListening();
    }
    protected void onStop() {

        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Intent i =new Intent(UserActivity.this,ActivityLogin.class);
                startActivity(i);
                break;
            case R.id.profile:
                Intent intent=new Intent(UserActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}