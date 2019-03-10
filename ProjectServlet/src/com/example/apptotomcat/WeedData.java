package com.example.apptotomcat;

import java.io.Serializable;

public class WeedData implements Serializable {
    private String uname = "";
    private String password = "";
    private int id;
    public  WeedData(){
    }
    public WeedData(int id){
        this.id = id;
    }
    public void setName(String uname){
        this.uname = uname;
    }
    public String getName(){
        return uname;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    public int getId(){return id;}
}
