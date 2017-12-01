package com.parking.kani.parking.utility;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class TimeNotiService extends Service
{
    private boolean isStop;
    private int count;
    private int maxCount;
    private int notiCount;
    private boolean noti;

    private SharedPreferences timer, setting;

    public TimeNotiService() {}

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        timer = getSharedPreferences("timer", 0);
        setting = getSharedPreferences("setting", 0);

        long currentTime = Calendar.getInstance().getTimeInMillis();
        maxCount = ((int) (timer.getLong("endTime", 0) - currentTime) / 1000);
        notiCount = ((int) (timer.getLong("notiTime", 0) - timer.getLong("startTime", 0)) / 1000);

        Log.d("MAX_COUNT", maxCount + "");
        Log.d("NOTI_COUNT", notiCount + "");

        // Thread를 이용해 Counter 실행시키기
        Thread counter = new Thread(new Counter());
        counter.start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        isStop = true;
        return super.onUnbind(intent);
    }

    private class Counter implements Runnable
    {
        private Handler handler = new Handler();

        @Override
        public void run()
        {
            for( count = maxCount; count >= 1; count-- )
            {
                noti = setting.getBoolean("noti", false);

                // STOP 버튼을 눌렀다면 종료
                if( isStop )
                    break;

                // 해당 시간이 되면 알림 발생
                if( count == notiCount && noti )
                    new AlarmHATT(getApplicationContext(), count).Alarm();

                /*
                   Thread 안에서는 UI와 관련된 Toast를 쓸 수 없음.
                   따라서 Handler를 통해 이용할 수 있도록 만듬.
                 */

                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d("COUNT", count + "");
                    }
                });

                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getApplicationContext(), "주차 시간 타이머가 끝났어요.", Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            });
        }
    }

    /*
        StopService가 실행될 때 호출
     */

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isStop = true;
    }
}

































