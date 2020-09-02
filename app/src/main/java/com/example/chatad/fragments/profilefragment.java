package com.example.chatad.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.chatad.MainActivity;
import com.example.chatad.R;
import com.example.chatad.StartActivity;
import com.example.chatad.models.usermodel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class profilefragment extends Fragment
{
    View view;
    TextView logout,change_password_field,user_name_field,user_email_field;
    Uri photo;
    CircleImageView circleImageView;
    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        views();
        inItFirebase();
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(view.getContext(), MainActivity.class));
                if(getActivity() != null )
                {
                    getActivity().finish();
                }
            }
        });

    }

    private void views()
    {
        toolbar = view.findViewById(R.id.toolbar);
        logout = view.findViewById(R.id.log_out);
        change_password_field = view.findViewById(R.id.change_password);
        user_name_field = view.findViewById(R.id.user_name);
        user_email_field = view.findViewById(R.id.user_email);
        circleImageView = view.findViewById(R.id.user_image);
    }

    private void inItFirebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

//    private void initImageView()
//    {
//        circleImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            { // crop image
//                CropImage.activity() //object from activity
//                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
//                        .setAspectRatio(1,1) // 1 ,1 for square shape
//                        .start(new profilefragment());
//            }
//        });
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//        {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == Activity.RESULT_OK) //i have be sure result is okay because result could be return empty
//            {
//                if(result!=null)
//                {
//                    photo = result.getUri();
//                    Picasso.get()
//                            .load(photo)
//                            .placeholder(R.drawable.ic_userfor)
//                            .error(R.drawable.ic_userfor)
//                            .into(circleImageView);
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
//            {
//                Exception error = result.getError();
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void uploadphoto(final String username, final String email, Uri photo, final String id)
    {// method to save image in storage
        UploadTask uploadTask;
        storageReference.child("images/"+photo.getLastPathSegment());//get image path  , the "/" to return into file
        uploadTask =storageReference.putFile(photo); // put file:used with images and pdf
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
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

                //savatodb(username,email,pic_url,id);
            }
        });
    }
//        private void savatodb(String username, String email, String pic_url,String id)
//    {
//        usermodel usermodel = new usermodel(username,email,pic_url);
//        databaseReference.child("Users").child(id).setValue(usermodel);
//        startActivity(new Intent(getContext(),StartActivity.class));
//        getActivity().finish();
//    }
}


