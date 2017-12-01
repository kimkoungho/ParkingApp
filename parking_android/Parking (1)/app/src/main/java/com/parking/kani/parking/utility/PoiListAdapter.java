package com.parking.kani.parking.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parking.kani.parking.R;
import com.skp.Tmap.TMapPOIItem;

import java.util.ArrayList;

/**
 * Created by kimkyeongho on 2016. 12. 30..
 */

public class PoiListAdapter extends BaseAdapter
{
    private Context context = null;
    private LayoutInflater inflater;
    private ArrayList<TMapPOIItem> list = null;
    private int layout;

    ImageView poi_img;
    TextView poi_name;
    TextView poi_addr;

    public PoiListAdapter(Context context, int layout, ArrayList<TMapPOIItem> data)
    {
        this.context = context;
        this.list = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(layout, viewGroup, false);
        }

        poi_img=ViewHolder.get(view, R.id.poi_img);
        poi_name=ViewHolder.get(view, R.id.poi_name);
        poi_addr=ViewHolder.get(view, R.id.poi_addr);

        poi_name.setText(list.get(i).getPOIName());
        poi_addr.setText(list.get(i).getPOIAddress().replace("null",""));


        return view;
    }
}
