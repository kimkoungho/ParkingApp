package com.parking.kani.parking.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parking.kani.parking.R;

import java.util.HashMap;

/**
 * Created by Kani on 2017-01-18.
 */

public class TimerDialog extends BaseDialog
{
    Context context;

    EditText parking_hour, parking_minute;
    EditText noti_hour, noti_minute;
    Button setting_btn, cancel_btn;

    HashMap<String, Object> map = new HashMap<String, Object> ();

    public TimerDialog(final Context context, int themeResId)
    {
        super(context, themeResId);
        this.context = context;
    }

    public void setDialogSetting()
    {
        this.setContentView(R.layout.dialog_timer);

        parking_hour = (EditText) findViewById(R.id.parking_hour);
        parking_minute = (EditText) findViewById(R.id.parking_minute);

        noti_hour = (EditText) findViewById(R.id.noti_hour);
        noti_minute = (EditText) findViewById(R.id.noti_minute);

        setting_btn = (Button) findViewById(R.id.setting_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        setting_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                try
                {
                    int parkingHour = parking_hour.getText().toString().equals("")? 0: Integer.parseInt(parking_hour.getText().toString());
                    int parkingMinute = parking_minute.getText().toString().equals("")? 0: Integer.parseInt(parking_minute.getText().toString());
                    int notiHour = noti_hour.getText().toString().equals("")? 0: Integer.parseInt(noti_hour.getText().toString());
                    int notiMinute = noti_minute.getText().toString().equals("")? 0: Integer.parseInt(noti_minute.getText().toString());

                    int parking_time = parkingHour * 60 + parkingMinute;
                    int noti_time = notiHour * 60 + notiMinute;

                    if (parkingHour > 23 || parkingHour < 0)
                        Toast.makeText(getContext(), "주차시간은 00시간 00분에서 23시간 59까지 설정 할수 있어요.", Toast.LENGTH_SHORT).show();
                    else if (parkingMinute > 59 || parkingMinute < 0)
                        Toast.makeText(getContext(), "주차시간은 00시간 00분에서 23시간 59까지 설정 할수 있어요.", Toast.LENGTH_SHORT).show();
                    else if (notiHour > 23 || notiHour < 0)
                        Toast.makeText(getContext(), "알림시간은 00시간 00분에서 23시간 59까지 설정 할수 있어요.", Toast.LENGTH_SHORT).show();
                    else if (notiMinute > 59 || notiMinute < 0)
                        Toast.makeText(getContext(), "알림시간은 00시간 00분에서 23시간 59까지 설정 할수 있어요.", Toast.LENGTH_SHORT).show();
                    else if (parking_time <= noti_time)
                        Toast.makeText(getContext(), "알림 시간이 주차 시간보다 크거나 같으면 안되요.", Toast.LENGTH_SHORT).show();
                    else
                    {
                        map.put("parking_hour", parkingHour);
                        map.put("parking_minute", parkingMinute);
                        map.put("noti_hour", notiHour);
                        map.put("noti_minute", notiMinute);
                        map.put("header", "setting");
                        updateComponentListener.updateComponent(map);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "값을 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                map.put("header", "cancel");
                updateComponentListener.updateComponent(map);
            }
        });
    }
}
