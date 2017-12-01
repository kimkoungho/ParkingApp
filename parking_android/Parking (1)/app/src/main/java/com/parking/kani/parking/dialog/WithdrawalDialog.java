package com.parking.kani.parking.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.activity.MyAcountActivity;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ServerConnection;

/**
 * Created by Kani on 2017-01-05.
 */

public class WithdrawalDialog extends BaseDialog
{
    private Button yes_btn, no_btn;
    private Context context;

    public WithdrawalDialog(final Context context, int themeResId)
    {
        super(context, themeResId);
        this.context=context;
    }

    public void setDialogSetting()
    {
        this.setContentView(R.layout.dialog_withdrawal);

        yes_btn = (Button) findViewById(R.id.yes_btn);
        no_btn = (Button) findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final ServerConnection conn = new ServerConnection();
                conn.setDialog(context);
                conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        String str = (String) object;
                        if(str.trim().equals("success"))
                        {
                            dismiss();
                            updateComponentListener.updateComponent("");
                        }
                        else
                            Toast.makeText(context,"서버에서 계정삭제에 실패했습니다",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(context, "서버로 전송 실패",Toast.LENGTH_SHORT).show();
                    }
                });
                //저장된 회원 정보 삭제
                SharedPreferences login = context.getSharedPreferences("Login_Setting", 0);
                SharedPreferences.Editor login_editor = login.edit();

                login_editor.putBoolean("auto_login_check",false);
                login_editor.remove("member_password");
                login_editor.remove("car_category");
                login_editor.remove("car_type");
                login_editor.remove("car_num");
                login_editor.commit();

                String document = "/update_member.php";
                String query_str = "?cmd=withdrawl&member_id="+login.getString("member_id","");
                conn.execute(document+query_str);

                Toast.makeText(getContext(), "해당 계정을 삭제합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }
}
