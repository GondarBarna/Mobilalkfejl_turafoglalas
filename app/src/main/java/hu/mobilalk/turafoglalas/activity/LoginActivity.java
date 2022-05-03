package hu.mobilalk.turafoglalas.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import hu.mobilalk.turafoglalas.R;

public class LoginActivity extends AppCompatActivity
{
    private static final String LOG_TAG = LoginActivity.class.getName();

    EditText emailInput;
    EditText passwordInput;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);

        preferences = getSharedPreferences(Objects.requireNonNull(LoginActivity.class.getPackage()).toString(), MODE_PRIVATE);

        emailInput.setText(preferences.getString("email_value", ""));
        passwordInput.setText(preferences.getString("password_value", ""));

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void login(View view)
    {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(email.equals("") || password.equals(""))
        {
            Toast.makeText(LoginActivity.this, "A mezők nem lehetnek üresek!", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(success ->{
                Log.d(LOG_TAG, "Successful login (e-mail and password)");
                launchTours();
            })
            .addOnFailureListener(fail -> {
                Log.d(LOG_TAG, "Login failed (e-mail and password)");
                Toast.makeText(LoginActivity.this, "A bejelentkezés sikertelen. Kérlek, próbáld meg újra!", Toast.LENGTH_LONG).show();
            });
    }

    private void launchTours()
    {
        startActivity(new Intent(this, TourMenuActivity.class));
    }

    public void anonymusLogin(View view)
    {
        firebaseAuth.signInAnonymously()
            .addOnSuccessListener(success -> {
                Log.d(LOG_TAG, "Successful login (guest)");
                launchTours();
            })
            .addOnFailureListener(fail -> {
                Log.d(LOG_TAG, "Login failed (guest)");
                Toast.makeText(LoginActivity.this, "A bejelentkezés sikertelen. Kérlek, próbáld meg újra!", Toast.LENGTH_LONG).show();
            });
    }

    public void register(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email_value", emailInput.getText().toString());
        editor.putString("password_value", passwordInput.getText().toString());
        editor.apply();
    }
}