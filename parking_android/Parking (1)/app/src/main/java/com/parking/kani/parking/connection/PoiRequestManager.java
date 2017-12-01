package com.parking.kani.parking.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;

import java.util.ArrayList;

/**
 * Created by kimkyeongho on 2016. 12. 30..
 */

public class PoiRequestManager extends AsyncTask<String, Integer, String>
{
    private Context context;
    private ProgressDialog progDailog;

    public boolean flag=true;
    public AsynctaskFinishListener asyn_listener;

    public ArrayList<TMapPOIItem> poi_list;

    public PoiRequestManager(Context context)
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
        super.onPreExecute();
        progDailog = new ProgressDialog(context);
        progDailog.setMessage("검색중...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(true);
        progDailog.setCanceledOnTouchOutside(false);
        progDailog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                //progDailog.dismiss();
                flag=false;
                //onBackPressed();
            }
        });
        //webFlag=false;
        progDailog.show();
        flag=true;
    }


    @Override
    protected String doInBackground(String... strings)
    {
        int cnt=0;
        try
        {
            TMapData tMapData=new TMapData();
            poi_list=tMapData.findAddressPOI(strings[0]);

            //Log.d("333333",poi_list.get(0).getPOIAddress());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        progDailog.dismiss();

        //if(asyn_listener!=null)
        asyn_listener.succeedTask(poi_list);
    }
    @Override
    protected void onCancelled()
    {
        super.onCancelled();
        progDailog.dismiss();
    }
}
