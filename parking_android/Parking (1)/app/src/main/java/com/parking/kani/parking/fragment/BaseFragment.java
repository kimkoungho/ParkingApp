package com.parking.kani.parking.fragment;

/**
 * Created by Kani on 2016-12-26.
 */
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ImageDownTask;
import com.parking.kani.parking.utility.ParkItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.parking.kani.parking.R.id.image;
import static com.parking.kani.parking.R.id.park_img;

public class BaseFragment extends Fragment
{
    //영욱이
//    public final static String t_map_key="fe77fde6-c8bd-3c68-8f3b-7c6439f5471b";
    //api key
    public final static String t_map_key="796ea8a7-b571-3249-961b-d3d5e57bcf93";

    /*
    올레 아이나비: kt.navi
    카카오내비: com.locnall.KimGiSa
    지도: com.google.android.apps.maps
     T map: com.skt.tmap.ku
    3D지도 아틀란: kr.mappers.AtlanSmart
    네이버 지도: com.nhn.android.nmap
    */
    protected String ext_pakage_list[]=new String[]{
        "kt.navi", "com.locnall.KimGiSa", "com.google.android.apps.maps", "com.skt.tmap.ku", "kr.mappers.AtlanSmart", "com.nhn.android.nmap"
    };
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

    }

    public ArrayList<ParkItem> parseParkList(String json_str, boolean dist_flag, boolean current_park_flag)
    {

        ArrayList<ParkItem> list=new ArrayList<>();
        try
        {
            JSONArray json_park=new JSONArray(json_str);
            for(int i=0; i<json_park.length(); ++i)
            {
                JSONObject jsonObject=(JSONObject)json_park.get(i);

                int park_no=Integer.parseInt((String)jsonObject.get("park_no"));
                String name=(String)jsonObject.get("name");
                String addr=(String)jsonObject.get("addr");
                String type1=(String)jsonObject.get("type1");
                String type2=(String)jsonObject.get("type2");
                String tel=(String)jsonObject.get("tel");
                if(tel.equals("")) tel="연락처 없음";
                int space=Integer.parseInt((String)jsonObject.get("space"));
                String cost_type=(String)jsonObject.get("cost_type");
                String weekday_start=(String)jsonObject.get("weekday_start");
                weekday_start=convertTimeFormat(weekday_start);
                String weekday_end=(String)jsonObject.get("weekday_end");
                weekday_end=convertTimeFormat(weekday_end);
                String holiday_start=(String)jsonObject.get("holiday_start");
                holiday_start=convertTimeFormat(holiday_start);
                String holiday_end=(String)jsonObject.get("holiday_end");
                holiday_end=convertTimeFormat(holiday_end);
                String weekend_cost_tp=(String)jsonObject.get("weekend_cost_tp");
                String holiday_cost_tp=(String)jsonObject.get("holiday_cost_tp");
                int month_cost=Integer.parseInt((String)jsonObject.get("month_cost"));
                double latitude=Double.parseDouble((String)jsonObject.get("latitude"));
                double longitude=Double.parseDouble((String)jsonObject.get("longitude"));
                int base_cost=Integer.parseInt((String)jsonObject.get("base_cost"));
                String base_time=(String)jsonObject.get("base_time");
                int add_cost=Integer.parseInt((String)jsonObject.get("add_cost"));
                String add_time=(String)jsonObject.get("add_time");
                int day_cost=Integer.parseInt((String)jsonObject.get("day_cost"));
                int hour_cost=Integer.parseInt((String)jsonObject.get("hour_cost"));
                double distance = -1.0;
                if(dist_flag)
                    distance=Double.parseDouble((String)jsonObject.get("distance"));
                int current_park = -1;
                if(current_park_flag)
                    current_park = (int)jsonObject.get("cur_parking");

                int rank=0;
                switch (i)
                {
                    case 0:rank=1;break;
                    case 1:rank=2;break;
                    case 2:rank=3;break;
                    default:rank=0;break;
                }
                JSONArray img_list = (JSONArray)jsonObject.get("url");
                String []park_imges = new String[img_list.length()];
                for(int j=0; j<img_list.length(); ++j)
                {
                    park_imges[j] = (String)img_list.get(j);
                }

                list.add(new ParkItem(park_no,name,addr,type1,type2,tel,space,cost_type,weekday_start,weekday_end,holiday_start,holiday_end,
                        weekend_cost_tp,holiday_cost_tp,month_cost,latitude,longitude,base_cost,base_time,add_cost,add_time,day_cost,hour_cost,distance,rank,current_park, park_imges));

                //Log.d(Integer.toString(i),"...."+Integer.toString(rank));
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        return list;
    }

    //db에 있는 시간은 900 => 09:00 내가 원하는 형식이 아님 convert 수행
    protected String convertTimeFormat(String time){

        int hour;
        int min;
        if(time.equals("0"))
            return "00:00";

        if(time.length()>=4)
        {
            hour=Integer.parseInt(time.substring(0,2));
            min=Integer.parseInt(time.substring(2));
        }
        else
        {
            hour=Integer.parseInt(time.substring(0,1));
            min=Integer.parseInt(time.substring(1));
        }

        return String.format("%02d",hour)+":"+String.format("%02d",min);
    }
}
