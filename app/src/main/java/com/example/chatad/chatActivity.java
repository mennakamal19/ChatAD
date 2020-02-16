package com.example.chatad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatad.models.Messagemodel;
import com.example.chatad.models.usermodel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {
    EditText msgbodyfield;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<Messagemodel> mm;
    String msg, name, myimg, roomid;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        roomid = getIntent().getStringExtra("roomID");
        recyclerView = findViewById(R.id.recyclerview);
        msgbodyfield = findViewById(R.id.msg_body);
        layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mm = new ArrayList<>();

        getdata(getuID());
        getchats(roomid);
        firebase();
    }

    private void firebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void getchats(String roomid)
    {
        databaseReference.child("Chats").child(roomid).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mm.clear();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                {
                    Messagemodel messagemodel = dataSnapshot1.getValue(Messagemodel.class);
                    mm.add(messagemodel);
                }
                chatadapter adapter = new chatadapter(mm);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(mm.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getuID()
    {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }

    private void getdata(String getuID)
    {
        databaseReference.child("users").child(getuID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                usermodel usermodel = dataSnapshot.getValue(usermodel.class);
                if (usermodel != null)
                {
                    name = usermodel.getUsername();
                    myimg = usermodel.getPhoto();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void send(View view)
    {
        msg = msgbodyfield.getText().toString();
        if(TextUtils.isEmpty(msg))
        {
            Toast.makeText(getApplicationContext(), "type a message", Toast.LENGTH_SHORT).show();
            msgbodyfield.requestFocus();
            return;
        }
        sendmsg(msg,name,myimg,getuID());
    }

    private void sendmsg(String msg, String name, String myimg, String getuID)
    {
        Messagemodel messagemodel =new  Messagemodel(msg,name,myimg,getuID);
        String msgkey = databaseReference.child("chats").child(roomid).push().getKey();
        if (msgkey != null)
        {
            databaseReference.child("Chats").child(roomid).child(msgkey).setValue(messagemodel);
            msgbodyfield.setText("");
        }
    }

    class chatadapter extends RecyclerView.Adapter<chatadapter.chatvh>
    {
       List<Messagemodel>messagemodels;

        public chatadapter(List<Messagemodel> messagemodels)
        {
            this.messagemodels = messagemodels;
        }

        @NonNull
        @Override
        public chatvh onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        { View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_item,parent,false);
            return new chatvh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull chatvh holder, int position)
        {
            Messagemodel messagemodel = messagemodels.get(position);
            final String id = messagemodel.getId();
            String name = messagemodel.getName();
            String msg = messagemodel.getMsg();
            String image = messagemodel.getImage();
            holder.name.setText(name);
            holder.msg_body.setText(msg);
            Picasso.get().load(image).into(holder.circleImageView);

            if (id.equals(getuID()))
            {
                holder.linearLayout.setGravity(Gravity.END);
            }
        }

        @Override
        public int getItemCount()
        {
            return messagemodels.size();
        }

        class chatvh extends RecyclerView.ViewHolder
        {
            TextView name,msg_body;
            CircleImageView circleImageView;
            LinearLayout linearLayout;

            public chatvh(@NonNull View itemView)
            {
                super(itemView);
                name = itemView.findViewById(R.id.name_txt);
                msg_body = itemView.findViewById(R.id.msg_txt);
                circleImageView = itemView.findViewById(R.id.userimage);
                linearLayout = itemView.findViewById(R.id.lin1);
            }
        }
    }
}
