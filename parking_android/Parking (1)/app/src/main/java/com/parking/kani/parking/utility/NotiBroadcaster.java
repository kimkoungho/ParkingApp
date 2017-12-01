package com.parking.kani.parking.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parking.kani.parking.R;
import com.parking.kani.parking.activity.MainActivity;

/**
 * Created by Kani on 2017-01-19.
 */

public class NotiBroadcaster extends BroadcastReceiver
{
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent)
    {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);

        int restCount = intent.getExtras().getInt("restCount");
        String restTime = (restCount > 3600)? String.format("%d시간 %d분", restCount / 3600, (restCount % 3600) / 60): (restCount / 60 + "분");

        Log.d("REST_COUNT", restTime);

        builder.setSmallIcon(R.drawable.icon_noti).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("어디에 주차").setContentText("주차 시간이 " + restTime + " 남았어요.")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());
    }
}