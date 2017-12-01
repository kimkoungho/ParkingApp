package com.parking.kani.parking.connection;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by kimkyeongho on 2016. 12. 25..
 */

public class GPSManager extends AsyncTask<String, Integer, String>
{

    public double lati = 0.0;
    public double longi = 0.0;

    public boolean flag=true;

    public LocationManager mLocationManager;
    public VeggsterLocationListener mVeggsterLocationListener;

    private Context context;//해당 context 받아오기
    private ProgressDialog progDailog;

    public AsynctaskFinishListener asyn_listener;

    public GPSManager(Context context)
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
        mVeggsterLocationListener = new VeggsterLocationListener();
        mLocationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);

        //퍼미션 확인
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, mVeggsterLocationListener);

        progDailog = new ProgressDialog(context);
        progDailog.setMessage("위치 잡는중...");
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
        while (flag && this.lati == 0.0)
        {
            try
            {
                Thread.sleep(1000);
                cnt++;
                if(cnt>5)
                    break;

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onCancelled()
    {
        super.onCancelled();
        progDailog.dismiss();
        //퍼미션 확인
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(mVeggsterLocationListener);
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        progDailog.dismiss();
        progDailog=null;

        if(longi==0.0)
        {
            Toast.makeText(context, "GPS 상태를 다시 확인해주세요", Toast.LENGTH_LONG).show();
            //mainWebview.loadUrl(macaddr+"Main.jsp");
            asyn_listener.failTask();
            return;
        }
        //위치 값을 제대로 받음
        Log.d("GPS",lati+","+longi);
        //현재 위치 경위도 기반으로 서버에 요청
        asyn_listener.succeedTask(new String(lati+"-"+longi));
    }

    //location listener 구현
    public class VeggsterLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {

            int lat = (int) location.getLatitude(); // * 1E6);
            int log = (int) location.getLongitude(); // * 1E6);
            int acc = (int) (location.getAccuracy());

            String info = location.getProvider();
            try
            {

                // LocatorService.myLatitude=location.getLatitude();

                // LocatorService.myLongitude=location.getLongitude();

                lati = location.getLatitude();
                longi = location.getLongitude();

            }
            catch (Exception e)
            {
                // progDailog.dismiss();
                // Toast.makeText(getApplicationContext(),"Unable to get Location"
                // , Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.i("OnProviderDisabled", "OnProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.i("onProviderEnabled", "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i("onStatusChanged", "onStatusChanged");

        }
    }
}
