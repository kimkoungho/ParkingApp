package com.parking.kani.parking.utility;

import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by Kani on 2017-01-20.
 */

public class TimerTextViewSeriablizable implements Serializable
{
    private TextView parking_time;

    public TimerTextViewSeriablizable(TextView parking_time)
    {
        this.parking_time = parking_time;
    }

    public TextView getTextView()
    {
        return parking_time;
    }
}
