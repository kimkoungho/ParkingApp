package com.parking.kani.parking.utility;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kimkyeongho on 2016. 12. 26..
 */

public class ParkItem implements Serializable{
    private int park_no;
    private String name;
    private String addr;
    private String type1;
    private String type2;
    private String tel;
    private int space;
    private String cost_type;
    private String weekday_start;
    private String weekday_end;
    private String holiday_start;
    private String holiday_end;
    private String weekend_cost_tp;
    private String holiday_cost_tp;
    private int month_cost;
    private double latitude;
    private double longitude;
    private int base_cost;
    private String base_time;
    private int add_cost;
    private String add_time;
    private int day_cost;
    private int hour_cost;
    private double distance;
    private int rank;
    private int current_park_cnt;
    private String[] image_urls;
    private ArrayList<byte[]> park_imges = null;
    /*

     */
    public ParkItem(int park_no, String name, String addr, String type1, String type2, String tel, int space, String cost_type, String weekday_start, String weekday_end, String holiday_start, String holiday_end, String weekend_cost_tp, String holiday_cost_tp, int month_cost, double latitude, double longitude, int base_cost, String base_time, int add_cost, String add_time, int day_cost, int hour_cost, double distance, int rank, int current_park_cnt, String[] image_urls)
    {
        this.park_no=park_no;
        this.name=name;
        this.addr=addr;
        this.type1=type1;
        this.type2=type2;
        this.tel=tel;
        this.space=space;
        this.cost_type=cost_type;
        this.weekday_start=weekday_start;
        this.weekday_end=weekday_end;
        this.holiday_start=holiday_start;
        this.holiday_end=holiday_end;
        this.weekend_cost_tp=weekend_cost_tp;
        this.holiday_cost_tp=holiday_cost_tp;
        this.month_cost=month_cost;
        this.latitude=latitude;
        this.longitude=longitude;
        this.base_cost=base_cost;
        this.base_time=base_time;
        this.add_cost=add_cost;
        this.add_time=add_time;
        this.day_cost=day_cost;
        this.hour_cost=hour_cost;
        this.distance=distance;
        this.rank=rank;
        this.current_park_cnt=current_park_cnt;
        this.image_urls=image_urls;
    }

    public void setPark_imges(ArrayList<byte[]> park_imges)
    {
        this.park_imges = park_imges;
    }

    public int getPark_no(){return park_no;}
    public String getName(){return name;}
    public String getAddr(){return addr;}
    public String getType1(){return type1;}
    public String getType2(){return type2;}
    public String getTel(){return tel;}
    public int getSpace(){return space;}
    public String getCost_type(){return cost_type;}
    public String getWeekday_start(){return weekday_start;}
    public String getWeekday_end(){return weekday_end;}
    public String getHoliday_start(){return holiday_start;}
    public String getHoliday_end(){return holiday_end;}
    public String getWeekend_cost_tp(){return weekend_cost_tp;}
    public String getHoliday_cost_tp(){return holiday_cost_tp;}
    public int getMonth_cost(){return month_cost;}
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
    public int getBase_cost(){return base_cost;}
    public String getBase_time(){return base_time;}
    public int getAdd_cost(){return add_cost;}
    public String getAdd_time(){return add_time;}
    public int getDay_cost(){return day_cost;}
    public int getHour_cost(){return hour_cost;}
    public double getDistance(){return distance;}
    public int getRank(){return rank;}
    public int getCurrent_park_cnt(){return current_park_cnt;}
    public String[] getImage_urls(){return image_urls;}
    public ArrayList<byte[]> getPark_imges(){return park_imges;}
}
