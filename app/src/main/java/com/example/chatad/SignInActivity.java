package com.example.chatad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity
{
    EditText emailfield,passwordfield;
    String email,password;
    CircleImageView circleImageView;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        emailfield = findViewById(R.id.email);
        passwordfield = findViewById(R.id.password);
        circleImageView = findViewById(R.id.image);
        email = emailfield.getText().toString();
        password = passwordfield.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "enter your email", Toast.LENGTH_SHORT).show();
        }
    }


    public void signin(View view)
    {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Intent intent = new Intent(SignInActivity.this,StartActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }
}
