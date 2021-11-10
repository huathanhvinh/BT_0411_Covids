package com.example.bt_0411_covids;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Permissions;
import java.util.Calendar;
import java.util.List;

public class ThemUser extends AppCompatActivity {
    ImageView imAnhUser;
    EditText edHoten, edCMND, edSDT, edSoLanTest, edSoLanTiem;
    Spinner spTinhTrang;
    Button btnThem, btnTrove;

    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
    int REQUEST_CODE = 12345;
    boolean isPermissionGranted = false;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mUser = database.getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_user);
        setControl();
        setEvent();
    }

    //Sự kiện
    private void setEvent() {
        //lấy hình từ điện thoại
        layHinhTuDienThoai();
        //xử lý nút trở về
        btnTrove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //xử lý nút thêm
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edHoten.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edCMND.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "CMND không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edSDT.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edSoLanTest.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Số lần test không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edSoLanTiem.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Số lần tiêm không được để trống", Toast.LENGTH_SHORT).show();
                } else {
                    User us = new User();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");
                    //StorageReference mountainImagesRef = storageRef.child("images/"+"image"+calendar.getTimeInMillis()+".png");
                    // Get the data from an ImageView as bytes
                    imAnhUser.setDrawingCacheEnabled(true);
                    imAnhUser.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imAnhUser.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getApplicationContext(), "Thất Bại", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc
                            // ...
                            //????????????????????????
                            mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    us.setHoTen(edHoten.getText().toString());
                                    us.setCmnd(edCMND.getText().toString());
                                    us.setSoDT(edSDT.getText().toString());
                                    us.setSoLanTest(Integer.parseInt(edSoLanTest.getText().toString()));
                                    us.setSoLanTiem(Integer.parseInt(edSoLanTiem.getText().toString()));
                                    us.setTinhTrang(spTinhTrang.getSelectedItem().toString());
                                    us.setHinhAnh(uri.toString());

                                    String key = mUser.push().getKey();
                                    us.setMaFB(key);
                                    mUser.child(key).setValue(us);
                                    Toast.makeText(getApplicationContext(), "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }
        });
    }

    //Ánh xạ
    private void setControl() {
        imAnhUser = findViewById(R.id.imHinh);
        edHoten = findViewById(R.id.edHoTen);
        edCMND = findViewById(R.id.edCmnd);
        edSDT = findViewById(R.id.edSdt);
        edSoLanTest = findViewById(R.id.edSoLanTest);
        edSoLanTiem = findViewById(R.id.edSoLanTiem);
        spTinhTrang = findViewById(R.id.spTinhTrang);
        btnThem = findViewById(R.id.btnThem);
        btnTrove = findViewById(R.id.btnTrove);
    }

    //lấy hình từ điện thoại
    private void layHinhTuDienThoai() {
        imAnhUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    //cấp quyền cho phép truy cập ảnh
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE);
        }
    }

    //cấp quyền cho phép truy cập ảnh
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //cấp quyền cho phép truy cập ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imAnhUser.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    }

}