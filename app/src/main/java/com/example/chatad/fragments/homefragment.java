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

import com.example.chatad.CreateRoomActivity;
import com.example.chatad.R;
import com.example.chatad.models.RoomModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class homefragment extends Fragment
{
    View view;
    String id;
    private FloatingActionButton create_room;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private List<RoomModel>roomModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.home_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerviewhome);
        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        roomModelList = new ArrayList<>();

        getuid();
        firebase();
        getrooms();

        create_room = view.findViewById(R.id.floating);
        create_room.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getContext(), CreateRoomActivity.class));
            }
        });

    }
    private  String getuid()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){
            id =user.getUid();
        }
        return id;
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
        databaseReference.child("Rooms").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                roomModelList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    RoomModel roomModel = dataSnapshot1.getValue(RoomModel.class); // the benefit of the empty constructor in models
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

    class roomadapter extends RecyclerView.Adapter<roomadapter.roomvh>
    {
        List<RoomModel>roomModels;

        public roomadapter(List<RoomModel> roomModels)
        {
            this.roomModels = roomModels;
        }

        @NonNull
        @Override
        public roomvh onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.room_item,parent,false);
            return new roomvh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull roomvh holder, int position)
        {
             final RoomModel roomModel =roomModels.get(position);
             String title =roomModel.getTitle();
             String image = roomModel.getImage();
             final String key = roomModel.getId();
             holder.roomtitle.setText(title);
            Picasso.get()
                    .load(image)
                    .into(holder.circleImageView);
            holder.check(key,roomModel);
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
            void check (final String key , final RoomModel roomModel)
            {
                databaseReference.child("MyRooms").child(getuid()).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild(key))
                        {
                            join.setText("Exit");
                            join.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    databaseReference.child("MyRooms").child(getuid()).child(key).removeValue();
                                    join.setText("join");
                                }
                            });
                        }else
                            {
                                join.setText("Join");
                                join.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        databaseReference.child("MyRooms").child(getuid()).child(key).setValue(roomModel);
                                        Toast.makeText(getContext(), "Joined.." , Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

}
