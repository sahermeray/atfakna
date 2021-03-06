package com.saher.authapp.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saher.authapp.OnRecyclerViewItemClickListener;
import com.saher.authapp.R;
import com.saher.authapp.activity.HomeActivity;
import com.saher.authapp.activity.ViewItemActivity;
import com.saher.authapp.adapter.ItemAdapter;
import com.saher.authapp.model.Item;

public class HomeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference itemsCollectionReference = db.collection(Item.COLLECTION_NAME);
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    TextView verificationtv;
    public int h=0;
    public int d=0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.home_fragment);
        firebaseAuth = FirebaseAuth.getInstance();
        verificationtv = root.findViewById(R.id.verification_text);
        verificationtv.setText("");

        SharedPreferences mpref = this.getActivity().getSharedPreferences("emailandpassword", 0);
        final String userPaswd = mpref.getString("password", "");
        final String userEmail = mpref.getString("email", "");
        //SharedPreferences mpref2=this.getActivity().getSharedPreferences("face",0);
        //final String signwithface=mpref2.getString("signinwithfacebook","");

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();


        if(user!=null) {
            for (UserInfo userinfo : user.getProviderData()) {
                if ((userinfo.getProviderId().equals("facebook.com"))||(userinfo.getProviderId().equals("google.com"))) {
                    d = 10;
                    break;
                }
            }
        }
        if(d!=10) {
            if (userEmail != "" && userPaswd != "" && user != null) {
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "not successful", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(getContext(), "welcome", Toast.LENGTH_LONG).show();

                            if (!user.isEmailVerified()) {


                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getContext(), "we sent you a link..please verify your email address and press continue", Toast.LENGTH_LONG).show();
                                        verificationtv.setText("please verify your email and CLICK HERE");
                                    }
                                });

                            } else if (user.isEmailVerified()) {
                                verificationtv.setText("");


                            }

                        }
                    }
                });
            }
        }

        verificationtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser usertask = firebaseAuth.getCurrentUser();
                usertask.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user.isEmailVerified()) {
                            verificationtv.setText("");
                            HomeActivity.navusername.setText(user.getEmail());


                            Intent intent = new Intent(getActivity().getBaseContext(), HomeActivity.class);
                            intent.putExtra("message", 10);
                            getActivity().startActivity(intent);

                        } else {
                            Toast.makeText(getContext(), "please verify your email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        setUpRecyclerView();

        return root;
    }

    private void setUpRecyclerView() {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null) {
            for (UserInfo user : currentUser.getProviderData()) {
                if ((user.getProviderId().equals("facebook.com"))||(user.getProviderId().equals("google.com"))) {
                    h = 10;
                    break;
                }
            }
        }
        Query query = itemsCollectionReference.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        ItemAdapter adapter = new ItemAdapter(options, new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String Item_Id) {
                if ((currentUser != null && currentUser.isEmailVerified())||h==10) {
                    Intent i = new Intent(getContext(), ViewItemActivity.class);
                    i.putExtra("COMING_FROM_USER_ACTIVITY", 1);
                    i.putExtra("ITEM_ID", Item_Id);
                    startActivity(i);
                } else if (currentUser == null) {
                    Intent i = new Intent(getContext(), ViewItemActivity.class);
                    i.putExtra("COMING_FROM_USER_ACTIVITY", 2);
                    i.putExtra("ITEM_ID", Item_Id);
                    startActivity(i);
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