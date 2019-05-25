package com.kys.lg.zzakbook.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kys.lg.zzakbook.Adapter.UserFragment;
import com.kys.lg.zzakbook.R;
import com.kys.lg.zzakbook.WritingActivity;
import com.kys.lg.zzakbook.databinding.RecyclerviewItem2Binding;
import com.kys.lg.zzakbook.model.ContentDTO;

import java.util.ArrayList;

import static com.kys.lg.zzakbook.MainActivity.FRAGMENT_ARG;


public class Fragment1 extends Fragment {

    private RecyclerView recyclerView2;
    private TextView textView;
    private  ProgressBar progressBar;
    public ArrayList<ContentDTO> contents= new ArrayList<>();
    private LinearLayout top_layout;
    private FloatingActionButton button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_fragment1, container, false);
        recyclerView2=v.findViewById(R.id.recyclerview2);
        textView=v.findViewById(R.id.writing_text);
        progressBar = v.findViewById(R.id.progress);
        top_layout=v.findViewById(R.id.top_layout);
        button=v.findViewById(R.id.fab);



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), WritingActivity.class);
                startActivityForResult(i,100);

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView2.smoothScrollToPosition(0);
            }
        });

        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setAdapter(new RecyclerviewAdapter());

        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //최상단
                if(!recyclerView2.canScrollVertically(-1)){
                    top_layout.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                }//최하단
                else if(!recyclerView2.canScrollVertically(1)){

                    button.setVisibility(View.VISIBLE);
                    top_layout.setVisibility(View.GONE);

                }
                else{
                    top_layout.setVisibility(View.GONE);

                }

            }
        });

        return v;

    }


    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder>{

        public ArrayList<ContentDTO> contents;
        public ArrayList<String> contentUidList;


        //데이터 가져오기

        RecyclerviewAdapter() {

            progressBar.setVisibility(View.VISIBLE);

            contents= new ArrayList<>();
            contentUidList= new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contents.clear();
                    contentUidList.clear();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        contents.add(snapshot.getValue(ContentDTO.class));
                        contentUidList.add(snapshot.getKey());

                    }
                    notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(),"서버가 잠시 터졌습니다.",Toast.LENGTH_SHORT).show();

                }
            });
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item2,parent,false);

            return new MyViewHolder(v);

        }



        @Override
        public int getItemCount() {
            return contents.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            final int finalPosition = position;
            final RecyclerviewItem2Binding binding= ((MyViewHolder)holder).getBinding();
            //해당 포지션의 객체를 바인딩 해준다.

            // Profile Image
            //child(): 데이터가 있을 위치를 정해준다.
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("profileImages").child(contents.get(position).uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                @SuppressWarnings("VisibleForTests")
                                String url = dataSnapshot.getValue().toString();

                                //profileimgview에 이미지 올리기
                                Glide.with(holder.itemView.getContext())
                                        .load(url)
                                        .apply(new RequestOptions().circleCrop()).into(binding.imageviewProPic);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            //프로필 사진 눌렀을 때
            binding.imageviewProPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserFragment fragment = new UserFragment();
                    Bundle bundle = new Bundle();
                    //현재 uid
                    bundle.putString("destinationUid", contents.get(finalPosition).uid);
                    bundle.putString("userId", contents.get(finalPosition).userId);
                    bundle.putInt(FRAGMENT_ARG, 5);

                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_layout, fragment)
                            .commit();

                }
            });

            final ContentDTO contentDTO= contents.get(position);
            binding.tvComment.setText(contents.get(position).comments+"comments");
            binding.tvStatus.setText(contents.get(position).explain);
            binding.tvTime.setText(contents.get(position).timestamp);
            binding.tvName.setText(contents.get(position).userId);

            binding.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteEvent(finalPosition);
                }


            });

            if(contents.get(position)
                .favorites.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                binding.heart.setImageResource(R.drawable.heart);


            }else{
                binding.heart.setImageResource(R.drawable.heart_black);

            }

            //프로필 사진을 이미지 뷰에 업로드
            //glide.load(board.getPropic()).into(binding.imageviewProPic);
            //이미지를 첨부 했을 때와 아닐때
            //if(board.getPostpic()==0){
              //  binding.tvImage.setVisibility(View.GONE);
              //   }else{
                //binding.tvImage.setVisibility(View.VISIBLE);
                //glide.load(board.getPropic()).into(binding.tvImage);
            //이미지 올리기
            Glide.with(holder.itemView.getContext()).load(contents.get(position).imageUrl).into(binding.tvImage);

            }


        private void favoriteEvent(int finalPosition) {

            final int position=finalPosition;

            //데이터 꺼내기
            FirebaseDatabase.getInstance().getReference("images").child(contentUidList.get(finalPosition))
                    .runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            ContentDTO contentDTO=mutableData.getValue(ContentDTO.class);

                            String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if(contentDTO == null){
                                return Transaction.success(mutableData);
                            }
                            if(contentDTO.favorites.containsKey(uid)){
                                contentDTO.favoriteCount= contentDTO.favoriteCount-1;
                                contentDTO.favorites.remove(uid);
                            }
                            else{
                                contentDTO.favoriteCount= contentDTO.favoriteCount+1;
                                contentDTO.favorites.put(uid,true);

                            }
                            mutableData.setValue(contentDTO);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public RecyclerviewItem2Binding binding;

            public MyViewHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }

            RecyclerviewItem2Binding getBinding(){
                return binding;
            }
        }


        }


    @Override
    public void onStart() {
        super.onStart();

//        Fragment4 fragment4= new Fragment4();
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        Bundle bundle = new Bundle();
//        bundle.putString("destinationUid", uid);
//        fragment4.setArguments(bundle);
//        Toast.makeText(getActivity(),uid,Toast.LENGTH_LONG).show();




    }
}




