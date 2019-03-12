package com.example.apptotomcat;

import java.io.Serializable;

public class WeedData implements Serializable {
    private String name = "";
    private String score = "";
    private int id;
    byte[] photo;
    public  WeedData(){
    }
    public WeedData(int id){
        this.id = id;
    }
    public void setName(String uname){
        this.name = uname;
    }
    public String getName(){
        return name;
    }
    public void setScore(String score){
        this.score = score;
    }
    public String getScore(){
        return score;
    }
    public int getId(){return id;}
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    public byte[] getPhoto() {
        return photo;
    }
}
