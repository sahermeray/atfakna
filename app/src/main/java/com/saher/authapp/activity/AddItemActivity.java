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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saher.authapp.R;
import com.saher.authapp.model.Item;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQ_CODE = 1;
    Toolbar viewItemToolbar;
    EditText et_name, et_price, et_phone, et_description;
    EditText et_location;
    Uri imageUri;
    String itemId = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference itemRef = db.collection(Item.COLLECTION_NAME);
    MenuItem save;
    MenuItem delete;
    ImageView imageView;
    FirebaseStorage firebasestorage;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        viewItemToolbar = findViewById(R.id.activity_add_item_toolbar);
        setSupportActionBar(viewItemToolbar);
        et_name = findViewById(R.id.activity_add_item_name);
        et_location = findViewById(R.id.activity_add_item_location);
        et_price = findViewById(R.id.activity_add_item_price);
        et_phone = findViewById(R.id.activity_add_item_phone);
        et_description = findViewById(R.id.activity_add_item_description);
        imageView = findViewById(R.id.activity_add_item_image_view);
        firebasestorage = FirebaseStorage.getInstance();
        storageRef = firebasestorage.getReference();


        Intent intent = getIntent();
        itemId = intent.getStringExtra("ITEM_ID");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, PICK_IMAGE_REQ_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_item_menu, menu);
        menu.findItem(R.id.save).setVisible(true);
        menu.findItem(R.id.delete).setVisible(true);
        menu.findItem(R.id.edit).setVisible(false);
        menu.findItem(R.id.loveit).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveItem();
            case R.id.delete:
                deleteItem(itemId);
        }
        return true;
    }

    private void deleteItem(String itemId) {
        finish();
    }

    private void saveItem() {
        final String name = et_name.getText().toString();
        final String location = et_location.getText().toString();
        final String price = et_price.getText().toString();
        final String phone = et_phone.getText().toString();
        final String description = et_description.getText().toString();
        final String id = FirebaseAuth.getInstance().getUid();
        final String uniqueID = itemId;
        final String userEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (name.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "article name and description must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null != imageUri) {
            String imagePath = id + "/" + uniqueID + ".jpg";
            final StorageReference storageReference = storageRef.child(imagePath);
            UploadTask uploadTask = storageReference.putFile(imageUri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddItemActivity.this, "your item is being edited...", Toast.LENGTH_LONG).show();
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
                        final Item item = new Item(name, location, price, phone, description, id, downloadUri.toString(),userEmail);

                        itemRef.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getBaseContext(), documentReference.getId(), Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {
                        Toast.makeText(AddItemActivity.this, "error", Toast.LENGTH_LONG).show();
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
                Picasso.with(this).load(imageUri).into(imageView);
            }
        }
    }
}