package com.parking.kani.parking.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.GPSManager;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.dialog.BaseDialog;
import com.parking.kani.parking.dialog.LoginDialog;
import com.parking.kani.parking.fragment.CarPositionFragment;
import com.parking.kani.parking.fragment.CurrentPositionFragment;
import com.parking.kani.parking.fragment.FavoriteFragment;
import com.parking.kani.parking.fragment.TargetSearchFragment;
import com.parking.kani.parking.utility.DBHelper;
import com.tsengvn.typekit.TypekitContextWrapper;

import static com.parking.kani.parking.activity.MainActivity.MYCARSEARCH;

public class NaviActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private ImageButton navi_menu_login,navi_menu_regist,navi_menu_my_account,navi_menu_logout;
    private TextView member_id_text,car_type,car_num;
    private LinearLayout navi_login_default_layout,navi_login_succeed_layout;
    private DrawerLayout drawer;//네비 드로어

    private ImageView navi_car_icon;

    public static int LOGOUT_OK = 1, LOGOUT_NO = 0;
    private final int MYACCOUNT_REQUEST = 0;

    private BaseDialog dialog = null;//실행중인 다이얼로그
    private AlertDialog.Builder builder = null;//임시 다이얼로그

    private SharedPreferences login;
    private SharedPreferences.Editor login_editor;

    private final String CARKIND[] = {"상관없음", "오토바이", "승용(소)", "승용(대)", "승합", "SUV", "화물(중)", "화물(대)", "버스", "택시"};
    private int CARKIND_RESOURCEID[] = {R.drawable.icon_car0, R.drawable.icon_car1, R.drawable.icon_car2, R.drawable.icon_car3, R.drawable.icon_car4, R.drawable.icon_car5, R.drawable.icon_car6, R.drawable.icon_car7, R.drawable.icon_car8, R.drawable.icon_car9};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navi_login_default_layout = (LinearLayout) findViewById(R.id.navi_login_default_layout);
        navi_menu_login = (ImageButton)findViewById(R.id.navi_menu_login);
        navi_menu_regist = (ImageButton)findViewById(R.id.navi_menu_regist);
        navi_login_succeed_layout = (LinearLayout)findViewById(R.id.navi_login_succeed_layout);
        navi_menu_my_account = (ImageButton)findViewById(R.id.navi_menu_my_account);
        navi_menu_logout = (ImageButton)findViewById(R.id.navi_menu_logout);
        member_id_text = (TextView)findViewById(R.id.member_id_text);
        car_type = (TextView)findViewById(R.id.car_type);
        car_num = (TextView)findViewById(R.id.car_num);
        navi_car_icon = (ImageView) findViewById(R.id.navi_car_icon);


        //다이얼로그 필요한것 여기서부터
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        final int width = dm.widthPixels;
        final int height = dm.heightPixels;
        //다이얼로그 필요한것 여기까지

        navi_menu_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Log.d("111","login_btn");
                //Toast.makeText(getApplicationContext(),"login_btn",Toast.LENGTH_SHORT).show();
                drawer.closeDrawer(GravityCompat.START);

                dialog = new LoginDialog(NaviActivity.this, R.style.Theme_Dialog);


                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                loginDialog.setContentView(R.layout.dialog_login);

                ((LoginDialog)dialog).setDialogSetting();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                params.width = (int) (width * 0.9);
//                params.height = (int) (height * 0.8);
                params.width = 1200;
                params.height = 800;
                dialog.getWindow().setAttributes(params);

                (dialog).setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)//로그인 성공시 네비 드로어 업데이트
                    {
                        //Log.d("TAG",setting.getString("member_id","1111"));
                        member_id_text.setText(login.getString("member_id",""));
                        car_type.setText(login.getString("car_type",""));
                        car_num.setText(login.getString("car_num",""));

                        //기본 로그인 레이아웃 날리고 로그인 된 상태의 레이아웃 보이기
                        navi_login_default_layout.setVisibility(View.GONE);
                        navi_login_succeed_layout.setVisibility(View.VISIBLE);

                        //드로어 열기
                        drawer.openDrawer(GravityCompat.START);
                    }
                });

                dialog.show();

                //imm.hideSoftInputFromWindow(member_id.getWindowToken(), 0);
            }
        });

        navi_menu_regist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NaviActivity.this, RegistActivity.class);
                startActivity(intent);
            }
        });

        navi_menu_my_account.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NaviActivity.this, MyAcountActivity.class);
                startActivityForResult(intent,MYACCOUNT_REQUEST);
            }
        });

        navi_menu_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //다이얼로그 띄우기
                // 다이얼로그 생성
                builder = new AlertDialog.Builder(NaviActivity.this);
                builder.setTitle("로그아웃 하시겠습니까?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //저장된 회원 정보 모두 삭제
                        login_editor.putBoolean("auto_login_check",false);
                        login_editor.remove("member_password");
                        login_editor.remove("car_category");
                        login_editor.remove("car_type");
                        login_editor.remove("car_num");
                        login_editor.commit();

                        //로그인 다이얼로그 열기
                        Toast.makeText(NaviActivity.this,"로그아웃 되었습니다",Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();

                        //로그인 된 상태의 레이아웃 날리기
                        navi_login_succeed_layout.setVisibility(View.GONE);
                        navi_login_default_layout.setVisibility(View.VISIBLE);

                        //드로어 열기
                        drawer.openDrawer(GravityCompat.START);
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();

            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        login = getSharedPreferences("Login_Setting", 0);
        login_editor = login.edit();

        //이전 페이지에서 무엇을 요청했는지에 따라 각각 다른 fragment 를 달아야 함!
        setFragment();

        //만약 로그인 되어 있었다면 drawer update
        if(login.getString("member_password","") != "")
        {
            member_id_text.setText(login.getString("member_id",""));
            car_type.setText(login.getString("car_type",""));
            car_num.setText(login.getString("car_num",""));

            for( int i = 0; i < CARKIND.length; i++ )
            {
                if( login.getString("car_category","").equals(CARKIND[i]) )
                {
                    navi_car_icon.setBackgroundResource(CARKIND_RESOURCEID[i]);
                    break;
                }
            }

            //기본 로그인 레이아웃 날리고 로그인 된 상태의 레이아웃 보이기
            navi_login_default_layout.setVisibility(View.GONE);
            navi_login_succeed_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            //FragmentManager fragmentManager = getFragmentManager();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.detach()

            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent intent)
    {
        super.onActivityResult(request_code,result_code,intent);

        if(request_code == MYACCOUNT_REQUEST)
        {
            if(result_code == LOGOUT_OK)
            {
                //로그인 된 상태의 레이아웃 날리기
                navi_login_succeed_layout.setVisibility(View.GONE);
                navi_login_default_layout.setVisibility(View.VISIBLE);
            }
            else if(request_code == LOGOUT_NO)
            {
                //navi_drawer 초기화하기
                member_id_text.setText(login.getString("member_id",""));
                car_type.setText(login.getString("car_type",""));
                car_num.setText(login.getString("car_num",""));
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting)
        {
            //Log.d("TAG","setting icon click");
            Intent intent=new Intent(NaviActivity.this, SettingActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment()
    {
        Intent intent=getIntent();
        int fragment_no=intent.getExtras().getInt("fragment");

        Fragment fragment=null;
        if(fragment_no==MainActivity.MYLOCATION)
        {
            fragment = new CurrentPositionFragment();
        }
        else if(fragment_no==MainActivity.SEARCHLOCATION)
        {
            fragment = new TargetSearchFragment();
        }
        else if(fragment_no==MainActivity.BOOKMARK)
        {
            fragment = new FavoriteFragment();
        }
        else if(fragment_no== MYCARSEARCH)
        {
            fragment = new CarPositionFragment(); //
        }

        if(fragment==null) return;

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_place,fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onClick(View view) {
        int v_id=view.getId();

        switch(v_id)
        {
            case R.id.navi_home_btn:
            {
                finish();
                break;
            }
            case R.id.navi_current_btn:
            {
                //gps 위치 잡고 서버로 요청함
                //서버에서 return 값이 있으면, 액티비티를 넘어가서 서버에 요청? 요청하고 이동?
                GPSManager manager=new GPSManager(NaviActivity.this);
                manager.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object) {//성공 페이지 이동
                        String string=(String)object;
                        Log.d("TASK",string);

                        string = "37.554521"+"-"+"126.9684543";//임시 서울역

                        getIntent().putExtra("start_position",string);

                        ServerConnection conn=new ServerConnection();
                        conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                        {
                            @Override
                            public void succeedTask(Object object)
                            {
                                getIntent().putExtra("fragment",MainActivity.MYLOCATION);
                                getIntent().putExtra("server_data",(String)object);
                                setFragment();
                            }

                            @Override
                            public void failTask()
                            {
                                Toast.makeText(getApplicationContext(),"서버에 데이터 전송 실패!",Toast.LENGTH_SHORT).show();
                            }
                        });

                        //임시 현재 위치
                        string = "37.554521"+"-"+"126.9684543";

                        SharedPreferences setting=getSharedPreferences("setting", 0);
                        String query_str=MainActivity.makeQueryString(setting);
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
                break;
            }
            case R.id.navi_target_btn:
            {
                getIntent().putExtra("fragment",MainActivity.SEARCHLOCATION);
                setFragment();
                break;
            }
            case R.id.navi_farvorite_btn:
            {
                DBHelper dbHelper = new DBHelper(getApplicationContext(),"favorite.db",null,1);
                String park_no_list_str = dbHelper.select();

                if(park_no_list_str == "")
                {
                    getIntent().putExtra("fragment",MainActivity.BOOKMARK);
                    setFragment();
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

                            getIntent().putExtra("fragment",MainActivity.BOOKMARK);
                            getIntent().putExtra("server_data", str);
                            setFragment();
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

                break;
            }
            case R.id.navi_car_position_btn:
            {
                String member_pw = login.getString("member_password","");

                if(member_pw == null || member_pw.equals("") ) //로그인 필요
                {
                    drawer.closeDrawer(GravityCompat.START);

                    dialog = new LoginDialog(NaviActivity.this, R.style.Theme_Dialog);


                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                loginDialog.setContentView(R.layout.dialog_login);

                    ((LoginDialog)dialog).setDialogSetting();

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                params.width = (int) (width * 0.9);
//                params.height = (int) (height * 0.8);
                    params.width = 1200;
                    params.height = 800;
                    dialog.getWindow().setAttributes(params);

                    (dialog).setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                    {
                        @Override
                        public void updateComponent(Object obj)//로그인 성공시 네비 드로어 업데이트
                        {
                            //Log.d("TAG",setting.getString("member_id","1111"));
                            member_id_text.setText(login.getString("member_id",""));
                            car_type.setText(login.getString("car_type",""));
                            car_num.setText(login.getString("car_num",""));

                            //기본 로그인 레이아웃 날리고 로그인 된 상태의 레이아웃 보이기
                            navi_login_default_layout.setVisibility(View.GONE);
                            navi_login_succeed_layout.setVisibility(View.VISIBLE);

                            //드로어 열기
                            drawer.openDrawer(GravityCompat.START);

                            getIntent().putExtra("fragment", MYCARSEARCH);
                            setFragment();
                        }
                    });

                    dialog.show();
                    break;
                }
                else
                {
                    getIntent().putExtra("fragment", MYCARSEARCH);
                    setFragment();
                    break;
                }
            }
            case R.id.navi_setting_btn:
            {
                Intent intent=new Intent(NaviActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            }
            /*case R.id.navi_manual_btn:
            {

                break;
            }*/
        }
    }
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
