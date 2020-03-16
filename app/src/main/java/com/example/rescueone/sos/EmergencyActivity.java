package com.example.rescueone.sos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.rescueone.R;

public class EmergencyActivity extends AppCompatActivity {
    TextView emergencyData;
    String title;
    String body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        emergencyData = findViewById(R.id.emergencyData);

        Intent intent = getIntent();
        title = intent.getExtras().getString("TITLE");
        body = intent.getExtras().getString("BODY");
        emergencyData.setText(title+"\n"+body);
    }
}
