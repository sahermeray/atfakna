package com.saher.authapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQ_CODE = 1;
    Toolbar viewItemToolbar;
    EditText et_name, et_price, et_phone, et_description;
    EditText et_location;
    String itemId = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference itemRef = db.collection(Item.COLLECTION_NAME);
    CollectionReference userItemRef = db.collection(WatchedItem.COLLECTION_NAME);
    int isFromUserActivity = 0;
    MenuItem save;
    MenuItem edit;
    MenuItem delete;
    MenuItem loveit;
    ImageView iv;
    Uri imageUri;
    FirebaseStorage firebasestorage;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        viewItemToolbar = findViewById(R.id.edit_item_activity_toolbar);
        setSupportActionBar(viewItemToolbar);
        et_name = findViewById(R.id.activity_view_item_name);
        et_location = findViewById(R.id.activity_view_item_location);
        et_price = findViewById(R.id.activity_view_item_price);
        et_phone = findViewById(R.id.activity_view_item_phone);
        et_description = findViewById(R.id.activity_view_item_description);
        iv = findViewById(R.id.view_item_iv);
        firebasestorage = FirebaseStorage.getInstance();
        storageRef = firebasestorage.getReference();


        Intent intent = getIntent();
        isFromUserActivity = intent.getIntExtra("COMING_FROM_USER_ACTIVITY", 0);
        itemId = intent.getStringExtra("ITEM_ID");
        if (isFromUserActivity == 0) {
            if (itemId == null) {
                enableFields();
                clearFields();
            } else {
                fillItemToFields(itemId);
                disableFields();
            }
        } else {
            fillItemToFields(itemId);
            disableFields();
        }
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, PICK_IMAGE_REQ_CODE);
            }
        });
    }

    private void fillItemToFields(String itemId) {
        itemRef.document(itemId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    return;
                }

                Item it = documentSnapshot.toObject(Item.class);
                et_name.setText(it.getName().toString());
                et_location.setText(it.getLocation());
                et_price.setText(it.getPrice());
                et_phone.setText(it.getPhonenumber());
                et_description.setText(it.getDescription());
                if (it.getImage() != null && !it.getImage().isEmpty()) {
                    Picasso.with(EditItemActivity.this).load(Uri.parse(it.getImage())).into(iv);
                } else {
                    iv.setImageResource(R.drawable.ic_shopping);
                }
            }
        });
    }

    private void disableFields() {
        iv.setEnabled(false);
        et_name.setEnabled(false);
        et_location.setEnabled(false);
        et_price.setEnabled(false);
        et_phone.setEnabled(false);
        et_description.setEnabled(false);
    }

    private void enableFields() {
        iv.setEnabled(true);
        et_name.setEnabled(true);
        et_location.setEnabled(true);
        et_price.setEnabled(true);
        et_phone.setEnabled(true);
        et_description.setEnabled(true);
    }

    private void clearFields() {
        et_name.setText(null);
        et_location.setText(null);
        et_price.setText(null);
        et_phone.setText(null);
        et_description.setText(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_item_menu, menu);
        save = menu.findItem(R.id.save);
        edit = menu.findItem(R.id.edit);
        delete = menu.findItem(R.id.delete);
        loveit = menu.findItem(R.id.loveit);
        save.setVisible(false);
        edit.setVisible(true);
        delete.setVisible(true);
        loveit.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveItem();
                return true;
            case R.id.edit:
                save.setVisible(true);
                delete.setVisible(false);
                edit.setVisible(false);
                enableFields();
                return true;
            case R.id.delete:
                deleteItem(itemId);
                return true;
            case R.id.loveit:
                addToWatchlist();
                return true;
        }
        return true;
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
        loveit.setIcon(R.drawable.ic_loveit);
        final String userid = FirebaseAuth.getInstance().getUid();
        String itemId = this.itemId;
        WatchedItem ui = new WatchedItem(userid, itemId);
        userItemRef.add(ui);
        Toast.makeText(EditItemActivity.this, "item has been added to your watchlist", Toast.LENGTH_LONG).show();

    }


    private void saveItem() {
        final String name = et_name.getText().toString();
        final String location = et_location.getText().toString();
        final String price = et_price.getText().toString();
        final String phone = et_phone.getText().toString();
        final String description = et_description.getText().toString();
        final String id = FirebaseAuth.getInstance().getUid();
        final String uniqueID = itemId;

        if (name.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "article name and description must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        itemRef.whereEqualTo("uniqueID", itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                Item existingItem = documentSnapshot.toObject(Item.class);

                if (null == existingItem) {
                    return;
                }

                if (null != imageUri) {
                    String imagePath = id + "/" + uniqueID + ".jpg";
                    final StorageReference storageReference = storageRef.child(imagePath);
                    UploadTask uploadTask = storageReference.putFile(imageUri);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditItemActivity.this, "your item is being edited...", Toast.LENGTH_LONG).show();
                        }
                    }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                final Item item = new Item(
                                        name,
                                        location,
                                        price,
                                        phone,
                                        description,
                                        id,
                                        uniqueID,
                                        downloadUri.toString()
                                );
                                updateItem(documentSnapshot, item);

                            } else {
                                Toast.makeText(EditItemActivity.this, "error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    final Item item = new Item(
                            name,
                            location,
                            price,
                            phone,
                            description,
                            id,
                            uniqueID,
                            existingItem.getImage());
                    updateItem(documentSnapshot, item);
                }
                Toast.makeText(EditItemActivity.this, "item edited", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void updateItem(DocumentSnapshot reference, Item item) {
        reference.getReference().set(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                Picasso.with(this).load(imageUri).into(iv);
            }
        }
    }
}