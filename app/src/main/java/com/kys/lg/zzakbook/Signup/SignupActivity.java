package com.kys.lg.zzakbook.Signup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kys.lg.zzakbook.LoginActivity;
import com.kys.lg.zzakbook.R;
import com.kys.lg.zzakbook.model.User;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edittext_email, edittext_password,edittext_name;
    private String email, password;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edittext_email = findViewById(R.id.signup_email);
        edittext_password = findViewById(R.id.signup_password);
        signup = findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(click);


    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            email = edittext_email.getText().toString().trim();
            password = edittext_password.getText().toString().trim();

            if (email.equals("") || password.equals("") || edittext_email.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "아이디나 비밀번호나 이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();

            } else {
                createAccount();
                FirebaseDatabase.getInstance().getReference().child("name").child(FirebaseAuth.getInstance().getUid())
                        .setValue(edittext_name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                    }
                });
            }
        }
    };

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final String uid = task.getResult().getUser().getUid();

                            User use = new User(email, password);

                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(use).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                                }
                            });
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent i = new Intent(SignupActivity.this,LoginActivity.class);
                            startActivity(i);

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignupActivity.this, "회원가입이 실패 했습니다.\n" + " 다시 시도해주세에요.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}


