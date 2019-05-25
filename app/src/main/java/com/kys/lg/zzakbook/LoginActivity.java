package com.kys.lg.zzakbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kys.lg.zzakbook.Signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {


    private TextView signup;
    private Button login;
    private EditText login_emil,login_password;
    private String email,password;
    private FirebaseAuth mAuth;
     private FirebaseAuth.AuthStateListener authStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup=findViewById(R.id.signup);
        login=findViewById(R.id.login);
        login_emil=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);


        mAuth=FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=login_emil.getText().toString().trim();
                password=login_password.getText().toString().trim();
                if(email.equals("")||password.equals("")){
                    Toast.makeText(getApplicationContext(),"아이디나 비밀번호가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();

                }else {
                   signin();

                }

    }
});

        authStateListener= new FirebaseAuth.AuthStateListener() {
        @Override
         public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //현재 유저가 없으면
        if(user!=null){

        Intent i= new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);
        finish();

        }


        }
        };



        }

    private void signin() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        else {
                            //로그인
                           mAuth.addAuthStateListener(authStateListener);

                        }

                        // ...
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
     //  mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null) {

            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}
