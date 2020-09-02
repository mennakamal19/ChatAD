package com.example.chatad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatad.models.usermodel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    EditText usernamefield,emailfield,passwordfield,confirmpasswordfield;
    Button signwithgoogle,signwithfacebook;
    String email,username,password,confirmpassword;

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    int RC_SIGN_IN = 2002;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fireBase();
        getData();

        // Configure Google Sign In
        signwithgoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signInWithGoogle();
            }
        });

        // Configure Facebook Sign In
        inItFacebook();
        signwithfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            }
        });
    }

    private void inItFacebook()
    {
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    }

    private void fireBase()
    {
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        if(user!= null)
        {
            startActivity(new Intent(getApplicationContext(),StartActivity.class));
            finish();
        }
    }

    private void getData()
    {
        usernamefield = findViewById(R.id.username);
        emailfield = findViewById(R.id.useremail);
        passwordfield = findViewById(R.id.userpassword);
        confirmpasswordfield = findViewById(R.id.userconfirmpassword);
        signwithgoogle = findViewById(R.id.sign_with_google);
        signwithfacebook = findViewById(R.id.sign_with_facebook);
    }

    private void signInWithGoogle()
    {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN); // RC define by any number you want
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
          super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)
                {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e)
            {
                // Google Sign In failed, update UI appropriately
                 Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        // Pass the activity result back to the Facebook SDK
           callbackManager.onActivityResult(requestCode, resultCode, data);
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

    public void alreadyHaveAccount(View view)
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
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Wait..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER) ; // circle style
        progressDialog.setCancelable(false);
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
                            usermodel usermodel = new usermodel(username,email,"https://firebasestorage.googleapis.com/v0/b/chat-1b573.appspot.com/o/images%2Fuser.svg?alt=media&token=3f61b34e-607c-478a-8112-6b67b19b4272");
                            databaseReference.child("Users").child(id).setValue(usermodel);
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),StartActivity.class));
                            finish();

                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void printHash()
    {
        // just method to know the /keyHash
        try
        {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.example.chatad", PackageManager.GET_SIGNATURES);
            for(Signature signature : packageInfo.signatures)
            {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
    }

}
