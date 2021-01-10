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

//import com.bumptech.glide.Glide;

public class ViewItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQ_CODE = 1;
    Toolbar viewItemToolbar;
    TextView itemTitleView, itemLocationView, itemPriceView, itemPhoneView, itemDescriptionView;
    String itemId = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference itemRef = db.collection(Item.COLLECTION_NAME);
    CollectionReference watchedItemCollectionReference = db.collection(WatchedItem.COLLECTION_NAME);
    int comingfromuseractivity = 0;
    MenuItem saveMenuItem;
    MenuItem editMenuItem;
    MenuItem deleteMenuItem;
    MenuItem watchMenuItem;
    ImageView previewImageView;
    Uri imageUri;
    StorageReference storageReference;

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
        previewImageView = findViewById(R.id.view_item_iv);
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        comingfromuseractivity = intent.getIntExtra("COMING_FROM_USER_ACTIVITY", 0);
        itemId = intent.getStringExtra("ITEM_ID");
        if (comingfromuseractivity == 0) {
            if (itemId != null) {
                fillItemToFields(itemId);
            }
        } else {
            fillItemToFields(itemId);
        }
    }

    private void fillItemToFields(String itemId) {
        itemRef.document(itemId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Item item = documentSnapshot.toObject(Item.class);
                    itemTitleView.setText(item.getName().toString());
                    itemLocationView.setText(item.getLocation());
                    itemPriceView.setText(item.getPrice());
                    itemPhoneView.setText(item.getPhoneNumber());
                    itemDescriptionView.setText(item.getDescription());
                    if (item.getImage() != null && !item.getImage().isEmpty()) {
                        Picasso.with(ViewItemActivity.this).load(Uri.parse(item.getImage())).into(previewImageView);
                    } else {
                        previewImageView.setImageResource(R.drawable.ic_shopping);
                    }
                }

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
        watchMenuItem.setVisible(true);
        watchMenuItem.setIcon(R.drawable.ic_watchlist);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveItem();
                return true;
            case R.id.edit:
                saveMenuItem.setVisible(true);
                deleteMenuItem.setVisible(false);
                editMenuItem.setVisible(false);
                return true;
            case R.id.delete:
                deleteItem(itemId);
                return true;
            case R.id.loveit:
                addToWatchlist();
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

    private void deleteItem(String itemId) {
        itemRef.whereEqualTo("uniqueID", itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot dd = queryDocumentSnapshots.getDocuments().get(0);
                dd.getReference().delete();
            }
        });
        finish();
    }

    private void addToWatchlist() {
        watchMenuItem.setIcon(R.drawable.ic_loveit);
        final String userId = FirebaseAuth.getInstance().getUid();
        String item_id = this.itemId;
        WatchedItem watchedItem = new WatchedItem(userId, item_id);
        watchedItemCollectionReference.add(watchedItem);
        Toast.makeText(ViewItemActivity.this, "item has been added to your watchlist", Toast.LENGTH_LONG).show();
    }

    private void saveItem() {
        if (itemId == null) {
            final String name = itemTitleView.getText().toString();
            final String location = itemLocationView.getText().toString();
            final String price = itemPriceView.getText().toString();
            final String phone = itemPhoneView.getText().toString();
            final String description = itemDescriptionView.getText().toString();
            final String userId = FirebaseAuth.getInstance().getUid();
            final String uniqueID = UUID.randomUUID().toString();
            String imageName = userId + "/" + uniqueID + ".jpg";
            if (name.trim().isEmpty() || location.trim().isEmpty() || price.trim().isEmpty() || phone.trim().isEmpty() || description.trim().isEmpty() || imageUri == null) {
                Toast.makeText(this, "insert all the fields please", Toast.LENGTH_SHORT).show();
                return;
            }
            final StorageReference reff = storageReference.child(imageName);
            UploadTask uploadTask = reff.putFile(imageUri);


            Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ViewItemActivity.this, "your item is uploading...", Toast.LENGTH_LONG).show();
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reff.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        itemRef.add(new Item(name, location, price, phone, description, userId, uniqueID, downloadUri.toString()));
                        Toast.makeText(ViewItemActivity.this, "item added", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ViewItemActivity.this, "error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            final String name = itemTitleView.getText().toString();
            final String location = itemLocationView.getText().toString();
            final String price = itemPriceView.getText().toString();
            final String phone = itemPhoneView.getText().toString();
            final String description = itemDescriptionView.getText().toString();
            final String id = FirebaseAuth.getInstance().getUid();
            final String uniqueID = itemId;
            String imagename = id + "/" + uniqueID + ".jpg";
            if (name.trim().isEmpty() || location.trim().isEmpty() || price.trim().isEmpty() || phone.trim().isEmpty() || description.trim().isEmpty() || imageUri == null) {
                Toast.makeText(this, "insert all the fields please", Toast.LENGTH_SHORT).show();
                return;
            }
            final StorageReference reff = storageReference.child(imagename);
            UploadTask uploadTask = reff.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ViewItemActivity.this, "your item is being edited...", Toast.LENGTH_LONG).show();
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reff.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        final Item itt = new Item(name, location, price, phone, description, id, uniqueID, downloadUri.toString());
                        itemRef.whereEqualTo("uniqueID", itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                DocumentSnapshot dd = queryDocumentSnapshots.getDocuments().get(0);
                                dd.getReference().set(itt);
                                Toast.makeText(ViewItemActivity.this, "item edited", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(ViewItemActivity.this, "error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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