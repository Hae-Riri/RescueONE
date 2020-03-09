package com.example.rescueone.add_phones;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rescueone.R;
import com.example.rescueone.db_server.EmergencyContact;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPhonesDialog extends Dialog {

    EditText name;
    EditText number;
    TextView cancel;
    TextView save;

    DatabaseReference mDatabase;
    Context context;
    String uid;

    private Add_DialogListener mDialogListener;

    public AddPhonesDialog (Context context,String uid){
        super(context);
        this.context = context;
        this.uid = uid;
    }

    //Interface
    interface Add_DialogListener{
        void onSaveClicked(String name, String phone);
        void onCancelClicked();
    }

    //Listener
    public void setDialogListener(Add_DialogListener add_dialogListener){
        mDialogListener = add_dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_phones);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);

        cancel.setOnClickListener(view -> {
            dismiss();
        });

        save.setOnClickListener(view -> {
            ((AddPhonesActivity)context).addData(
                    new EmergencyContact(
                            name.getText().toString(),
                            number.getText().toString()));

            Log.e("A",uid+" "+number.getText().toString());
            mDatabase.child("users").child(uid).child("emergencyContact")
                    .child(number.getText().toString())
                    .setValue(name.getText().toString());

//            //AddReceiver Activity로 값 전달
//            mDialogListener.onSaveClicked(name.getText().toString(),
//                    number.getText().toString());

            dismiss();
        });
    }
}
