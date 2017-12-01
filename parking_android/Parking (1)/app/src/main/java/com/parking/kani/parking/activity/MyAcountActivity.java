package com.parking.kani.parking.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.dialog.BaseDialog;
import com.parking.kani.parking.dialog.ChangeDialog;
import com.parking.kani.parking.dialog.WithdrawalDialog;

public class MyAcountActivity extends AppCompatActivity {

    private TextView my_account_user_id, my_account_car_name, my_account_car_number, my_account_car_position_addr;
    private ImageButton my_account_car_position_btn, my_account_logout_btn, my_account_withdrawal_btn;
    private ImageView my_account_car_kind;
    private Button car_kind_btn, car_name_btn, car_number_btn, pw_change_btn;

    private BaseDialog dialog;

    private SharedPreferences login;
    private SharedPreferences.Editor login_editor;

    private final String CARKIND[] = {"상관없음", "오토바이", "승용(소)", "승용(대)", "승합", "SUV", "화물(중)", "화물(대)", "버스", "택시"};
    private int CARKIND_RESOURCEID[] = {R.drawable.icon_car0, R.drawable.icon_car1, R.drawable.icon_car2, R.drawable.icon_car3, R.drawable.icon_car4, R.drawable.icon_car5, R.drawable.icon_car6, R.drawable.icon_car7, R.drawable.icon_car8, R.drawable.icon_car9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_acount);

        my_account_user_id = (TextView) findViewById(R.id.my_account_user_id);
        my_account_car_name = (TextView) findViewById(R.id.my_account_car_name);
        my_account_car_number = (TextView) findViewById(R.id.my_account_car_number);
        my_account_car_position_addr = (TextView) findViewById(R.id.my_account_car_position_addr);

        my_account_car_position_btn = (ImageButton) findViewById(R.id.my_account_car_position_btn);
        my_account_logout_btn = (ImageButton) findViewById(R.id.my_account_logout_btn);
        my_account_withdrawal_btn = (ImageButton) findViewById(R.id.my_account_withdrawal_btn);

        my_account_car_kind = (ImageView) findViewById(R.id.my_account_car_kind);

        car_kind_btn = (Button) findViewById(R.id.car_kind_btn);
        car_name_btn = (Button) findViewById(R.id.car_name_btn);
        car_number_btn = (Button) findViewById(R.id.car_number_btn);
        pw_change_btn = (Button) findViewById(R.id.pw_change_btn);

        // 주차위치 버튼.......이 곳에서 현재 Activity를 종료하고 아래에 싸여있던 NaviActivity에서 Fragment를 CarPositionFragment로 이동
        my_account_car_position_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getBaseContext(), "내 주차 정보를 보러 갑니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 로그아웃 버튼
        //NaviActivity 의 네비 드로어 고쳐야 함
        my_account_logout_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //다이얼로그 띄우기
                // 다이얼로그 생성
                AlertDialog.Builder logout_dialog = new AlertDialog.Builder(MyAcountActivity.this);
                logout_dialog.setTitle("로그아웃 하시겠습니까?");
                logout_dialog.setPositiveButton("yes", new DialogInterface.OnClickListener()
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

                        //로그아웃 되었다고 표현
                        setResult(NaviActivity.LOGOUT_OK);

                        //로그인 다이얼로그 열기
                        Toast.makeText(MyAcountActivity.this,"로그아웃 되었습니다",Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();

                        finish();
                    }
                });
                logout_dialog.setNegativeButton("no", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });

                logout_dialog.show();
            }
        });

        // 회원탈퇴 버튼
        my_account_withdrawal_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = new WithdrawalDialog(MyAcountActivity.this, R.style.Theme_Dialog);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                ((WithdrawalDialog)dialog).setDialogSetting();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = 1000;
                params.height = 750;
                dialog.getWindow().setAttributes(params);

                dialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)
                    {
                        //로그아웃 되었다고 표현
                        setResult(NaviActivity.LOGOUT_OK);
                        finish();
                    }
                });

                dialog.show();
            }
        });

        // 차량 분류 버튼 클릭시...
        car_kind_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = new ChangeDialog(MyAcountActivity.this, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ((ChangeDialog)dialog).setDialogSetting();
                ((ChangeDialog)dialog).setDialogLayout(ChangeDialog.KIND_CHANGE, car_kind_btn.getText().toString());

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = 1200;
                params.height = 530;
                dialog.getWindow().setAttributes(params);

                dialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)
                    {
                        car_kind_btn.setText((String)obj);
                    }
                });
                dialog.show();
            }
        });

        // 차량 종류 버튼 클릭시...
        car_name_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = new ChangeDialog(MyAcountActivity.this, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ((ChangeDialog)dialog).setDialogSetting();
                ((ChangeDialog)dialog).setDialogLayout(ChangeDialog.NAME_CHANGE, car_name_btn.getText().toString());

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = 1200;
                params.height = 530;
                dialog.getWindow().setAttributes(params);
                dialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)
                    {
                        car_name_btn.setText((String)obj);
                        my_account_car_name.setText((String)obj);
                    }
                });

                dialog.show();

//                changeDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
//                {
//                    @Override
//                    public void onDismiss(DialogInterface dialog)
//                    {
//                        car_name_btn.setText("");
//                    }
//                });
            }
        });

        // 차량 번호 버튼 클릭시...
        car_number_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = new ChangeDialog(MyAcountActivity.this, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ((ChangeDialog)dialog).setDialogSetting();
                ((ChangeDialog)dialog).setDialogLayout(ChangeDialog.NUMBER_CHANGE, car_number_btn.getText().toString());

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = 1200;
                params.height = 530;
                dialog.getWindow().setAttributes(params);

                dialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)
                    {
                        car_number_btn.setText((String)obj);
                        my_account_car_number.setText((String)obj);
                    }
                });
                dialog.show();

            }
        });

        // 비밀번호 변경 버튼 클릭시...
        pw_change_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog = new ChangeDialog(MyAcountActivity.this, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ((ChangeDialog)dialog).setDialogSetting();
                ((ChangeDialog)dialog).setDialogLayout(ChangeDialog.PW_CHANGE,login.getString("member_password",""));

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = 1200;
                params.height = 1380;
                dialog.getWindow().setAttributes(params);

                dialog.show();
            }
        });

        //값 초기화
        login = getSharedPreferences("Login_Setting", 0);
        login_editor = login.edit();

        SharedPreferences my_car_pos = getSharedPreferences("MyCarPos",0);

        my_account_user_id.setText(login.getString("member_id",""));
        my_account_car_name.setText(login.getString("car_type",""));
        my_account_car_number.setText(login.getString("car_num",""));

        for( int i = 0; i < CARKIND.length; i++ )
        {
            if( login.getString("car_category","").equals(CARKIND[i]) )
            {
                my_account_car_kind.setBackgroundResource(CARKIND_RESOURCEID[i]);
                break;
            }
        }


        my_account_car_position_addr.setText(my_car_pos.getString("my_car_addr","주차위치 없음"));

        car_kind_btn.setText(login.getString("car_category",""));
        car_name_btn.setText(login.getString("car_type",""));
        car_number_btn.setText(login.getString("car_num",""));


        setResult(0);//기본이 로그아웃 되지 않음
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();


    }
}
