package com.example.chatad.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatad.R;
import com.example.chatad.chatActivity;
import com.example.chatad.models.RoomModel;
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

public class chatfragment extends Fragment
{
    View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private List<RoomModel> roomModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.chat_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        roomModelList = new ArrayList<>();

        firebase();
        getrooms();
    }

    private void firebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void getrooms()
    {
        databaseReference.child("MyRooms").child(getuid()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                roomModelList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    RoomModel roomModel = dataSnapshot1.getValue(RoomModel.class); // empty constructor
                    roomModelList.add(roomModel);
                }
                roomadapter adapter = new roomadapter(roomModelList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class roomadapter extends RecyclerView.Adapter<chatfragment.roomadapter.roomvh>
    {
        List<RoomModel>roomModels;

        public roomadapter(List<RoomModel> roomModels)
        {
            this.roomModels = roomModels;
        }

        @NonNull
        @Override
        public roomadapter.roomvh onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.room_item,parent,false);
            return new roomadapter.roomvh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull chatfragment.roomadapter.roomvh holder, int position)
        {
            final RoomModel roomModel =roomModels.get(position);
            String title =roomModel.getTitle();
            String image = roomModel.getImage();
            final String key = roomModel.getId();
            holder.roomtitle.setText(title);
            holder.join.setText("exit");
            Picasso.get().load(image).into(holder.circleImageView);
            holder.join.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    databaseReference.child("MyRooms").child(getuid()).child(key).removeValue();
                    Toast.makeText(getContext(), "Exit..", Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getContext(), chatActivity.class);
                    intent.putExtra("roomID",key);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return roomModels.size();
        }

        class roomvh extends RecyclerView.ViewHolder
        {
            TextView roomtitle;
            CircleImageView circleImageView;
            Button join;
            public roomvh(@NonNull View itemView)
            {
                super(itemView);
                roomtitle = itemView.findViewById(R.id.room_title);
                circleImageView = itemView.findViewById(R.id.room_image);
                join = itemView.findViewById(R.id.join_btn);
            }
        }
    }
    private  String getuid()
    {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }
}