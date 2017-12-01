package com.parking.kani.parking.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kani on 2017-01-03.
 */

public class FavoriteListAdapter extends BaseAdapter
{
    private Context context = null;
    private LayoutInflater inflater;
    private ArrayList<ParkItem> list = null;
    private int layout;

    ImageView favorite_img;
    TextView favorite_name;
    TextView favorite_address;
    ImageButton favorite_remove_btn;

    LinearLayout no_listView_layout, listView_layout;

    public FavoriteListAdapter(Context context, int layout, ArrayList<ParkItem> data)
    {
        this.context = context;
        this.list = data;
        this.layout = layout;
    }
    //private ViewHolder viewHolder=null;

    public FavoriteListAdapter(Context context, int layout, ArrayList<ParkItem> data, LinearLayout layout1, LinearLayout layout2)
    {
        this(context, layout, data);

        no_listView_layout = layout1;
        listView_layout = layout2;
    }

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
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        if(view == null)
        {
            inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, viewGroup, false);
        }

        favorite_img = ViewHolder.get(view,R.id.favorite_img);
        favorite_name = ViewHolder.get(view,R.id.favorite_name);
        favorite_address = ViewHolder.get(view,R.id.favorite_address);
        favorite_remove_btn = ViewHolder.get(view,R.id.favorite_remove_btn);
        favorite_remove_btn.setTag(i);

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
                favorite_img.setImageBitmap(imageList[0]);
        }


        favorite_name.setText(list.get(i).getName());
        favorite_address.setText(list.get(i).getAddr());

        favorite_remove_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Log.d("삭제",list.size()+"..");
                //list.remove(v.getTag());
                DBHelper helper = new DBHelper(context,"favorite.db",null, 1);
                helper.delete(list.get((int)v.getTag()).getPark_no());

                int j=0;
                Iterator it = list.iterator();
                while(it.hasNext())
                {
                    it.next();
                    if(j == (int)v.getTag())
                    {
                        it.remove();
                        break;
                    }
                    j++;
                }

                //Log.d("삭제",list.size()+"..");
                notifyDataSetChanged();


                Toast.makeText(context, v.getTag()+" 해당 항목 삭제", Toast.LENGTH_SHORT).show();

                // 항목을 모두 삭제했을 경우
                if( getCount() == 0 )
                {
                    no_listView_layout.setVisibility(View.VISIBLE);
                    listView_layout.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
}