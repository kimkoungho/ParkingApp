package com.parking.kani.parking.activity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.parking.kani.parking.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.parking.kani.parking.utility.PermissionRequester;

public class SplashActivity extends AppCompatActivity
{
    public boolean finishFlag=false;
    Handler hd;
    SharedPreferences permission=null;
    SharedPreferences.Editor permission_editor=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        hd = new Handler();

        permission = getSharedPreferences("permissionSetting", 0);
        permission_editor=permission.edit();

        finishFlag=permission.getBoolean("permission_location",false);

        if(!finishFlag)
        {
            // 위치 권한
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // 마쉬멜로우 6.0 이상 권한 허용
                this.setPermission();
            }
            else
            {
                //hd.postDelayed(new splashhandler() , 5000); // 5초 후에 hd Handler 실행
                finishFlag = true;
            }
        }



        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        ImageView back=(ImageView)findViewById(R.id.splash_image);
        GlideDrawableImageViewTarget imageViewTarget01 = new GlideDrawableImageViewTarget(back);
        Glide.with(this).load(R.drawable.splash_img).into(imageViewTarget01);


        if(finishFlag==true)
            hd.postDelayed(new splashhandler() , 5000); // 5초 후에 hd Handler 실행
        else
        {
            //Toast.makeText(getApplicationContext(),"오류....",Toast.LENGTH_LONG).show();
            //권한 변경 요청

        }

        //this.onDestroy();
    }

    private class splashhandler implements Runnable
    {
        public void run()
        {
            finish();
        }
    }

    public void setPermission()
    {
        int result = new PermissionRequester.Builder(SplashActivity.this)
                .setTitle("권한 요청")
                .setMessage("권한을 요청합니다.")
                .setPositiveButtonName("네")
                .setNegativeButtonName("아니요.")
                .create()
                .request(Manifest.permission.ACCESS_FINE_LOCATION, 1000 , new PermissionRequester.OnClickDenyButtonListener()
                {
                    @Override
                    public void onClick(Activity activity)
                    {
                        Log.d("RESULT", "취소함.");
                        Toast.makeText(getApplicationContext(),"권한을 허용해주셔야 합니다", Toast.LENGTH_LONG).show();
                        //MainActivity.mainActivity.showDailog();
                        finish();

                    }
                });

        if (result == PermissionRequester.ALREADY_GRANTED)
        {
            Log.d("RESULT", "권한이 이미 존재함.");
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getApplicationContext(),"허용",Toast.LENGTH_LONG).show();

        }
        else if(result == PermissionRequester.NOT_SUPPORT_VERSION)
            Log.d("RESULT", "마쉬멜로우 이상 버젼 아님.");
        else if(result == PermissionRequester.REQUEST_PERMISSION)
            Log.d("RESULT", "요청함. 응답을 기다림.");

    }

    /**
     * 신규로 권한을 요청해 그 응답이 돌아왔을 경우 실행됨.
     * @param requestCode : 권한 요청시 전송했던 코드.
     * @param permissions : 요청한 권한
     * @param grantResults : 해당 권한에 대한 결과
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        if ( requestCode == 1000 )
        {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
            /*
             * 권한 획득 완료
             * 해야 할 일을 수행한다.
             */
                Log.d("RESULT", "권한 획득 완료");
                if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    //finishFlag=true;

                    permission_editor.putBoolean("permission_location",true);
                    //hd.postDelayed(new splashhandler() , 5000); // 5초 후에 hd Handler 실행
                    finish();
                }
            }
            else
            {
            /*
             * 권한 획득 실패
             * 대안을 찾거나 기능의 수행을 중지한다.
             */
                Log.d("RESULT", "권한 획득 실패");
                //Toast.makeText(getApplicationContext(),"동의해주셔야 합니다",Toast.LENGTH_LONG).show();


                permission_editor.putBoolean("permission_location",false);
                //MainActivity.mainActivity.showDailog();
                finish();
            }
        }
    }
}
