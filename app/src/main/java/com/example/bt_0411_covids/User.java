package com.example.bt_0411_covids;

import java.io.Serializable;

public class User implements Serializable {
    String cmnd,hoTen,soDT,tinhTrang,hinhAnh,maFB;
    int soLanTiem,soLanTest;

    public User() {
    }

    public User(String maFB, String cmnd, String hoTen, String soDT, String tinhTrang, String hinhAnh, int soLanTiem, int soLanTest) {
        this.maFB = maFB;
        this.cmnd = cmnd;
        this.hoTen = hoTen;
        this.soDT = soDT;
        this.tinhTrang = tinhTrang;
        this.hinhAnh = hinhAnh;
        this.soLanTiem = soLanTiem;
        this.soLanTest = soLanTest;
    }

    public String getMaFB() {
        return maFB;
    }

    public void setMaFB(String maFB) {
        this.maFB = maFB;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getSoLanTiem() {
        return soLanTiem;
    }

    public void setSoLanTiem(int soLanTiem) {
        this.soLanTiem = soLanTiem;
    }

    public int getSoLanTest() {
        return soLanTest;
    }

    public void setSoLanTest(int soLanTest) {
        this.soLanTest = soLanTest;
    }
}
