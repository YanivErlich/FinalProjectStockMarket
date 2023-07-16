package com.example.finalprojectstockmarket.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "EmailPasswordActivity";


    ImageView imgBack;
    EditText edtEmail, editPassword;
    Button btnLogin;
    TextView forgotTextView, tvSignUp;
    String email, passowrd;
    FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();

       initViews();
       setButtonsClickListeners();

    }

    private void setButtonsClickListeners() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = edtEmail.getText().toString().trim();
                passowrd = editPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(passowrd)){
                    editPassword.setError("Required!");
                    editPassword.requestFocus();
                    return;
                }
                signIn();
            }
        });

    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        edtEmail = findViewById(R.id.edtEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        forgotTextView = findViewById(R.id.forgotTextView);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(email, passowrd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Incorrect details", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    boolean isOnline(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return true;
        }
        return false;
    }
}