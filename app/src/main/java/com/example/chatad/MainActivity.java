package com.example.chatad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatad.models.usermodel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    EditText usernamefield,emailfield,passwordfield,confirmpasswordfield;
    LinearLayout googlelin;
    Uri photo;
    String email,username,password,confirmpassword;
    CircleImageView circleImageView;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    int RC_SIGN_IN = 2002;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getData();
        fireBase();
        initviews();

        googlelin = findViewById(R.id.googlelin);
        googlelin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signIn();
            }
        });

        // Configure Google Sign In
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    private void fireBase()
    {
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        FirebaseUser user = auth.getCurrentUser();
        if(user!= null)
        {
            startActivity(new Intent(getApplicationContext(),StartActivity.class));
            finish();
        }
    }

    private void getData()
    {
        circleImageView = findViewById(R.id.userimage);
        usernamefield = findViewById(R.id.username);
        emailfield = findViewById(R.id.useremail);
        passwordfield = findViewById(R.id.userpassword);
        confirmpasswordfield = findViewById(R.id.userconfirmpassword);
    }

    private void signIn()
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN); // RC define by any number you want
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

    }


    private void initviews()
    {
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            { // crop image
                CropImage.activity()//ha5ood object mn el activity
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH) // the guidelines
                        .setAspectRatio(1,1) // 1 ,1 for square shape
                        .start(MainActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) //i have be sure result is okay because result could be return empty
            {
                if(result!=null)
                {
                    photo = result.getUri();
                    Picasso.get()
                            .load(photo)
                            .placeholder(R.drawable.ic_targe)
                            .error(R.drawable.ic_targe)
                            .into(circleImageView);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e)
            {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "done..", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void already(View view)
    {
        Intent intent = new Intent(MainActivity.this,SignInActivity.class);
        startActivity(intent);
    }

    public void register(View view)
    {
        email = emailfield.getText().toString();
        username = usernamefield.getText().toString();
        password = passwordfield.getText().toString();
        confirmpassword = confirmpasswordfield.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext() , "enter your email", Toast.LENGTH_SHORT).show();
            emailfield.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(getApplicationContext(), "enter your username", Toast.LENGTH_SHORT).show();
            usernamefield.requestFocus();
            return;
        }
        if (password.length()<6)
        {
            Toast.makeText(getApplicationContext(), "password is too short", Toast.LENGTH_SHORT).show();
            passwordfield.requestFocus();
            return;
        }
        if (!confirmpassword.equals(password))
        {
            Toast.makeText(getApplicationContext(), "password not matching", Toast.LENGTH_SHORT).show();
            confirmpasswordfield.requestFocus();
            return;
        }
        if(photo==null)
        {
            Toast.makeText(getApplicationContext(), "select your picture", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Wait..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER) ; // style bylf dayra
        progressDialog.setCancelable(false); // law dost fe le fady y3ml cancel wala la
        progressDialog.show();

        adduser(username,email,password);


    }

    private void adduser( final String username, final String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            String id = task.getResult().getUser().getUid(); // id to save data
                            uploadphoto(username,email,photo,id);
                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadphoto(final String username, final String email, Uri photo, final String id)
    {// method to save image in storage
        UploadTask uploadTask;
        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+photo.getLastPathSegment());//get image path  , the "/" to return into file
        uploadTask =storageReference.putFile(photo); // put file:used with images and pdf
        Task<Uri>task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                return null;
                //return storageReference.getDownloadUrl();
            }

        }).addOnCompleteListener(new OnCompleteListener<Uri>() // na mo4h m7taga el method continuation ana m7taga on complete
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                Uri imageuri = task.getResult();
                String pic_url = imageuri.toString();

                savatodb(username,email,pic_url,id);
            }
        });
    }

    private void savatodb(String username, String email, String pic_url,String id)
    {
        usermodel usermodel = new usermodel(username,email,pic_url);
        databaseReference.child("Users").child(id).setValue(usermodel);
        progressDialog.dismiss();
        startActivity(new Intent(getApplicationContext(),StartActivity.class));
        finish();
    }
}
