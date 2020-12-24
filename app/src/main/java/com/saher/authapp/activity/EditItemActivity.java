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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import com.saher.authapp.model.UserItem;
import com.squareup.picasso.Picasso;

import java.util.UUID;

//import com.bumptech.glide.Glide;

public class EditItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQ_CODE = 1;
    Toolbar viewItemToolbar;
    EditText et_name, et_price, et_phone, et_description;
    EditText et_location;
    String item_id=null;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    final CollectionReference itemRef=db.collection("Itembook");
    CollectionReference userItemRef=db.collection("useritem");
    int comingfromuseractivity=0;
    MenuItem save;
    MenuItem edit;
    MenuItem delete;
    MenuItem loveit;
    ImageView iv;
    Uri imageUri;
    FirebaseStorage firebasestorage;
    StorageReference ref;


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
        iv=findViewById(R.id.view_item_iv);
        firebasestorage=FirebaseStorage.getInstance();
        ref=firebasestorage.getReference();


        Intent intent=getIntent();
        comingfromuseractivity=intent.getIntExtra("COMING_FROM_USER_ACTIVITY",0);
        item_id=intent.getStringExtra("ITEM_ID");
       if(comingfromuseractivity==0){
        if(item_id==null){
            enableFields();
            clearFields();
        }
        else{
            fillItemToFields(item_id);
            disableFields();
        }}
       else {
           fillItemToFields(item_id);
           disableFields();
       }
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in,PICK_IMAGE_REQ_CODE);
            }
        });
    }

    private void fillItemToFields(String itemId){
      itemRef.whereEqualTo("uniqueID",itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
      @Override
      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
      Item it=queryDocumentSnapshots.getDocuments().get(0).toObject(Item.class);
      et_name.setText(it.getName().toString());
      et_location.setText(it.getLocation());
      et_price.setText(it.getPrice());
      et_phone.setText(it.getPhonenumber());
      et_description.setText(it.getDescription());
          if(it.getImage()!=null&&!it.getImage().isEmpty()){
             Picasso.with(EditItemActivity.this).load(Uri.parse(it.getImage())).into(iv);
          }
          else{iv.setImageResource(R.drawable.ic_shopping);}
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
         loveit=menu.findItem(R.id.loveit);
        save.setVisible(false);
        edit.setVisible(true);
        delete.setVisible(true);
        loveit.setVisible(false);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
                deleteItem(item_id);
                return true;
            case R.id.loveit:
                addtowatchlist();
                return true;
        }
        return true;
    }

    private void deleteItem(String itemId){
        itemRef.whereEqualTo("uniqueID",itemId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               DocumentSnapshot dd=queryDocumentSnapshots.getDocuments().get(0);
               dd.getReference().delete();
            }
        });
      finish();
    }

    private void addtowatchlist(){
        loveit.setIcon(R.drawable.ic_loveit);
        final String userid= FirebaseAuth.getInstance().getUid();
        String itemid=item_id;
        UserItem ui=new UserItem(userid,itemid);
        userItemRef.add(ui);
        Toast.makeText(EditItemActivity.this,"item has been added to your watchlist",Toast.LENGTH_LONG).show();

    }
    

    private void saveItem(){
        if(item_id==null){
        final String name=et_name.getText().toString();
        final String location=et_location.getText().toString();
        final String price=et_price.getText().toString();
        final String phone=et_phone.getText().toString();
        final String description=et_description.getText().toString();
        final String id= FirebaseAuth.getInstance().getUid();
        final String uniqueID= UUID.randomUUID().toString();
        String imagename=id+"/"+uniqueID+".jpg";
        if(name.trim().isEmpty()||location.trim().isEmpty()||price.trim().isEmpty()||phone.trim().isEmpty()||description.trim().isEmpty()||imageUri==null){
            Toast.makeText(this, "insert all the fields please", Toast.LENGTH_SHORT).show();
            return;
        }
        final StorageReference reff=ref.child(imagename);
        UploadTask uploadTask=reff.putFile(imageUri);



        Task<Uri>urlTask=uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditItemActivity.this,"your item is uploading...",Toast.LENGTH_LONG).show();
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reff.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri=task.getResult();
                    itemRef.add(new Item(name,location,price,phone,description,id,uniqueID,downloadUri.toString()));
                    Toast.makeText(EditItemActivity.this,"item added",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(EditItemActivity.this,"error",Toast.LENGTH_LONG).show();
                }
            }
        });
        }
        else{
            final String name=et_name.getText().toString();
            final String location=et_location.getText().toString();
            final String price=et_price.getText().toString();
            final String phone=et_phone.getText().toString();
            final String description=et_description.getText().toString();
            final String id= FirebaseAuth.getInstance().getUid();
            final String uniqueID=item_id;
            String imagename=id+"/"+uniqueID+".jpg";
            if(name.trim().isEmpty()||location.trim().isEmpty()||price.trim().isEmpty()||phone.trim().isEmpty()||description.trim().isEmpty()||imageUri==null){
                Toast.makeText(this, "insert all the fields please", Toast.LENGTH_SHORT).show();
                return;
            }
            final StorageReference reff=ref.child(imagename);
            UploadTask uploadTask=reff.putFile(imageUri);
            Task<Uri>urlTask=uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditItemActivity.this,"your item is being edited...",Toast.LENGTH_LONG).show();
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reff.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        final Item itt=new Item(name,location,price,phone,description,id,uniqueID,downloadUri.toString());
                        itemRef.whereEqualTo("uniqueID",item_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                DocumentSnapshot dd=queryDocumentSnapshots.getDocuments().get(0);
                                dd.getReference().set(itt);
                                Toast.makeText(EditItemActivity.this,"item edited",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }else{
                        Toast.makeText(EditItemActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQ_CODE&&resultCode==RESULT_OK){
            if(data!=null){
                imageUri=data.getData();
                Picasso.with(this).load(imageUri).into(iv);
            }
        }
    }
}