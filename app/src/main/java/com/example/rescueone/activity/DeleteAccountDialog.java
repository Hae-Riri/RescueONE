package com.example.rescueone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.add_phones.AddPhonesDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountDialog extends Dialog {

    Context context;
    String uid;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    TextView yes;
    TextView no;

    CustomDialogListener customDialogListener;

    public DeleteAccountDialog (Context context){
        super(context);
        this.context = context;
    }

    interface CustomDialogListener{
        void onPositiveClicked(String ans);
        void onNegaticeClicked(String ans);
    }

    public void setDialogLister(CustomDialogListener customDialogListener){
        this.customDialogListener=customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_account);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth =FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        uid = currentUser.getUid();

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String ans = "no";
//                customDialogListener.onNegaticeClicked(ans);
                cancel();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String ans = "delete";
//                customDialogListener.onPositiveClicked(ans);
                mAuth.signOut();
                    mAuth.getCurrentUser().delete();
                    mDatabase.child("users").child(uid).removeValue();
                    Toast.makeText(getContext(), "회원탈퇴합니다.",
                            Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

    }
}
