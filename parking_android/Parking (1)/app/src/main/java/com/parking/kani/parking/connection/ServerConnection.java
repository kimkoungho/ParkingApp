package com.parking.kani.parking.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by kimkyeongho on 2016. 12. 22..
 */

public class ServerConnection extends AsyncTask<String, Void, String>
{

//    private final String server_addr="http://192.168.0.2/parking/";
    private final String server_addr = "http://parkingtest.esy.es/parking/";
//    private final String server_addr = "http://100.100.105.112/parking/";

    public AsynctaskFinishListener asyn_listener;

    private Context context = null;//해당 context 받아오기
    private ProgressDialog progDailog;//서버에서 데이터를 받는 중일 때 발생하는 다이얼로그

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
            progDailog.setMessage("서버 요청중...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(true);
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
    protected String doInBackground(String... strings)
    {
        URL url=null;

        BufferedReader reader=null;
        try
        {
            url=new URL(server_addr+strings[0]);

            //Log.d("URL....",strings[0]);

            HttpURLConnection conn=(HttpURLConnection)url.openConnection();

            //conn.setReadTimeout(1000);//읽는 시간 1초
            //conn.setConnectTimeout(1000);//연결 지속 시간 1초
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            Log.d("server_conn","시작");

            StringBuilder stringBuilder=new StringBuilder();
            String str="";
            while((str=reader.readLine())!=null)
            {
                if(str.contains("<br />"))
                {
                    Log.d("Warning", str);
                    continue;
                }
                stringBuilder.append(str+"\n");
            }

            Log.d("데이터",stringBuilder.toString().trim());
            //error code 이면 분기 처리 필요

            return stringBuilder.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(String str)
    {
        super.onPostExecute(str);
        if(context != null)
            progDailog.dismiss();

        if(str!=null && asyn_listener!=null)
        {
            asyn_listener.succeedTask(str);
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
