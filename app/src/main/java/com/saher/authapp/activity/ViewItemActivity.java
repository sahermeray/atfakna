package com.saher.authapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saher.authapp.R;
import com.saher.authapp.model.Item;
import com.saher.authapp.model.WatchedItem;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ViewItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQ_CODE = 1;
    Toolbar viewItemToolbar;
    TextView itemTitleView, itemLocationView, itemPriceView, itemPhoneView, itemDescriptionView,itemEmail;
    String itemId = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference itemsCollectionReference = db.collection(Item.COLLECTION_NAME);
    CollectionReference watchedItemCollectionReference = db.collection(WatchedItem.COLLECTION_NAME);
    int comingfromuseractivity = 0;
    MenuItem saveMenuItem;
    MenuItem editMenuItem;
    MenuItem deleteMenuItem;
    MenuItem watchMenuItem;
    ImageView previewImageView;
    Uri imageUri;
    StorageReference storageReference;
    boolean isItemWatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        viewItemToolbar = findViewById(R.id.item_activity_toolbar);
        setSupportActionBar(viewItemToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        itemTitleView = findViewById(R.id.item_title);
        itemLocationView = findViewById(R.id.activity_view_item_location);
        itemPriceView = findViewById(R.id.item_price);
        itemPhoneView = findViewById(R.id.activity_view_item_phone);
        itemDescriptionView = findViewById(R.id.activity_view_item_description);
        itemEmail=findViewById(R.id.activity_view_item_email);
        previewImageView = findViewById(R.id.view_item_iv);
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        comingfromuseractivity = intent.getIntExtra("COMING_FROM_USER_ACTIVITY", 0);
        itemId = intent.getStringExtra("ITEM_ID");
        if (comingfromuseractivity == 1) {
                fillItemToFields(itemId);
            }
        else if(comingfromuseractivity == 2) {
            fillsomeItemToFields(itemId);
        }
        }



    private void fillItemToFields(String itemId) {
        itemsCollectionReference.document(itemId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Item item = documentSnapshot.toObject(Item.class);
                    itemTitleView.setText(item.getName().toString());
                    itemLocationView.setText(item.getLocation());
                    itemPriceView.setText(item.getPrice());
                    itemPhoneView.setText(item.getPhonenumber());
                    itemDescriptionView.setText(item.getDescription());
                    itemEmail.setText(item.getUserEmail());
                    if (item.getImage() != null && !item.getImage().isEmpty()) {
                        Picasso.with(ViewItemActivity.this).load(Uri.parse(item.getImage())).into(previewImageView);
                    } else {
                        previewImageView.setImageResource(R.drawable.ic_shopping);
                    }
                }
            }
        });
        watchedItemCollectionReference.whereEqualTo("itemIdl", itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    return;
                }
                watchMenuItem.setIcon(R.drawable.ic_loveit);
                isItemWatched = true;
            }
        });
    }


    private void fillsomeItemToFields(String itemId) {
        itemsCollectionReference.document(itemId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Item item = documentSnapshot.toObject(Item.class);
                    itemTitleView.setText(item.getName().toString());
                    itemLocationView.setText(item.getLocation());
                    itemPriceView.setText(item.getPrice());
                    itemPhoneView.setText("create account to see phone number");
                    itemDescriptionView.setText(item.getDescription());
                    itemEmail.setText("create account to see email");
                    if (item.getImage() != null && !item.getImage().isEmpty()) {
                        Picasso.with(ViewItemActivity.this).load(Uri.parse(item.getImage())).into(previewImageView);
                    } else {
                        previewImageView.setImageResource(R.drawable.ic_shopping);
                    }
                }
            }
        });

        watchedItemCollectionReference.whereEqualTo("itemIdl", itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    return;
                }
                watchMenuItem.setIcon(R.drawable.ic_loveit);
                isItemWatched = true;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_item_menu, menu);
        saveMenuItem = menu.findItem(R.id.save);
        editMenuItem = menu.findItem(R.id.edit);
        deleteMenuItem = menu.findItem(R.id.delete);
        watchMenuItem = menu.findItem(R.id.loveit);
        saveMenuItem.setVisible(false);
        editMenuItem.setVisible(false);
        deleteMenuItem.setVisible(false);

        if(comingfromuseractivity==1){
        watchMenuItem.setVisible(true);
        watchMenuItem.setIcon(R.drawable.ic_watchlist);
        }else if(comingfromuseractivity==2){
          watchMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                return true;
            case R.id.edit:
                return true;
            case R.id.delete:
                return true;
            case R.id.loveit:
                if (isItemWatched) {
                    unwatchItem();
                } else {
                    addToWatchlist();
                }
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addToWatchlist() {
        watchMenuItem.setIcon(R.drawable.ic_loveit);
        final String userId = FirebaseAuth.getInstance().getUid();
        String item_id = this.itemId;
        WatchedItem watchedItem = new WatchedItem(userId, item_id);
        watchedItemCollectionReference.add(watchedItem);
        Toast.makeText(ViewItemActivity.this, "item has been added to your watchlist", Toast.LENGTH_LONG).show();
    }

    private void unwatchItem() {
        watchMenuItem.setIcon(R.drawable.ic_watchlist);
        final String userId = FirebaseAuth.getInstance().getUid();
        watchedItemCollectionReference
                .whereEqualTo("itemIdl", this.itemId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.iterator().hasNext()) {
                            queryDocumentSnapshots.iterator().next().getReference().delete();
                        }
                    }
                });
        Toast.makeText(ViewItemActivity.this, "item has been added to your watchlist", Toast.LENGTH_LONG).show();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                Picasso.with(this).load(imageUri).into(previewImageView);
            }
        }
    }
}