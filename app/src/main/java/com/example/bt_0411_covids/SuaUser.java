package com.example.bt_0411_covids;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class SuaUser extends AppCompatActivity {
    EditText edHoten,edcmnd,edphone,edsolanTest,edSolanTiem;
    ImageView imHinh;
    Spinner sptinhTrang;
    Button btnSua,btnXoa,btnTrove;

    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
    int REQUEST_CODE = 12345;
    boolean isPermissionGranted = false;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mUser = database.getReference("User");

    User user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_user);
        setControl();
        setEvent();
    }

    private void setEvent() {
        //lấy hình từ điện thoại
        layHinhTuDienThoai();
        //lấy thông tin
        layThongTin();
        //trở về
        btnTrove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //btn xóa
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = (User) getIntent().getSerializableExtra("ThongTinUser");
                AlertDialog.Builder builder = new AlertDialog.Builder(SuaUser.this);
                builder.setTitle("Xóa");
                builder.setMessage("Bạn có muốn xóa không ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUser.child(user.getMaFB()).removeValue();
                        Toast.makeText(getApplicationContext(), "Xóa Thành Công", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        //btnSua
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = (User) getIntent().getSerializableExtra("ThongTinUser");
                if (edHoten.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edcmnd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "CMND không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edphone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edsolanTest.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Số lần test không được để trống", Toast.LENGTH_SHORT).show();
                } else if (edSolanTiem.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Số lần tiêm không được để trống", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");
                    //StorageReference mountainImagesRef = storageRef.child("images/"+"image"+calendar.getTimeInMillis()+".png");
                    // Get the data from an ImageView as bytes
                    imHinh.setDrawingCacheEnabled(true);
                    imHinh.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imHinh.getDrawable()).getBitmap();
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
                                    user.setHoTen(edHoten.getText().toString());
                                    user.setCmnd(edcmnd.getText().toString());
                                    user.setSoDT(edphone.getText().toString());
                                    user.setSoLanTest(Integer.parseInt(edsolanTest.getText().toString()));
                                    user.setSoLanTiem(Integer.parseInt(edSolanTiem.getText().toString()));
                                    user.setTinhTrang(sptinhTrang.getSelectedItem().toString());
                                    user.setHinhAnh(uri.toString());

                                    mUser.child(user.getMaFB()).setValue(user);
                                    Toast.makeText(getApplicationContext(), "Sửa Thành Công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }
        });
    }
    private void layHinhTuDienThoai() {
        imHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

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
                imHinh.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Bạn chưa chọn hình gì", Toast.LENGTH_SHORT).show();
        }
    }

    private void layThongTin() {
        user = (User) getIntent().getSerializableExtra("ThongTinUser");
        edHoten.setText(user.getHoTen());
        edcmnd.setText(user.getCmnd());
        edphone.setText(user.getSoDT());
        edsolanTest.setText(user.getSoLanTest()+"");
        edSolanTiem.setText(user.getSoLanTiem()+"");
        Picasso.get()
                .load(user.getHinhAnh())
                .fit()
                .into(imHinh);
        if (user.getTinhTrang().equals("F0"))
        {
            sptinhTrang.setSelection(0);
        }else if (user.getTinhTrang().equals("F1"))
        {
            sptinhTrang.setSelection(1);
        }else if (user.getTinhTrang().equals("F2"))
        {
            sptinhTrang.setSelection(2);
        }else if (user.getTinhTrang().equals("Chưa Tiêm"))
        {
            sptinhTrang.setSelection(3);
        }else
        {
            sptinhTrang.setSelection(4);
        }
    }

    private void setControl() {
        edHoten = findViewById(R.id.edHotens);
        edcmnd = findViewById(R.id.edcmnds);
        edphone = findViewById(R.id.edsdts);
        edsolanTest = findViewById(R.id.edSoLanTests);
        edSolanTiem = findViewById(R.id.edSoLanTiems);
        imHinh = findViewById(R.id.imHinhs);
        btnSua = findViewById(R.id.btnSua);
        btnXoa = findViewById(R.id.btnXoa);
        btnTrove = findViewById(R.id.btnTrove);
        sptinhTrang = findViewById(R.id.spTinhTrangs);
    }
}