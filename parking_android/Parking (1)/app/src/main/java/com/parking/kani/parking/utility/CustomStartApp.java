package com.parking.kani.parking.utility;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by kimkyeongho on 2016. 12. 28..
 */

public class CustomStartApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

//        Typekit.getInstance()
//                .addNormal(Typekit.createFromAsset(this, "폰트.ttf"))
//                .addBold(Typekit.createFromAsset(this, "폰트2.ttf"))
//                .addCustom1(Typekit.createFromAsset(this, "폰트3.ttf"));// "fonts/폰트.ttf"

        Typekit.getInstance().addCustom1(Typekit.createFromAsset(this, "HY목각파임B.TTF"));
    }
}