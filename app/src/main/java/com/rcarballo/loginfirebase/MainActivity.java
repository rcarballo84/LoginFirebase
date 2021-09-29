package com.rcarballo.loginfirebase;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

enum ProviderType {
    FACEBOOK
}

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private EditText tvNombre;
    private EditText tvApellido;
    private EditText tvEdad;
    private EditText tvFechaNac;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String provider;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        provider = getProvider(user);

        setup(provider);

        if (user != null){
            String name = user.getDisplayName();
            uid = user.getUid();

            TextView tvBienvenido = findViewById(R.id.tvBienvenido);

            tvBienvenido.setText(name);
        }else{
            goLoginScreen();
        }
    }

    private String getProvider(FirebaseUser user) {
        String provider = "";
        List<? extends UserInfo> infos = user.getProviderData();

        for (UserInfo ui : infos) {
            if (ui.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                provider = ui.getProviderId();
            }else if (ui.getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)) {
                provider = ui.getProviderId();
            }
        }
        return provider;
    }

    private void setup(String provider) {
        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);
        tvEdad = findViewById(R.id.tvEdad);
        tvFechaNac = findViewById(R.id.tvFechaNac);

        View btnLogout = findViewById(R.id.btnLogout);
        View btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(view -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("clientes");

            String nombre = tvNombre.getText().toString();
            String apellido = tvApellido.getText().toString();
            int edad = Integer.parseInt(tvEdad.getText().toString());
            String fechaNac = tvFechaNac.getText().toString();

            Cliente cliente = new Cliente(nombre, apellido, edad, fechaNac);
            registrarUsuario(uid, cliente);
        });

        btnLogout.setOnClickListener(view -> {
            if (provider.equals(ProviderType.FACEBOOK.name())){
                LoginManager.getInstance().logOut();
            }
            FirebaseAuth.getInstance().signOut();
            goLoginScreen();
        });

        final View dialogDateView = View.inflate(this, R.layout.date_picker, null);
        final AlertDialog alertDialogDate = new AlertDialog.Builder(this).create();
        tvFechaNac.setOnClickListener(view -> {
            alertDialogDate.setView(dialogDateView);
            alertDialogDate.show();
        });

        dialogDateView.findViewById(R.id.date_time_set).setOnClickListener(view -> {
            DatePicker datePicker = dialogDateView.findViewById(R.id.date_picker);

            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth()
            );

            Date fecha = new Date();
            fecha.setTime(calendar.getTimeInMillis());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

            tvFechaNac.setText(formatter.format(fecha));

            alertDialogDate.dismiss();
        });
    }

    public void registrarUsuario(String userId, Cliente cliente) {
        reference.child(userId).child(cliente.getUserName()).setValue(cliente)
                .addOnSuccessListener(aVoid -> {
                    // Write was successful!
                    Toast.makeText(this, "Se cargaron los datos.", Toast.LENGTH_LONG ).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e -> {
                    // Write failed
                    Toast.makeText(this, "Hubo una falla en la carga de datos.", Toast.LENGTH_LONG ).show();
                });

        //reference.child("users").child(userId).setValue(cliente);
    }

    private void limpiarCampos() {
        tvNombre.setText("");
        tvApellido.setText("");
        tvEdad.setText("");
        tvFechaNac.setText("");
    }

    private void goLoginScreen(){
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
};
