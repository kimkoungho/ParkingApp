package com.parking.kani.parking.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.dialog.BaseDialog;

import java.net.URLEncoder;

public class RegistActivity extends AppCompatActivity
{
    EditText id_content, pw_content, re_pw_content, car_name_content, car_number_content;
    TextView id_error, pw_error, re_pw_error, car_kind_content;
    ImageButton duplicate_check_btn, pre_btn, next_btn;
    Button cancel_btn, regist_btn;

//    final String CARKIND[] = {"상관없음", "승용(소)", "승용(대)", "승합", "화물(중)", "화물(대)"};
    private final String CARKIND[] = {"상관없음", "오토바이", "승용(소)", "승용(대)", "승합", "SUV", "화물(중)", "화물(대)", "버스", "택시"};
    int currentKindIndex = 0;
    boolean idFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        id_content = (EditText) findViewById(R.id.id_content);
        pw_content = (EditText) findViewById(R.id.pw_content);
        re_pw_content = (EditText) findViewById(R.id.re_pw_content);
        car_name_content = (EditText) findViewById(R.id.car_name_content);
        car_number_content = (EditText) findViewById(R.id.car_number_content);

        id_error = (TextView) findViewById(R.id.id_error);
//        pw_error = (TextView) findViewById(R.id.pw_error);
        re_pw_error = (TextView) findViewById(R.id.re_pw_error);
        car_kind_content = (TextView) findViewById(R.id.car_kind_content);

        duplicate_check_btn = (ImageButton) findViewById(R.id.duplicate_check_btn);
        pre_btn = (ImageButton) findViewById(R.id.pre_btn);
        next_btn = (ImageButton) findViewById(R.id.next_btn);

        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        regist_btn = (Button) findViewById(R.id.regist_btn);



        id_content.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                id_error.setText("");
                idFlag = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        re_pw_content.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if( pw_content.getText().toString().length() == 0 )
                    re_pw_error.setText("");
                else if( pw_content.getText().toString().equals(re_pw_content.getText().toString()) )
                {
                    re_pw_error.setText("비밀번호가 일치해요.");
                    re_pw_error.setTextColor(Color.GREEN);
                }
                else
                {
                    re_pw_error.setText("비밀번호가 일치하지 않아요.");
                    re_pw_error.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        pw_content.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                re_pw_error.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        duplicate_check_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //아이디 이메일 양식 검사
                if(!BaseDialog.checkEmail(id_content.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"이메일 약식이 성립하지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 서버에서 아이디 중복체크 로직 필요(임의의 로직으로 작성함)
                ServerConnection conn = new ServerConnection();
                conn.setDialog(RegistActivity.this);
                conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        String str = (String)object;

                        if(str.trim().equals("0")) //성공
                        {
                            id_error.setText("사용가능한 아이디에요.");
                            id_error.setTextColor(Color.GREEN);
                            idFlag = true;
                        }
                        else
                        {
                            id_error.setText("이미 존재하는 아이디에요.");
                            id_error.setTextColor(Color.RED);
                            idFlag = false;
                        }
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(getApplicationContext(),"서버에서 응답 실패",Toast.LENGTH_SHORT).show();
                    }
                });
                String document = "/id_check.php";
                String query_str = "?member_id="+id_content.getText().toString().trim();

                query_str = query_str.replaceAll(" ", "%20");

                Log.e("2222222222222222222", document + query_str);

                //Toast.makeText(getApplicationContext(),query_str,Toast.LENGTH_SHORT).show();
                conn.execute(document+query_str);
            }
        });

        pre_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( currentKindIndex == 0 )
                    currentKindIndex = CARKIND.length - 1;
                else
                    currentKindIndex--;

                car_kind_content.setText(CARKIND[currentKindIndex]);
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( currentKindIndex == CARKIND.length - 1 )
                    currentKindIndex = 0;
                else
                    currentKindIndex++;

                car_kind_content.setText(CARKIND[currentKindIndex]);
            }
        });


        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getBaseContext(), "회원가입을 취소합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        regist_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( idFlag )
                {
                    if( pw_content.getText().toString().equals(re_pw_content.getText().toString()) )
                    {
                        ServerConnection conn = new ServerConnection();
                        conn.setDialog(RegistActivity.this);
                        conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                        {
                            @Override
                            public void succeedTask(Object object)
                            {
                                String str = (String) object;

                                if(str.trim().equals("success"))
                                {
                                    Toast.makeText(getBaseContext(), "회원가입에 성공했어요.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(getBaseContext(), "회원가입에 실패했어요.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failTask()
                            {
                                Toast.makeText(getApplicationContext(),"서버에서 응답 실패",Toast.LENGTH_SHORT).show();
                            }
                        });
                        String document = "/join.php";

                        try{
                            String query_str = "?member_id="+id_content.getText().toString().trim()+"&password="+pw_content.getText().toString().trim()
                                    +"&car_category="+ URLEncoder.encode(car_kind_content.getText().toString().trim(),"utf-8")
                                    +"&car_type="+URLEncoder.encode(car_name_content.getText().toString().trim(),"utf-8")
                                    +"&car_num="+URLEncoder.encode(car_number_content.getText().toString().trim(),"utf-8");
                            Log.e("TAG",query_str);
                            conn.execute(document+query_str);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "실패", Toast.LENGTH_SHORT).show();
                        re_pw_error.setText("비밀번호가 일치하지 않아요.");
                        re_pw_error.setTextColor(Color.RED);
                        re_pw_content.requestFocus();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), "아이디가 유효하지 않거나 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}