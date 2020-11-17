package com.saher.authapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class View_Item_Activity extends AppCompatActivity {
    Toolbar viewItemToolbar;
    TextInputEditText et_name, et_location, et_price, et_phone, et_description;
    int item_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__item_);
        viewItemToolbar = findViewById(R.id.view_item_activity_toolbar);
        setSupportActionBar(viewItemToolbar);
        et_name = findViewById(R.id.activity_view_item_name);
        et_location = findViewById(R.id.activity_view_item_location);
        et_price = findViewById(R.id.activity_view_item_price);
        et_phone = findViewById(R.id.activity_view_item_phone);
        et_description = findViewById(R.id.activity_view_item_location);
    }

    private void disableFields() {
        et_name.setEnabled(false);
        et_location.setEnabled(false);
        et_price.setEnabled(false);
        et_phone.setEnabled(false);
        et_description.setEnabled(false);
    }

    private void enableFields() {
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
        MenuItem save = menu.findItem(R.id.save);
        MenuItem edit = menu.findItem(R.id.edit);
        MenuItem delete = menu.findItem(R.id.delete);
        if (item_id == -1) {
            save.setVisible(true);
            edit.setVisible(false);
            delete.setVisible(false);
        } else {
            save.setVisible(false);
            edit.setVisible(true);
            delete.setVisible(true);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                saveItem();
                //Intent i=new Intent(View_Item_Activity.this,ProfileActivity.class);
                //startActivity(i);
        }
        return true;
    }
    private void saveItem(){
        String name=et_name.getText().toString();
        String location=et_location.getText().toString();
        String price=et_price.getText().toString();
        String phone=et_phone.getText().toString();
        String description=et_description.getText().toString();
        String id= FirebaseAuth.getInstance().getUid();
        if(name.trim().isEmpty()||location.trim().isEmpty()||price.trim().isEmpty()||phone.trim().isEmpty()||description.trim().isEmpty()){
            Toast.makeText(this, "insert all the fields please", Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionReference itembookRef= FirebaseFirestore.getInstance().collection("Itembook");
        itembookRef.add(new Item(name,location,price,phone,description,id));
        Toast.makeText(this,"item added",Toast.LENGTH_LONG).show();
        finish();
    }
}