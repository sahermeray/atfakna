package com.saher.authapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saher.authapp.model.Item;
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.activity.ViewItemActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ItemAdapter adapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference itemRef=db.collection("Itembook");
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.home_fragment);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });gxdb
        firebaseAuth=FirebaseAuth.getInstance();
        setUpRecyclerView();
        return root;
    }

    private void setUpRecyclerView(){
        final FirebaseUser useruser=firebaseAuth.getCurrentUser();
        Query query=itemRef.orderBy("name",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Item> options=new FirestoreRecyclerOptions.Builder<Item>().setQuery(query,Item.class).build();
        adapter=new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String Item_Id) {
                if(useruser!=null){
                Intent i=new Intent(getContext(), ViewItemActivity.class);
                i.putExtra("COMING_FROM_USER_ACTIVITY",1);
                i.putExtra("ITEM_ID",Item_Id);
                startActivity(i);}
                else if(useruser==null){
                    Toast.makeText(getContext(),"you have to create account or login first",Toast.LENGTH_LONG).show();
                }
            }

        },getContext());
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
    }
}