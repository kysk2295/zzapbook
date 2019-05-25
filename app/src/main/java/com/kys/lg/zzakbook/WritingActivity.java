package com.kys.lg.zzakbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kys.lg.zzakbook.databinding.ActivityWritingBinding;
import com.kys.lg.zzakbook.model.ContentDTO;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WritingActivity extends AppCompatActivity {

    private ActivityWritingBinding binding;
    private String photoUrl;
    public String emitter;

    public static final int PICK_PROFILE_FROM_ALBUM = 10;
    public static final int PICK_IMAGE_FROM_ALBUM = 0;
    public static final int PICK_IMAGE_FROM_CAMERA=5;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    public String name;
    private EditText editText;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_writing);
        binding.edittext.setCounterEnabled(true);
        editText = findViewById(R.id.writing_text);
        progressBar=findViewById(R.id.progress);

        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,PICK_IMAGE_FROM_ALBUM);
            }
        });
        binding.addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //글 올리기
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseStorage = FirebaseStorage.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance();
                auth = FirebaseAuth.getInstance();
                progressBar.setVisibility(View.VISIBLE);

                if(photoUrl!=null) {

                    File file = new File(photoUrl);
                    final Uri uri = Uri.fromFile(file);

                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://zzapbook-cd3e7.appspot.com")
                            .child(uri.getLastPathSegment());


                    storageReference.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                    DatabaseReference images = firebaseDatabase.getReference().child("images").push();

                                    Date d = new Date();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    ContentDTO contentDTO = new ContentDTO();
                                    contentDTO.imageUrl = uri.toString();
                                    //유저의 UID
                                    contentDTO.uid = auth.getCurrentUser().getUid();
                                    //게시물의 설명
                                    contentDTO.explain = editText.getText().toString();
                                    //유저의 아이디
                                    contentDTO.userId = auth.getCurrentUser().getEmail();
                                    //게시물 업로드 시간
                                    contentDTO.timestamp = simpleDateFormat.format(d);

                                    images.setValue(contentDTO);

                                    setResult(RESULT_OK);
                                    finish();

                                    progressBar.setVisibility(View.GONE);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(getApplicationContext(), "게시물 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else{

                    Date d = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    ContentDTO contentDTO = new ContentDTO();
                    //유저의 UID
                    contentDTO.uid = auth.getCurrentUser().getUid();
                    //게시물의 설명
                    contentDTO.explain = editText.getText().toString();
                    //유저의 아이디
                    contentDTO.userId = auth.getCurrentUser().getEmail();
                    //게시물 업로드 시간
                    contentDTO.timestamp = simpleDateFormat.format(d);
                    firebaseDatabase.getReference().child("images").push().setValue(contentDTO);
                    setResult(RESULT_OK);
                    finish();

                    progressBar.setVisibility(View.GONE);



                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // 앨범에서 사진 선택시 호출 되는 부분
        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {

            //uri 구하기
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, data.getData(), proj,
                    null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            //이미지 경로
            photoUrl = cursor.getString(column_index);
            binding.imgview.setVisibility(View.VISIBLE);
            //이미지뷰에 이미지 세팅
            binding.imgview.setImageURI(data.getData());
        }
        if(requestCode==PICK_IMAGE_FROM_CAMERA && resultCode==RESULT_OK){
            binding.imgview.setVisibility(View.VISIBLE);
            getPictureForPhoto();
        }
    }

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    photoUrl=currentPhotoPath;
                    startActivityForResult(intent, PICK_IMAGE_FROM_CAMERA);
                }
            }

        }
    }
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;

    }
    private void getPictureForPhoto() {

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        binding.imgview.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }

    //사진의 회전값 구하기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }






}
