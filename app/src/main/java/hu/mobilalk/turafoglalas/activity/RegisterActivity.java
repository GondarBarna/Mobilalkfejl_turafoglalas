package hu.mobilalk.turafoglalas.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import hu.mobilalk.turafoglalas.R;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity
{
    private static final String LOG_TAG = RegisterActivity.class.getName();

    EditText emailInput;
    EditText passwordInput;
    EditText passwordAgainInput;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        passwordAgainInput = findViewById(R.id.input_password_again);

        preferences = getSharedPreferences(Objects.requireNonNull(RegisterActivity.class.getPackage()).toString(), MODE_PRIVATE);

        emailInput.setText(preferences.getString("email", ""));

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void register(View view)
    {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordAgain = passwordAgainInput.getText().toString();

        if("".equals(email) || "".equals(password) || "".equals(passwordAgain))
        {
            Toast.makeText(RegisterActivity.this, "A mezők nem lehetnek üresek!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(passwordAgain))
        {
            Toast.makeText(RegisterActivity.this, "A két jelszó nem egyezik meg!", Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length() < 6)
        {
            Toast.makeText(RegisterActivity.this, "A jelszónak legalább 6 karakter hosszúnak kell lennie!", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(success -> {
                Log.d(LOG_TAG, "Successful registration");
                finish();
            })
            .addOnFailureListener(fail -> {
                Log.d(LOG_TAG, "Registration failed");
                Toast.makeText(RegisterActivity.this, "A regisztráció sikertelen, kérlek póbáld meg újra!", Toast.LENGTH_LONG).show();
            });
    }

    public void back(View view)
    {
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", emailInput.getText().toString());
        editor.apply();
    }
}
