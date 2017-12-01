package com.parking.kani.parking.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.GPSManager;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.dialog.BaseDialog;
import com.parking.kani.parking.dialog.LoginDialog;
import com.parking.kani.parking.utility.BackPressCloseHandler;
import com.parking.kani.parking.utility.DBHelper;
import com.tsengvn.typekit.TypekitContextWrapper;

public class MainActivity extends AppCompatActivity
{
    //어떤 fragment 를 호출할지
    public static final int MYLOCATION=1;
    public static final int SEARCHLOCATION=2;
    public static final int BOOKMARK=3;
    public static final int MYCARSEARCH=4;

    private Button my_pos_btn;
    private Button my_target_btn;
    private Button bookmark_btn;
    private Button my_car_btn;

    private Intent splashIntent;
    private Intent naviIntent;

    private SharedPreferences login;
    private SharedPreferences.Editor login_editor;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        splashIntent=new Intent(this, SplashActivity.class);
        startActivity(splashIntent);

        setContentView(R.layout.activity_main);

        //임시로 로그인 되었다고 가정
        login = getSharedPreferences("Login_Setting", 0);
        login_editor=login.edit();
//        login_editor.putBoolean("login_flag",true);
//        login_editor.putString("member_id","koung2@naver.com");
//        login_editor.commit();


        my_pos_btn=(Button)findViewById(R.id.my_pos_btn);
        my_pos_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //gps 위치 잡고 서버로 요청함
                //서버에서 return 값이 있으면, 액티비티를 넘어가서 서버에 요청? 요청하고 이동?
                GPSManager manager=new GPSManager(MainActivity.this);
                manager.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object) {//성공 페이지 이동
                        String string=(String)object;
                        Log.d("TASK",string);

                        string = "37.554521"+"-"+"126.9684543";//임시 서울역

                        naviIntent=new Intent(MainActivity.this, NaviActivity.class);
                        naviIntent.putExtra("start_position",string);

                        ServerConnection conn=new ServerConnection();
                        conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                        {
                            @Override
                            public void succeedTask(Object object)
                            {
                                naviIntent.putExtra("fragment",MYLOCATION);
                                naviIntent.putExtra("server_data",(String)object);
                                startActivity(naviIntent);
                            }

                            @Override
                            public void failTask()
                            {
                                Toast.makeText(getApplicationContext(),"서버에서 데이터 응답 실패",Toast.LENGTH_SHORT).show();
                            }
                        });

                        //임시 현재 위치
                        string = "37.554521"+"-"+"126.9684543";

                        SharedPreferences setting=getSharedPreferences("setting", 0);

                        String query_str=makeQueryString(setting);
                        String document="/search_parking.php";

                        conn.execute(document+query_str+"&latitude="+string.split("-")[0]+"&longitude="+string.split("-")[1]);

                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(getApplicationContext(),"서버 전송 실패",Toast.LENGTH_SHORT).show();
                    }
                });
                manager.execute();
            }
        });

        my_target_btn=(Button)findViewById(R.id.my_target_btn);
        my_target_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(MainActivity.this, NaviActivity.class);
                intent.putExtra("fragment",SEARCHLOCATION);
                startActivity(intent);
            }
        });

        bookmark_btn=(Button)findViewById(R.id.bookmark_btn);
        bookmark_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DBHelper dbHelper = new DBHelper(getApplicationContext(),"favorite.db",null,1);
                String park_no_list_str = dbHelper.select();

                if(park_no_list_str == "")
                {
                    Intent intent=new Intent(MainActivity.this, NaviActivity.class);
                    intent.putExtra("server_data", "");
                    intent.putExtra("fragment",BOOKMARK);
                    startActivity(intent);
                }
                else
                {
                    ServerConnection conn = new ServerConnection();
                    conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                    {
                        @Override
                        public void succeedTask(Object object)
                        {
                            String str = (String) object;

                            Intent intent=new Intent(MainActivity.this, NaviActivity.class);
                            intent.putExtra("server_data", str);
                            intent.putExtra("fragment",BOOKMARK);
                            startActivity(intent);
                        }

                        @Override
                        public void failTask()
                        {
                            Toast.makeText(getApplicationContext(),"서버에서 데이터 받기 실패",Toast.LENGTH_SHORT).show();
                        }
                    });
                    String document = "/favorite_parking.php";
                    conn.execute(document+"?park_no_list_str="+park_no_list_str);
                }
            }
        });

        my_car_btn=(Button)findViewById(R.id.my_car_btn);
        my_car_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String member_pw = login.getString("member_password","");

                if(member_pw == null || member_pw.equals("") ) //로그인 필요
                {
                    LoginDialog dialog = new LoginDialog(MainActivity.this, R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setDialogSetting();

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                params.width = (int) (width * 0.9);
//                params.height = (int) (height * 0.8);
                    params.width = 1200;
                    params.height = 800;
                    dialog.getWindow().setAttributes(params);

                    dialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                    {
                        @Override
                        public void updateComponent(Object obj)
                        {
                            Intent intent = new Intent(MainActivity.this, NaviActivity.class);
                            intent.putExtra("fragment",MYCARSEARCH);
                            startActivity(intent);
                        }
                    });

                    dialog.show();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, NaviActivity.class);
                    intent.putExtra("fragment",MYCARSEARCH);
                    startActivity(intent);
                }
            }
        });

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        login_editor.remove("member_pw");
        login_editor.commit();
    }

    //설정 정보를 이용하여 쿼리 스트링 만들기
    public static String makeQueryString(SharedPreferences setting)
    {
        String query_string="";

        SharedPreferences search_setting=setting;
        String search_type=SettingActivity.setting_list[0][search_setting.getInt("search",0)];


        String dist=SettingActivity.setting_list[1][search_setting.getInt("distance",0)];
        double distance=Double.parseDouble(dist.split("m")[0])/1000.0;
        if(dist.charAt(dist.length()-1)=='+')
            distance=-1.0;
//        if(dist.split("m")[1]!=null)
//            distance=-1.0;

        String time_cost=SettingActivity.setting_list[2][search_setting.getInt("cost",0)];//1시간 기준
        if(!time_cost.equals("상관없음"))
            time_cost=time_cost.split("원")[0];
        String park_item=SettingActivity.setting_list[3][search_setting.getInt("park_item",0)];
        String car=SettingActivity.setting_list[4][search_setting.getInt("car",0)];

        query_string="?search_type="+search_type+"&distance="+distance +"&cost="+time_cost+"&park_item="+park_item+"&car="+car;

        Log.d("QUERY_STRING",query_string);
        return query_string;
    }
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
