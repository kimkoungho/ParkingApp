package com.parking.kani.parking.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Kani on 2017-01-19.
 */

public class AlarmHATT
{
    private Context context;
    private int restCount;

    public AlarmHATT(Context context, int restCount)
    {
        this.context = context;
        this.restCount = restCount;
    }

    public void Alarm()
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotiBroadcaster.class);

        intent.putExtra("restCount", restCount);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();

        //알람 예약
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }
}
