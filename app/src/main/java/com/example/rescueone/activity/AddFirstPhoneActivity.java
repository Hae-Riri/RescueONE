package com.example.rescueone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivityAddFirstPhoneBinding;
import com.example.rescueone.db_server.EmergencyContact;
import com.example.rescueone.db_server.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFirstPhoneActivity extends AppCompatActivity {

    EditText name;
    EditText phone;
    ImageButton check;
    Toolbar toolbar;

    ActivityAddFirstPhoneBinding mBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    String uid = currentUser.getUid();
    User user;
    ArrayList<EmergencyContact> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_first_phone);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        check = findViewById(R.id.addFirst);
        toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        check.setOnClickListener(view -> {
            new EmergencyContact(name.getText().toString(),
                    phone.getText().toString());
            Log.e("first add",uid+" "+phone.getText().toString());
            mDatabase.child("users").child(uid).child("emergencyContact").child(phone.getText().toString()).setValue(name.getText().toString());

            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }
    public void OnClick(View view){
        if(view.equals(R.id.root)){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBinding.name.getWindowToken(),0);
        }
    }
}
