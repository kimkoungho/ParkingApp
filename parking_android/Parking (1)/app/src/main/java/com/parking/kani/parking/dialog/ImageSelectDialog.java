package com.parking.kani.parking.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.parking.kani.parking.R;

/**
 * Created by Kani on 2017-01-06.
 */

public class ImageSelectDialog extends BaseDialog
{
    private Button camera_btn, gallery_btn, cancel_btn;

    public ImageSelectDialog(final Context context, int themeResId)
    {
        super(context, themeResId);
    }

    public void setDialogSetting()
    {
        this.setContentView(R.layout.dialog_image_select);

        camera_btn = (Button) findViewById(R.id.camera_btn);
        gallery_btn = (Button) findViewById(R.id.gallery_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        camera_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateComponentListener.updateComponent("CAMERA");
            }
        });

        gallery_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateComponentListener.updateComponent("GALLERY");
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateComponentListener.updateComponent("CANCEL");
            }
        });
    }
}
