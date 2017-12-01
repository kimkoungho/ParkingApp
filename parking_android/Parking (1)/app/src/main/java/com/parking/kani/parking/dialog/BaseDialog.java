package com.parking.kani.parking.dialog;

import android.app.Dialog;
import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kani on 2016-12-26.
 */

public class BaseDialog extends Dialog
{
    protected UpdateComponentListener updateComponentListener;

    BaseDialog(final Context context, int themeResId)
    {
        super(context, themeResId);

        this.setCancelable(false);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        dismiss();
    }

    //이메일 양식 검사
    public static boolean checkEmail(String email)
    {
        String regex = "^[_0-9a-zA-Z-]+@[0-9a-zA-Z-]+(.[_0-9a-zA-Z-]+)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(email);
        return match.matches();
    }

    public void setUpdateComponentListener(UpdateComponentListener listener)
    {
        this.updateComponentListener=listener;
    }

    public interface UpdateComponentListener
    {
        public void updateComponent(Object obj);
    }
}
