package com.saher.authapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.saher.authapp.activity.ViewItemActivity;
import com.saher.authapp.model.Item;
import com.saher.authapp.model.UserSetting;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    TextView profileEmailTextview;
    ImageView profileImage;
    Spinner profileLanguage;
    Button profileSave;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userSettingsCollectionReference = db.collection("UserSetting");
    String language,country,userId;
    Uri imageUri;
    StorageReference storageReference;
    Toolbar profileToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileToolbar = findViewById(R.id.profile_activity_toolbar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference();
        ccp=findViewById(R.id.profile_country);
        profileEmailTextview=findViewById(R.id.profile_email);
        profileImage=findViewById(R.id.profile_image);
        profileLanguage=findViewById(R.id.profile_language);
        profileSave=findViewById(R.id.profile_save);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        userId=user.getUid();
        profileEmailTextview.setText(user.getEmail());
        country=ccp.getDefaultCountryName();

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=ccp.getSelectedCountryName();
            }
        });

        profileLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        language="";
                        break;
                    case 1:
                        language="English";
                        break;
                    case 2:
                        language="Arabic";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, 1);
            }
        });

        profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(language.trim().isEmpty()||country.trim().isEmpty()||imageUri==null){
                    Toast.makeText(getBaseContext(),"please choose all the fields",Toast.LENGTH_LONG).show();
                }else{
                    final String sameid=userId;
                    final String newCountry=country;
                    final String newLanguage=language;

                    final StorageReference reff = storageReference.child(userId);
                    UploadTask uploadTask = reff.putFile(imageUri);


                    Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "your profile is uploading...", Toast.LENGTH_LONG).show();
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
                               final UserSetting usus=new UserSetting(sameid,newCountry,downloadUri.toString(),newLanguage);
                                userSettingsCollectionReference.whereEqualTo("UserId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Toast.makeText(ProfileActivity.this,"gooooood",Toast.LENGTH_LONG).show();
                                        DocumentSnapshot ee = queryDocumentSnapshots.getDocuments().get(0);
                                        ee.getReference().set(usus);
                                        Toast.makeText(ProfileActivity.this, "profile edited", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                Picasso.with(this).load(imageUri).into(profileImage);
            }
        }
    }




}