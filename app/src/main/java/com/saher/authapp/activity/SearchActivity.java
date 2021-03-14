package com.saher.authapp.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.model.Item;

public class SearchActivity extends AppCompatActivity {
    private static final String JARGON = "test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference itemRef = db.collection(Item.COLLECTION_NAME);
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar viewItemToolbar = findViewById(R.id.search_activity_toolbar);
        setSupportActionBar(viewItemToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.search_page);


        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(SearchActivity.this,"this is search activity "+query,Toast.LENGTH_LONG).show();
            doMySearch(query);
        }
    }
    public void doMySearch(String s){
        String strSearch=s;
        Toast.makeText(SearchActivity.this,"strSearch "+strSearch,Toast.LENGTH_LONG).show();
        int strlength=strSearch.length();
        String strFrontCode=strSearch.substring(0,strlength-1);
        String strEndCode=strSearch.substring(strlength-1,strSearch.length());
        String startcode=strSearch;
        char x=(char) (strEndCode.charAt(0)+1);
        String endcode=strFrontCode+String.valueOf(x);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Query query = itemRef.whereGreaterThanOrEqualTo(Item.FIELD_NAME,startcode).whereLessThan(Item.FIELD_NAME,endcode);
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        ItemAdapter adapter = new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String Item_Id) {
                if ((currentUser != null && currentUser.isEmailVerified())) {
                    Intent i = new Intent(getBaseContext(), ViewItemActivity.class);
                    i.putExtra("COMING_FROM_USER_ACTIVITY", 1);
                    i.putExtra("ITEM_ID", Item_Id);
                    startActivity(i);
                } else if (currentUser == null) {
                    Intent i = new Intent(getBaseContext(), ViewItemActivity.class);
                    i.putExtra("COMING_FROM_USER_ACTIVITY", 2);
                    i.putExtra("ITEM_ID", Item_Id);
                    startActivity(i);
                }
            }

        }, this);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }*/

    /*private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView searchQuery = findViewById(R.id.searchQuery);
            searchQuery.setText("Results for: " + query);
            System.out.println(query);
        }
    }*/

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.JARGON, true);
        startSearch(null, false, appData, false);
        return true;
    }

   
    }
