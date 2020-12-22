package com.saher.authapp.activity;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.model.Item;

public class UserActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Toolbar usertoolbar;
    ItemAdapter adapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference itemRef=db.collection("Itembook");
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        usertoolbar=findViewById(R.id.user_activity_toolbar);
        setSupportActionBar(usertoolbar);
        rv=findViewById(R.id.activity_user_rv);
        setUpRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu,menu);
        Spinner spinner=(Spinner)menu.findItem(R.id.spinner).getActionView();
        ArrayAdapter<CharSequence>spinneradapter=ArrayAdapter.createFromResource(this,R.array.search,R.layout.support_simple_spinner_dropdown_item);
        spinneradapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);
        SearchView searchView=(SearchView)menu.findItem(R.id.user_search).getActionView();
        searchView.setSubmitButtonEnabled(true);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                Query query1=itemRef.whereEqualTo("name",query);
                                FirestoreRecyclerOptions<Item> options1=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query1,Item.class).build();
                                adapter=new ItemAdapter(options1, new OnRecyclerViewItemClickListener() {
                                    @Override
                                    public void onItemClick(String Item_Id) {
                                        Intent i=new Intent(getBaseContext(), ViewItemActivity.class);
                                        i.putExtra("COMING_FROM_USER_ACTIVITY",1);
                                        i.putExtra("ITEM_ID",Item_Id);
                                        startActivity(i);
                                    }
                                },getBaseContext());
                                adapter.startListening();
                                adapter.notifyDataSetChanged();
                                rv.setHasFixedSize(true);
                                rv.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
                                rv.setAdapter(adapter);
                                Toast.makeText(UserActivity.this,"submit clicked",Toast.LENGTH_LONG).show();
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                Query query2=itemRef.whereEqualTo("name",newText);
                                FirestoreRecyclerOptions<Item> options2=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query2,Item.class).build();
                                adapter=new ItemAdapter(options2, new OnRecyclerViewItemClickListener() {
                                    @Override
                                    public void onItemClick(String Item_Id) {
                                        Intent i=new Intent(getBaseContext(), ViewItemActivity.class);
                                        i.putExtra("COMING_FROM_USER_ACTIVITY",1);
                                        i.putExtra("ITEM_ID",Item_Id);
                                        startActivity(i);
                                    }
                                },getBaseContext());
                                adapter.startListening();
                                adapter.notifyDataSetChanged();
                                rv.setHasFixedSize(true);
                                rv.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
                                rv.setAdapter(adapter);
                                Toast.makeText(UserActivity.this,"text changed",Toast.LENGTH_LONG).show();
                                return false;
                            }
                        });
                        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                            @Override
                            public boolean onClose() {
                                setUpRecyclerView();
                                Toast.makeText(UserActivity.this,"search closed",Toast.LENGTH_LONG).show();
                                return false;
                            }
                        });

        return true;
    }

    private void setUpRecyclerView(){
        Query query=itemRef.orderBy("name",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Item> options=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query,Item.class).build();
        adapter=new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String Item_Id) {
                Intent i=new Intent(getBaseContext(), ViewItemActivity.class);
                i.putExtra("COMING_FROM_USER_ACTIVITY",1);
                i.putExtra("ITEM_ID",Item_Id);
                startActivity(i);
            }
        },this);
        adapter.startListening();
        adapter.notifyDataSetChanged();
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
                Intent i =new Intent(UserActivity.this, LoginActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }
}