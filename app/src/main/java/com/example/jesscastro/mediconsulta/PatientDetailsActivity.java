package com.example.jesscastro.mediconsulta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class PatientDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        Intent intent = getIntent();
        Toast.makeText(this, String.valueOf(intent.getExtras().getInt("patient_id")),Toast.LENGTH_LONG).show();
    }
}
