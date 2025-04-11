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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private DatabaseReference fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fdb = FirebaseDatabase.getInstance("https://techpatashala-d6821-default-rtdb.firebaseio.com/").getReference("users");

        EditText edtUsername = findViewById(R.id.edt_username);
        EditText edtEmail = findViewById(R.id.edt_email);
        EditText edtPassword = findViewById(R.id.edt_password);
        EditText edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        ImageButton btnShowPassword = findViewById(R.id.btn_show_password);
        Button btnRegister = findViewById(R.id.btn_register);
        btnShowPassword.setOnClickListener(view -> {
            if (!isPasswordVisible) {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnShowPassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
            } else {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnShowPassword.setImageResource(R.drawable.baseline_visibility_off);
            }
            isPasswordVisible = !isPasswordVisible;
        });
        btnRegister.setOnClickListener(view -> insertUser(edtUsername.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString()));
    }

    public void insertUser(String username, String email, String password) {
        User user = new User(username, email, password);
        fdb.push().setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignUpActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, UserPageActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
