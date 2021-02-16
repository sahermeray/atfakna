package com.saher.authapp.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saher.authapp.ProfileActivity;
import com.saher.authapp.R;
import com.saher.authapp.model.UserSetting;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SearchView.OnQueryTextListener queryTextListener;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSetting userSetting;
    public static ImageView navimage;
    public static TextView navusername;
    public int x=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        navusername=(TextView)headerView.findViewById(R.id.nav_prof_name);
        navimage=(ImageView)headerView.findViewById(R.id.nav_prof_image);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        Intent i = getIntent();
        final int y = i.getIntExtra("message", 0);
        final int z=i.getIntExtra("comefromface",0);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser useriuser = firebaseAuth.getCurrentUser();
        if(useriuser!=null) {
            for (UserInfo user : useriuser.getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    x = 10;
                    break;
                }
            }
        }
        //if(useriuser!=null){
            //UserInfo user=useriuser.getProviderData().get(0);
        //}


        if ((useriuser != null && useriuser.isEmailVerified())||x==10||y==10||z==5) {
            db.collection(UserSetting.COLLECTION_NAME)
                    .whereEqualTo(UserSetting.FIELD_USER_ID, useriuser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                userSetting = queryDocumentSnapshots
                                        .getDocuments()
                                        .get(0)
                                        .toObject(UserSetting.class);
                                if (userSetting != null && userSetting.getUserImage() != null && !userSetting.getUserImage().equals("")) {
                                    //ImageView profileImage = findViewById(R.id.nav_prof_image);
                                    Picasso.with(getBaseContext()).load(userSetting.getUserImage()).into(navimage);
                                }
                            }
                        }
                    });


            navusername.setText(useriuser.getEmail());
            navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_gallery).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_slideshow).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_create_account).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_gallery).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_slideshow).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_create_account).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        }


        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(navusername.getText().length()!=0){
                Intent inte=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(inte);}else{
                    return;
                }
            }
        });


        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id == R.id.nav_logout) {
                    firebaseAuth.signOut();
                    finishAffinity();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (id == R.id.nav_create_account) {
                    Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(i);
                } else if (id == R.id.nav_login) {
                    Intent intt = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intt);
                }

                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getBaseContext(), SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}