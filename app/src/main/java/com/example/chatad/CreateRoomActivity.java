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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.chatad.models.RoomModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateRoomActivity extends AppCompatActivity
{
    CircleImageView circleImageView;
    EditText roomtitlefield;
    String roomtitle;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri photo;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        circleImageView = findViewById(R.id.roompic);
        roomtitlefield = findViewById(R.id.roomtitle);

        toolbar();
        initviews();
        firebase();
    }

    private void firebase()
    {
        firebaseDatabase =FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void toolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:  //id.home set by defult "homeasupenabled"
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initviews() {
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(CreateRoomActivity.this);
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
            if (resultCode == Activity.RESULT_OK)
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
    }

    public void createroom(View view)
    {
        roomtitle = roomtitlefield.getText().toString();

        if (TextUtils.isEmpty(roomtitle))
        {
            Toast.makeText(getApplicationContext(), "enter room title", Toast.LENGTH_SHORT).show();
            roomtitlefield.requestFocus();
            return;
        }

        if (photo == null)
        {
            Toast.makeText(getApplicationContext(), "select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(CreateRoomActivity.this);
        progressDialog.setMessage("Wait ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        uploadimasge(roomtitle,photo);
    }

    private void uploadimasge(final String roomtitle, Uri photo)
    {
        UploadTask uploadTask;
        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+photo.getLastPathSegment());
        uploadTask =storageReference.putFile(photo);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() // na mo4h m7taga el method continuation ana m7taga on complete
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    Uri image = task.getResult();
                    String pic_url = image.toString();
                    addtoDb(roomtitle,pic_url);
                }else
                    {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
            }
        });
    }

    private void addtoDb(String roomtitle, String pic_url)
    {
        String key = databaseReference.child("Rooms").push().getKey();
        RoomModel roomModel = new RoomModel(roomtitle,pic_url,key); // fe mara kona l5bt wa badlt ben el satreen dool wa adany error
        databaseReference.child("Rooms").child(key).setValue(roomModel);

        progressDialog.dismiss();

        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
