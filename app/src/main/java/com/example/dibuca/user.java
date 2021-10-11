package com.example.dibuca;

public class user {
    private String ten, email, sdt, congty, congviec, id;

    public user() {

    }

    public user(String ten, String email, String sdt, String congty , String congviec, String id) {
        this.ten = ten;
        this.email = email;
        this.sdt = sdt;
        this.congty = congty;
        this.congviec = congviec;
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getCongty() {
        return congty;
    }

    public void setCongty(String congty) {
        this.congty = congty;
    }

    public String getCongviec() {
        return congviec;
    }

    public void setCongviec(String congviec) {
        this.congviec = congviec;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "user{" +
                "ten='" + ten + '\'' +
                ", email='" + email + '\'' +
                ", sdt='" + sdt + '\'' +
                ", congty='" + congty + '\'' +
                ", congviec='" + congviec + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
