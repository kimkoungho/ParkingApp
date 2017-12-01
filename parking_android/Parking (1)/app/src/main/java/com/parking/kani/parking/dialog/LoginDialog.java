package com.parking.kani.parking.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.activity.MainActivity;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kani on 2017-01-04.
 */

public class LoginDialog extends BaseDialog
{
    private ImageButton dialog_close_btn, login_btn;
    private EditText member_id, member_password;
    private CheckBox save_id_chk, auto_login_chk;

    private Context context;
    private SharedPreferences login;
    private SharedPreferences.Editor login_editor;

    public LoginDialog(final Context context, int themeResId)
    {
        super(context, themeResId);

        this.context=context;

//        this.setContentView(R.layout.dialog_login);

//        dialog_close_btn = (ImageButton) findViewById(R.id.dialog_close_btn);
//
//        dialog_close_btn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                dismiss();
//            }
//        });
    }

    public void setDialogSetting()
    {
        this.setContentView(R.layout.dialog_login);

        dialog_close_btn = (ImageButton) findViewById(R.id.dialog_close_btn);
        login_btn = (ImageButton) findViewById(R.id.login_btn);
        member_id = (EditText) findViewById(R.id.member_id);
        member_password = (EditText) findViewById(R.id.member_passsword);
        save_id_chk = (CheckBox)findViewById(R.id.save_id_chk);
        auto_login_chk = (CheckBox)findViewById(R.id.auto_login_chk);

        dialog_close_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (member_id.getText().toString().equals(""))
                {
                    Toast.makeText(context,"id 나 password를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    member_id.requestFocus();
                    return;
                }
                if(member_password.getText().toString().equals(""))
                {
                    Toast.makeText(context,"id 나 password를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    member_password.requestFocus();
                    return;
                }

                //email 양식 검사
                if(!checkEmail(member_id.getText().toString().trim()))
                {
                    Toast.makeText(context,"id 가 email 양식이 아닙니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                //로그인 기능 구현
                final ServerConnection conn = new ServerConnection();
                conn.setDialog(context);
                conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        //입력창 닫기
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(member_id.isFocusable())
                            imm.hideSoftInputFromWindow(member_id.getWindowToken(), 0);
                        else if(member_password.isFocusable())
                            imm.hideSoftInputFromWindow(member_password.getWindowToken(), 0);

                        String str = (String) object;
                        str=str.trim();

                        if(str.equals("fail"))//로그인 실패
                        {
                            Toast.makeText(context,"아이디나 비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();

                            member_id.requestFocus();
                            member_id.selectAll();
                        }
                        else//로그인 성공
                        {
                            //Log.d("login_succeed",context.getClass().toString());
                            Toast.makeText(context,"로그인 성공",Toast.LENGTH_SHORT).show();

                            try
                            {
                                JSONArray jsonArray = new JSONArray(str);
                                JSONObject jsonObject = (JSONObject)jsonArray.get(0);
                                login_editor.putString("member_id",member_id.getText().toString().trim());
                                login_editor.putString("member_password",member_password.getText().toString().trim());
                                login_editor.putString("car_category",jsonObject.getString("car_category"));
                                login_editor.putString("car_type",jsonObject.getString("car_type"));
                                login_editor.putString("car_num",jsonObject.getString("car_num"));
                                login_editor.commit();
                            }
                            catch(JSONException e)
                            {
                                e.printStackTrace();
                            }

                            updateComponentListener.updateComponent("");
                            dismiss();
                        }
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(context,"서버에서 데이터 수신 실패",Toast.LENGTH_SHORT).show();
                    }
                });
                String document = "/login.php";
                String query_str = "?member_id="+member_id.getText().toString().trim()+"&member_pw="+member_password.getText().toString().trim();
                conn.execute(document+query_str);
            }
        });

        login = context.getSharedPreferences("Login_Setting", 0);
        login_editor= login.edit();

        //setting 정보 가져오기
        boolean save_id_checked=login.getBoolean("save_id_check",false);
        boolean auto_login_checked=login.getBoolean("auto_login_check", false);
        save_id_chk.setChecked(save_id_checked);
        auto_login_chk.setChecked(auto_login_checked);

        if(save_id_checked)
        {
            member_id.setText(login.getString("member_id", ""));
            member_password.callOnClick();
        }
        else
            member_id.callOnClick();

        if(auto_login_checked)
        {
            member_id.setText(login.getString("member_id",""));
            member_password.setText(login.getString("member_password",""));
            login_btn.callOnClick();
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();

        //dismiss 이벤트 발생시 check box 의 상태 저장
        login_editor.putBoolean("save_id_check",save_id_chk.isChecked());
        if(save_id_chk.isChecked())
            login_editor.putString("member_id",member_id.getText().toString().trim());
        login_editor.putBoolean("auto_login_check",auto_login_chk.isChecked());
        if(auto_login_chk.isChecked())
        {
            login_editor.putString("member_id",member_id.getText().toString().trim());
            login_editor.putString("c",member_password.getText().toString().trim());
        }
        login_editor.commit();

        Log.d("상태 저장",save_id_chk.isChecked()+"...."+auto_login_chk.isChecked());
    }

}
