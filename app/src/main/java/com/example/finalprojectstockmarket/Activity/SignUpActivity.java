package com.example.finalprojectstockmarket.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectstockmarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    ImageView imgBack;
    EditText edtEmail, edtPassword, edtName, edtCode;
    Button btnRegister;
    TextView tvSignIn;
    private FirebaseAuth mAuth;

    public String name, email, password, credit, id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setButtonsClickListeners();


    }

    private void setButtonsClickListeners() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtName.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                credit = edtCode.getText().toString().trim();

                if (name.isEmpty()) {
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()==false){
                    edtEmail.setError("Invalid email format!");
                    edtEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    edtPassword.setError("Required!");
                    edtPassword.requestFocus();
                    return;
                }
                if (password.length()<6) {
                    edtPassword.setError("Min 6 characters!");
                    edtPassword.requestFocus();
                    return;
                }
                if (credit.isEmpty()) {
                    credit = "0";
                }
                createAccount(email, password);
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.editPassword);
        edtCode = findViewById(R.id.edtCode);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void createAccount(final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserData();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    private void saveUserData() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("CreditAmount");
                                dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(credit);
                                Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }




}