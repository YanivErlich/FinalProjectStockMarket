package com.example.finalprojectstockmarket.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectstockmarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    EditText edtName;
    int count = 0;
    Button btnLogOut, btnUpdate;
    ImageView imgEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setButtonsClickListeners();
        edtName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        edtName.setEnabled(false);


    }



    private void  setButtonsClickListeners(){
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2 != 0) {
                    edtName.setEnabled(true);
                    btnUpdate.setVisibility(View.VISIBLE);
                }else {
                    edtName.setEnabled(false);
                    btnUpdate.setVisibility(View.GONE);
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                if(name.isEmpty()){
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build();
                firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Name updated successfully", Toast.LENGTH_SHORT).show();
                            edtName.setEnabled(false);
                            btnUpdate.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this).toBundle());
                finish();
            }
        });

    }
    private void initViews() {
        edtName = findViewById(R.id.edtName);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogOut = findViewById(R.id.btnLogOut);
        imgEdit = findViewById(R.id.imgEdit);
    }
}