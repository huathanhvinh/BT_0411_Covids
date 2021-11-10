package com.example.bt_0411_covids;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mUser = database.getReference("User");

    RadioButton rdF0, rdF1, rdF2, rdChuaTiem, rdKhac;
    Button btnThoat, btnThem;
    ListView lvDanhSach;
    ImageView imrs;

    ArrayList<User> arrUser = new ArrayList<>();
    Adapter_User adapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();
//        <item>F0</item>
//        <item>F1</item>
//        <item>F2</item>
//        <item>Chưa Tiêm</item>
//        <item>Khác</item>
//        String key = mUser.push().getKey();
//        User us = new User(key, "237871291", "Nguyễn Văn A", "0933000100", "F0", "https://firebasestorage.googleapis.com/v0/b/covids-aa7fa.appspot.com/o/image1636546321599.png?alt=media&token=13e589f1-2089-4d35-8e9a-04444a791449", 1, 2);
//        mUser.child(key).setValue(us);
//
//        String key1 = mUser.push().getKey();
//        User us1 = new User(key1, "871871291", "Nguyễn Văn B", "0933000101", "F0", "https://firebasestorage.googleapis.com/v0/b/covids-aa7fa.appspot.com/o/image1636546321599.png?alt=media&token=13e589f1-2089-4d35-8e9a-04444a791449", 1, 2);
//        mUser.child(key1).setValue(us1);
//
//        String key2 = mUser.push().getKey();
//        User us2 = new User(key2, "237871000", "Nguyễn Văn C", "0933000102", "F1", "https://firebasestorage.googleapis.com/v0/b/covids-aa7fa.appspot.com/o/image1636546321599.png?alt=media&token=13e589f1-2089-4d35-8e9a-04444a791449", 1, 2);
//        mUser.child(key2).setValue(us2);
//
//        String key3 = mUser.push().getKey();
//        User us3 = new User(key3, "126871291", "Nguyễn Văn D", "0933000103", "F2", "https://firebasestorage.googleapis.com/v0/b/covids-aa7fa.appspot.com/o/image1636546321599.png?alt=media&token=13e589f1-2089-4d35-8e9a-04444a791449", 1, 2);
//        mUser.child(key3).setValue(us3);
//
//        String key4 = mUser.push().getKey();
//        User us4 = new User(key4, "762871291", "Nguyễn Văn E", "0933000104", "Chưa Tiêm", "https://firebasestorage.googleapis.com/v0/b/covids-aa7fa.appspot.com/o/image1636546321599.png?alt=media&token=13e589f1-2089-4d35-8e9a-04444a791449", 0, 2);
//        mUser.child(key4).setValue(us4);
//
//        String key5 = mUser.push().getKey();
//        User us5 = new User(key5,"102871291", "Nguyễn Văn F", "0933000105", "Khác", "https://firebasestorage.googleapis.com/v0/b/covids-aa7fa.appspot.com/o/image1636546321599.png?alt=media&token=13e589f1-2089-4d35-8e9a-04444a791449", 1, 2);
//        mUser.child(key5).setValue(us5);
//        startActivity(new Intent(getApplicationContext(),ThemUser.class));

    }

    private void setControl() {
        rdF0 = findViewById(R.id.rdF0);
        rdF1 = findViewById(R.id.rdF1);
        rdF2 = findViewById(R.id.rdF2);
        rdChuaTiem = findViewById(R.id.rdChuaTiem);
        rdKhac = findViewById(R.id.rdKhac);
        btnThem = findViewById(R.id.btnThemMoi);
        btnThoat = findViewById(R.id.btnThoat);
        lvDanhSach = findViewById(R.id.lvDanhSach);
        imrs = findViewById(R.id.imRefresh);

        adapterUser = new Adapter_User(getApplicationContext(), R.layout.custom_user, arrUser);
        lvDanhSach.setAdapter(adapterUser);
    }

    private void setEvent() {
        //refresh du lieu
        imrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrUser.clear();
                layThongTinBenhNhan();
            }
        });
        //Load dữ liệu từ firebase về máy
        layThongTinBenhNhan();
        //xử lý các radio
        layDuLieuSauKhiClickRadioButton();
        //xử lý nút thoát
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Thoát");
                builder.setMessage("Bạn có muốn thoát không ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        //Xử lý nút thêm
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ThemUser.class));
            }
        });
    }

    //Load dữ liệu từ firebase về máy
    private void layThongTinBenhNhan() {
        mUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User us = snapshot.getValue(User.class);
                arrUser.add(us);
                adapterUser.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //xử lý các radio
    private void layDuLieuSauKhiClickRadioButton() {
        rdKhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdF0.setChecked(false);
                rdF1.setChecked(false);
                rdF2.setChecked(false);
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrUser.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("tinhTrang").getValue().toString().equals("Khác")) {
                                User us = ds.getValue(User.class);
                                arrUser.add(us);
                                adapterUser.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        rdChuaTiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdF0.setChecked(false);
                rdF1.setChecked(false);
                rdF2.setChecked(false);
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrUser.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("tinhTrang").getValue().toString().equals("Chưa Tiêm")) {
                                User us = ds.getValue(User.class);
                                arrUser.add(us);
                                adapterUser.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        rdF0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdKhac.setChecked(false);
                rdChuaTiem.setChecked(false);
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrUser.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("tinhTrang").getValue().toString().equals("F0")) {
                                User us = ds.getValue(User.class);
                                arrUser.add(us);
                                adapterUser.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        rdF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdKhac.setChecked(false);
                rdChuaTiem.setChecked(false);
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrUser.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("tinhTrang").getValue().toString().equals("F1")) {
                                User us = ds.getValue(User.class);
                                arrUser.add(us);
                                adapterUser.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        rdF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdKhac.setChecked(false);
                rdChuaTiem.setChecked(false);
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrUser.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.child("tinhTrang").getValue().toString().equals("F2")) {
                                User us = ds.getValue(User.class);
                                arrUser.add(us);
                                adapterUser.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}