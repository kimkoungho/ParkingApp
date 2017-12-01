package com.parking.kani.parking.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parking.kani.parking.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kimkyeongho on 2016. 12. 29..
 */

public class ExternalAppAdapter extends BaseAdapter
{

    private Context context = null;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, Object>> app_list;
    private int layout;

    private ImageView app_icon;
    private TextView app_name;


    public ExternalAppAdapter(Context context, int layout, ArrayList<HashMap<String, Object>> data)
    {
        this.context=context;
        this.layout=layout;
        this.app_list=data;
    }

    @Override
    public int getCount() {
        return app_list.size();
    }

    @Override
    public Object getItem(int i) {
        return app_list.get(i);
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

        app_icon=(ImageView)view.findViewById(R.id.external_app_icon);
        app_name=(TextView)view.findViewById(R.id.external_app_name);

        app_icon.setImageDrawable((Drawable)app_list.get(i).get("icon"));
        app_name.setText((String)app_list.get(i).get("name"));

        return view;
    }
}
