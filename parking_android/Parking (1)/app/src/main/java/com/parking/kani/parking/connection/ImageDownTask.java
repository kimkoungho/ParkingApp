package com.parking.kani.parking.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.parking.kani.parking.utility.ParkItem;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kimkyeongho on 2017. 1. 10..
 */

public class ImageDownTask extends AsyncTask<String, Void, Object>
{
//    private final String server_addr="http://192.168.0.2/parking/";
    private final String server_addr = "http://parkingtest.esy.es/parking/";
//    private final String server_addr = "http://100.100.105.112/parking/";

    public AsynctaskFinishListener asyn_listener;

    private Context context = null;//해당 context 받아오기
    private ProgressDialog progDailog;//서버에서 데이터를 받는 중일 때 발생하는 다이얼로그

    public ParkItem item = null;

    public void setDialog(Context context)
    {
        this.context=context;
    }

    public void setAsynctaskFinishListener(AsynctaskFinishListener listener)
    {
        asyn_listener=listener;
    }
    @Override
    protected void onPreExecute()
    {
        // show dialog
        super.onPreExecute();
        if(context!=null)
        {
            progDailog = new ProgressDialog(context);
            progDailog.setMessage("이미지 받아오는 중...");
            progDailog.setIndeterminate(true);
            //progDailog.setCancelable(true);
            progDailog.setCanceledOnTouchOutside(false);
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    //progDailog.dismiss();
                    //flag=false;
                    //onBackPressed();
                }
            });
            //webFlag=false;
            progDailog.show();
        }
    }

    @Override
    protected Object doInBackground(String... strings)
    {
        URL url=null;

        try
        {
            Bitmap[] bitmaps = new Bitmap[strings.length];
            ArrayList<byte[]> images = new ArrayList<>();

            for(int i=0; i<strings.length; i++)
            {
                url=new URL(server_addr+strings[i]);

                //Log.d("image down", "시작");
                InputStream input = url.openStream();
                bitmaps[i] = BitmapFactory.decodeStream(input);
                ByteArrayOutputStream b_out = new ByteArrayOutputStream();
                bitmaps[i].compress(Bitmap.CompressFormat.JPEG,100,b_out);
                byte[] image = b_out.toByteArray();
                images.add(image);
            }

            item.setPark_imges(images);


            return item;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(Object item)
    {
        super.onPostExecute(item);
        if(context != null)
            progDailog.dismiss();

        if(item!=null && asyn_listener!=null)
        {
            asyn_listener.succeedTask(item);
        }
        else
        {
            asyn_listener.failTask();
            Log.d("Connection","반환 값이 없음");
        }
    }


    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled()
    {
        super.onCancelled();
        if(context != null)
            progDailog.dismiss();
    }
}
