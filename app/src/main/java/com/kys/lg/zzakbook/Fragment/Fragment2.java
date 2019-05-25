package com.kys.lg.zzakbook.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kys.lg.zzakbook.R;
import com.kys.lg.zzakbook.model.ContentDTO;

import java.util.ArrayList;


public class Fragment2 extends Fragment {

    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_fragment2, container, false);

        RecyclerView recyclerView=view.findViewById(R.id.recylcerview_grid);

        recyclerView.setAdapter(new GridRecyclerviewAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));



        return view;
    }


    private class GridRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ContentDTO> contentDTOS;

        public GridRecyclerviewAdapter(){
            contentDTOS=new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contentDTOS.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        contentDTOS.add(snapshot.getValue(ContentDTO.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(),"서버가 터졌습니다.",Toast.LENGTH_LONG).show();
                }
            });
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            int width=getResources().getDisplayMetrics().widthPixels /3;

            //원래 리사이클러 뷰 항목을 xml에 만들어서 하지만 이번엔 이미지만 사용하므로 이렇게 추가한다.
            ImageView imageView=new ImageView(viewGroup.getContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width,width));
            return new CustomViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            //이미지 다운
            Glide.with(viewHolder.itemView.getContext())
                        .load(contentDTOS.get(i).imageUrl)
                        .apply(new RequestOptions().centerCrop())
                        .into(((CustomViewHolder)viewHolder).imageView);

        }

        @Override
        public int getItemCount() {
            return contentDTOS.size();

        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;

            public CustomViewHolder(ImageView imageView) {
                super(imageView);
                this.imageView=imageView;
            }
        }
    }
}
