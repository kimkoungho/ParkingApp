package com.parking.kani.parking.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parking.kani.parking.R;

import java.util.ArrayList;

/**
 * Created by kimkyeongho on 2016. 12. 26..
 */

public class ParkListAdapter extends BaseAdapter
{

    private Context context = null;
    private LayoutInflater inflater;
    private ArrayList<ParkItem> list = null;
    private int layout;

    ImageView park_img;
    TextView park_name;
    ImageView park_rank;
    TextView park_distance;
    TextView park_filter;

    public ParkListAdapter(Context context, int layout, ArrayList<ParkItem> data)
    {
        this.context = context;
        this.list = data;
        this.layout = layout;
    }
    //private ViewHolder viewHolder=null;

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int i)
    {
        return list.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if(view == null)
        {
            inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(layout, viewGroup, false);
        }


        park_img=ViewHolder.get(view, R.id.park_img);
        park_name=ViewHolder.get(view, R.id.park_name);
        park_rank=ViewHolder.get(view, R.id.park_rank);
        park_filter = ViewHolder.get(view, R.id.park_filter);
        park_distance=ViewHolder.get(view, R.id.park_distance);

        Log.d(Integer.toString(i),"...."+Integer.toString(list.get(i).getRank()));
        park_rank.setVisibility(View.VISIBLE);
        if( list.get(i).getRank() == 1 )
            park_rank.setBackgroundResource(R.drawable.icon_first);
        else if( list.get(i).getRank() == 2 )
            park_rank.setBackgroundResource(R.drawable.icon_second);
        else if( list.get(i).getRank() == 3 )
            park_rank.setBackgroundResource(R.drawable.icon_third);
        else
            park_rank.setVisibility(View.INVISIBLE);

        park_filter.setText("거리");

        park_name.setText(list.get(i).getName());
        double dist=list.get(i).getDistance();
        String distance=String.format("%d",(int)Math.round(dist*1000))+" m";
        if(dist>1)
            distance=String.format("%.2f",dist)+" km";
        park_distance.setText(distance);

        Bitmap imageList[] = null;

        ArrayList<byte[]> img_list = list.get(i).getPark_imges();
        if(img_list!=null)
        {
            imageList = new Bitmap[img_list.size()];
            for(int j=0; j<img_list.size(); j++)
            {
                imageList[j] = BitmapFactory.decodeByteArray(img_list.get(j),0,img_list.get(j).length);
            }

            if(imageList.length>0)
                park_img.setImageBitmap(imageList[0]);
        }
        else
        {
            park_img.setImageResource(R.drawable.parking_no_image);
        }

        return view;
    }
}
