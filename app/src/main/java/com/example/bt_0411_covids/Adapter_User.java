package com.example.bt_0411_covids;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_User extends ArrayAdapter {
    Context context;
    int resource;
    ArrayList<User> arrUs= new ArrayList();

    public Adapter_User(@NonNull Context context, int resource, ArrayList<User> arrUs) {
        super(context, resource, arrUs);
        this.context = context;
        this.resource = resource;
        this.arrUs = arrUs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource,null);

        TextView tvHoten = convertView.findViewById(R.id.tvHoTen);
        TextView tvSDT = convertView.findViewById(R.id.tvSDT);
        TextView tvSoLanTiem = convertView.findViewById(R.id.tvSoLanTiem);
        TextView tvTinhTrang = convertView.findViewById(R.id.tvTinhTrang);
        Button btnChitiet = convertView.findViewById(R.id.btnThongTinChiTiet);
        ImageView imAvatar = convertView.findViewById(R.id.imHinhAnh);

        User us = arrUs.get(position);

        tvHoten.setText(us.getHoTen());
        tvSDT.setText(us.getSoDT());
        tvSoLanTiem.setText(us.getSoLanTiem()+"");
        tvTinhTrang.setText(us.getTinhTrang()+"");

        Picasso.get()
                .load(us.getHinhAnh())
                .fit()
                .into(imAvatar);

        //btn Chi tiêt
        btnChitiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SuaUser.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ThongTinUser", us);
                intent.putExtras(bundle);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return arrUs.size();
    }
}
