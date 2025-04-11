package com.example.techpatashala;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private DatabaseReference fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fdb = FirebaseDatabase.getInstance("https://techpatashala-d6821-default-rtdb.firebaseio.com/").getReference("users");

        EditText edtUsername = findViewById(R.id.edt_username);
        EditText edtPassword = findViewById(R.id.edt_password);
        ImageButton btnShowPassword = findViewById(R.id.btn_show_password);
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnShowPassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnShowPassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
            } else {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnShowPassword.setImageResource(R.drawable.baseline_visibility_off);
            }
            isPasswordVisible = !isPasswordVisible;
        });
        btnSubmit.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                verifyUser(username, password);
            }
        });
    }
    private void verifyUser(String username, String password) {
        fdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String dbUsername = userSnapshot.child("username").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    if (dbUsername != null && dbPassword != null && dbUsername.equals(username) && dbPassword.equals(password)) {
                        userFound = true;
                        break;
                    }
                }

                if (userFound) {
                    Toast.makeText(SignInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignInActivity.this, UserPageActivity.class);
                    intent.putExtra("username", username);  // Pass the logged-in username

                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(SignInActivity.this, "Wrong Credentials! Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignInActivity.this, "Database Error! Try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
