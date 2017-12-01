package com.parking.kani.parking.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parking.kani.parking.R;

/**
 * Created by Kani on 2017-01-06.
 */

public class RegistImageDialog extends BaseDialog
{
    Button yes_btn, no_btn;

    boolean result;

    public RegistImageDialog(final Context context, int themeResId)
    {
        super(context, themeResId);
    }

    public boolean getResult()
    {
        return result;
    }

    public void setDialogSetting()
    {
        this.setContentView(R.layout.dialog_regist_image);

        yes_btn = (Button) findViewById(R.id.yes_btn);
        no_btn = (Button) findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(getContext(), "이미지를 등록합니다.", Toast.LENGTH_SHORT).show();
                updateComponentListener.updateComponent("yes");
                //dismiss();
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateComponentListener.updateComponent("no");
                //dismiss();
            }
        });
    }
}

