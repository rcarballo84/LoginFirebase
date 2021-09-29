package com.rcarballo.loginfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class NumberPhoneActivity extends AppCompatActivity {

    private Spinner spinnerCountry;
    private EditText textNumberPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_phone);

        spinnerCountry = findViewById(R.id.spinnerCountries);
        spinnerCountry.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        textNumberPhone = findViewById(R.id.editTextMobile);
        View btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(view -> {
            String code = CountryData.countryAreaCodes[spinnerCountry.getSelectedItemPosition()];
            String number = textNumberPhone.getText().toString().trim();
            if (number.isEmpty()){
                textNumberPhone.setError("El número es obligatorio");
                textNumberPhone.requestFocus();
                return;
            }
            String phoneNumber = "+" + code + number;

            goVerifyPhoneActivity(phoneNumber);
        });
    }

    private void goVerifyPhoneActivity(String phoneNumber) {
        Intent intent = new Intent(NumberPhoneActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }
}