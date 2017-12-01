package com.parking.kani.parking.activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parking.kani.parking.R;

public class SettingActivity extends AppCompatActivity
{
    private Button noti_btn, reset_btn , succeed_btn;
    private TextView noti_txt;
    private TextView setting_txt[]=null;

    public static final String[][] setting_list=new String[][]{
            {"거리", "주차시간", "금액", "주차가능대수", "평점"},
            {"100m", "200m", "300m", "500m", "700m", "1000m", "1000m+"},
            {"상관없음", "3000원/1시간", "5000원/1시간", "10000원/1시간"},
            {"상관없음", "시간주차", "일일주차", "월정기주차"},
//            {"상관없음", "승용(소)", "승용(대)", "승합", "화물(중)", "화물(대)"}
            {"상관없음", "오토바이", "승용(소)", "승용(대)", "승합", "SUV", "화물(중)", "화물(대)", "버스", "택시"}
    };

    //setting 정보
    private boolean noti;
    private String []setting_key=new String[]{
            "search","distance","cost","park_item","car"
    };
    private int []setting_info=new int[5];//search,distance,cost,park_item,car


    private SharedPreferences setting=null;
    private SharedPreferences.Editor editor=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        noti_btn=(Button)findViewById(R.id.noti_btn);
        reset_btn=(Button)findViewById(R.id.reset_btn);
        succeed_btn=(Button)findViewById(R.id.succeed_btn);
        noti_txt=(TextView)findViewById(R.id.noti_txt);
        setting_txt=new TextView[]{
                (TextView)findViewById(R.id.search_type),
                (TextView)findViewById(R.id.distance_state),
                (TextView)findViewById(R.id.cost_state),
                (TextView)findViewById(R.id.park_item_state),
                (TextView)findViewById(R.id.car_type)
        };

        //값을 초기화
        //설정 정보 가져오기
        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();

        //noti-search_type-distance_state-cost_state-park_item_state-car_type
        noti=setting.getBoolean("noti",false);
        for(int i=0; i<setting_info.length; i++)
        {
            setting_info[i] = setting.getInt(setting_key[i], 0);
            setting_txt[i].setText(setting_list[i][setting_info[i]]);
        }

        //값 초기화
        if(noti)
        {
            Drawable background=getDrawable(R.drawable.icon_push_on);
            noti_btn.setBackground(background);
            noti_txt.setText("켜짐");
        }
        else
        {
            Drawable background=getDrawable(R.drawable.icon_push_off);
            noti_btn.setBackground(background);
            noti_txt.setText("꺼짐");
        }



        noti_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(noti_txt.getText().toString().equals("켜짐"))
                {
                    Drawable background=getDrawable(R.drawable.icon_push_off);
                    noti_btn.setBackground(background);
                    noti_txt.setText("꺼짐");
                }
                else
                {
                    Drawable background=getDrawable(R.drawable.icon_push_on);
                    noti_btn.setBackground(background);
                    noti_txt.setText("켜짐");
                }
                noti = !noti;
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Drawable background=getDrawable(R.drawable.icon_push_off);
                noti_btn.setBackground(background);
                noti_txt.setText("꺼짐");

                for(int i=0; i<setting_info.length; i++)
                {
                    setting_info[i] = 0;
                    setting_txt[i].setText(setting_list[i][setting_info[i]]);
                }
            }
        });

        succeed_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editor.putBoolean("noti",noti);
                for(int i=0; i<setting_info.length; i++)
                    editor.putInt(setting_key[i],setting_info[i]);

                editor.commit();
                finish();
            }
        });

    }

    public void preOnClick(View view)
    {
        int type=-1;
        switch (view.getId())
        {
            case R.id.search_pre_btn:type=0;break;
            case R.id.distance_pre_btn:type=1;break;
            case R.id.cost_pre_btn:type=2;break;
            case R.id.park_item_pre_btn:type=3;break;
            case R.id.car_pre_btn:type=4;break;
        }

        setting_info[type] = setting_info[type] - 1 < 0 ? setting_list[type].length - 1 : setting_info[type] - 1;
        setting_txt[type].setText(setting_list[type][setting_info[type]]);
    }

    public void nextOnClick(View view)
    {
        int type=-1;
        switch (view.getId())
        {
            case R.id.search_next_btn:type=0;break;
            case R.id.distance_next_btn:type=1;break;
            case R.id.cost_next_btn:type=2;break;
            case R.id.park_item_next_btn:type=3;break;
            case R.id.car_next_btn:type=4;break;
        }

        setting_info[type] = setting_info[type] + 1 == setting_list[type].length ? 0 : setting_info[type] + 1;
        setting_txt[type].setText(setting_list[type][setting_info[type]]);
    }
}
