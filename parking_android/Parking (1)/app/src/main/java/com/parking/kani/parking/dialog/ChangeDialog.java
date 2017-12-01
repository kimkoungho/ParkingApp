package com.parking.kani.parking.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ServerConnection;

/**
 * Created by Kani on 2017-01-05.
 */

public class ChangeDialog extends BaseDialog
{

    LinearLayout content_change_layout, change_layout, change_spinner_layout, pw_change_layout;
    TextView change_name, change_car_kind, old_pw, old_pw_error, new_pw, new_pw_error, re_pw, re_pw_error;
    EditText change_content, old_pw_content, new_pw_content, re_pw_content;
    ImageButton pre_btn, next_btn;
    Button cancel_btn, change_btn;

    public final static int KIND_CHANGE = 1;   // 차량 분류 변경
    public final static int NAME_CHANGE = 2;   // 차량 종류 변경
    public final static int NUMBER_CHANGE = 3; // 차량 번호 변경
    public final static int PW_CHANGE = 4; // 비밀번호 변경

//    final String CARKIND[] = {"없음","승용(소)", "승용(대)", "승합", "화물(중)", "화물(대)"};
    final String CARKIND[] = {"상관없음", "오토바이", "승용(소)", "승용(대)", "승합", "SUV", "화물(중)", "화물(대)", "버스", "택시"};
    int currentKindIndex;
    int dialogType;
    String pw;

    private Context context;

    public ChangeDialog(final Context context, int themeResId)
    {
        super(context, themeResId);
        this.context = context;
    }

    int checkMyCarKind(String carKind)
    {
        for( int i = 0; i < CARKIND.length; i++ )
        {
            if( carKind.equals(CARKIND[i]) )
                return i;
        }

        return -1;
    }


    public void setDialogLayout(int dialogType, String changeContent)
    {
        this.dialogType = dialogType;

        if( dialogType == KIND_CHANGE )
        {
            change_name.setText("차량분류");
            change_layout.setVisibility(View.GONE);
            change_spinner_layout.setVisibility(View.VISIBLE);

            currentKindIndex = checkMyCarKind(changeContent);
            change_car_kind.setText(CARKIND[currentKindIndex]);
        }
        else if( dialogType == NAME_CHANGE )
        {
            change_name.setText("차량종류");
            change_content.setText(changeContent);
            change_content.selectAll();
        }
        else if( dialogType == NUMBER_CHANGE )
        {
            change_name.setText("차량번호");
            change_content.setText(changeContent);
            change_content.selectAll();
        }
        else if( dialogType == PW_CHANGE )
        {
            content_change_layout.setVisibility(View.GONE);
            pw_change_layout.setVisibility(View.VISIBLE);

            pw = changeContent;
        }
    }

    public void setDialogSetting()
    {
        this.setContentView(R.layout.dialog_change);

        content_change_layout = (LinearLayout) findViewById(R.id.content_change_layout);
        change_layout = (LinearLayout) findViewById(R.id.change_layout);
        change_spinner_layout = (LinearLayout) findViewById(R.id.change_spinner_layout);
        pw_change_layout = (LinearLayout) findViewById(R.id.pw_change_layout);

        change_name = (TextView) findViewById(R.id.change_name);
        change_car_kind = (TextView) findViewById(R.id.change_car_kind);
        old_pw = (TextView) findViewById(R.id.old_pw);
        old_pw_error = (TextView) findViewById(R.id.old_pw_error);
        new_pw = (TextView) findViewById(R.id.new_pw);
//        new_pw_error = (TextView) findViewById(R.id.new_pw_error);
        re_pw = (TextView) findViewById(R.id.re_pw);
        re_pw_error = (TextView) findViewById(R.id.re_pw_error);

        change_content = (EditText) findViewById(R.id.change_content);
        old_pw_content = (EditText) findViewById(R.id.old_pw_content);
        new_pw_content = (EditText) findViewById(R.id.new_pw_content);
        re_pw_content = (EditText) findViewById(R.id.re_pw_content);

        pre_btn = (ImageButton) findViewById(R.id.pre_btn);
        next_btn = (ImageButton) findViewById(R.id.next_btn);

        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        change_btn = (Button) findViewById(R.id.change_btn);

        old_pw_content.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if( old_pw_content.getText().toString().length() == 0 )
                    old_pw_error.setText("");
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
                if( new_pw_content.getText().toString().length() == 0 )
                    re_pw_error.setText("");
                else if( new_pw_content.getText().toString().equals(re_pw_content.getText().toString()) )
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

        new_pw_content.addTextChangedListener(new TextWatcher()
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

        pre_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( currentKindIndex == 0 )
                    currentKindIndex = CARKIND.length - 1;
                else
                    currentKindIndex--;

                change_car_kind.setText(CARKIND[currentKindIndex]);
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

                change_car_kind.setText(CARKIND[currentKindIndex]);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        change_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences login = context.getSharedPreferences("Login_Setting", 0);
                SharedPreferences.Editor login_editor = login.edit();
                String query_str = "";

                boolean flag = false;
                if( dialogType == KIND_CHANGE )
                {
                    flag = true;
                    login_editor.putString("car_category",change_car_kind.getText().toString());
                    login_editor.commit();
                    //Toast.makeText(getContext(), change_car_kind.getText() + "--로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    updateComponentListener.updateComponent(change_car_kind.getText().toString());
                    query_str = "?cmd=car_category&member_id="+login.getString("member_id","")+"&car_category="+change_car_kind.getText().toString();
                }
                else if( dialogType == NAME_CHANGE || dialogType == NUMBER_CHANGE )
                {
                    if( change_content.getText() == null || change_content.equals("") )
                    {
                        Toast.makeText(getContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        change_content.requestFocus();
                    }
                    else
                    {
                        flag = true;
                        //Toast.makeText(getContext(), change_content.getText() + "--로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        updateComponentListener.updateComponent(change_content.getText().toString());

                        if(dialogType == NAME_CHANGE)
                        {
                            login_editor.putString("car_type",change_content.getText().toString());
                            login_editor.commit();
                            query_str = "?cmd=car_type&member_id="+login.getString("member_id","")+"&car_type="+change_content.getText().toString();
                        }
                        else
                        {
                            login_editor.putString("car_num",change_content.getText().toString());
                            login_editor.commit();
                            query_str = "?cmd=car_num&member_id="+login.getString("member_id","")+"&car_num="+change_content.getText().toString();
                        }
                    }
                }
                else if( dialogType == PW_CHANGE )
                {
                    if( old_pw_content.getText().toString().equals(pw) )
                    {
                        if( new_pw_content.getText().toString().equals(re_pw_content.getText().toString()) )
                        {
                            flag = true;
                            //Toast.makeText(getContext(), new_pw_content.getText() + "--로 변경되었습니다.", Toast.LENGTH_SHORT).show();

                            login_editor.putString("member_password",new_pw_content.getText().toString());
                            login_editor.commit();

                            query_str = "?cmd=password&member_id="+login.getString("member_id","")+"&password="+new_pw_content.getText().toString();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "실패", Toast.LENGTH_SHORT).show();
                            re_pw_error.setText("비밀번호가 일치하지 않아요.");
                            re_pw_error.setTextColor(Color.RED);
                            re_pw_content.requestFocus();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "실패", Toast.LENGTH_SHORT).show();
                        old_pw_error.setText("현재 비밀번호가 달라요.");
                        old_pw_content.requestFocus();
                    }
                }

                ServerConnection conn = new ServerConnection();
                conn.setDialog(context);
                conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        String str = (String)object;
                        Log.d("TAG",str);
                        if(str.trim().equals("fail"))//update 실패
                        {
                            Toast.makeText(context,"update 실패 했습니다",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context,"update 성공",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(getContext(), "서버에 요청 실패!", Toast.LENGTH_SHORT).show();
                    }
                });
                String document = "/update_member.php";

                //Log.d(document,query_str);
                if(flag)
                    conn.execute(document+query_str);
                //dismiss();
            }
        });
    }

}