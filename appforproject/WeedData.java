package com.example.appforproject;

import java.io.Serializable;
import java.util.ArrayList;

public class WeedData implements Serializable {
    private String name = "";
    private String score = "";
    private int id;
    byte[] photo;
    private int maxEntery;
    ArrayList<String> names;
    ArrayList<Float> count;
    ArrayList<Integer> idNumbers;
    ArrayList<Double> lon;
    ArrayList<Double> lat;
    ArrayList<String> geoName;
    private int max;
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
    public int getMaxEntery() {
        return maxEntery;
    }
    public void setMaxEntery(int maxEntery) {
        this.maxEntery = maxEntery;
    }
    public ArrayList<String> getNames() {
        return names;
    }
    public void setNames(ArrayList<String> names) {
        this.names = names;
    }
    public ArrayList<Float> getCount() {
        return count;
    }
    public void setCount(ArrayList<Float> count) {
        this.count = count;
    }
    public int getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public void setId(int id) { this.id = id; }
    public ArrayList<Integer> getIdNumbers() { return idNumbers; }
    public void setIdNumbers(ArrayList<Integer> idNumbers) { this.idNumbers = idNumbers;}
    public ArrayList<Double> getLon() { return lon; }
    public void setLon(ArrayList<Double> lon) { this.lon = lon; }
    public ArrayList<Double> getLat() { return lat; }
    public void setLat(ArrayList<Double> lat) { this.lat = lat; }
    public ArrayList<String> getGeoName() { return geoName; }
    public void setGeoName(ArrayList<String> geoName) { this.geoName = geoName; }
}
