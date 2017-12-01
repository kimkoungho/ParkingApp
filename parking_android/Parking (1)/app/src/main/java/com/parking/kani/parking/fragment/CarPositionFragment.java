package com.parking.kani.parking.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.GPSManager;
import com.parking.kani.parking.dialog.BaseDialog;
import com.parking.kani.parking.dialog.ImageSelectDialog;
import com.parking.kani.parking.dialog.RegistImageDialog;
import com.parking.kani.parking.dialog.TimerDialog;
import com.parking.kani.parking.utility.Base64Utility;
import com.parking.kani.parking.utility.PermissionRequester;
import com.parking.kani.parking.utility.TimeNotiService;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class CarPositionFragment extends BaseFragment
{
    private OnFragmentInteractionListener mListener;

    LinearLayout not_set_layout, set_layout, set_button_layout, content_layout, tmap_layout, set_time_layout;
    TMapView map_view;

    TextView addr_text, parking_time;

    ImageView image_layout;
    ImageButton show_map_btn;
    Button position_regist_btn, position_release_btn;

    RegistImageDialog registImageDialog;
    ImageSelectDialog imageSelectDialog;
    TimerDialog timerDialog;

    boolean isTMapOn = false;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri mImageCaptureUri;
    private String absolutePath;

    Base64Utility base64Utility = new Base64Utility();
    String imageFile = null;

    private SharedPreferences my_car_pos;
    private SharedPreferences.Editor my_car_pos_editor;

    private SharedPreferences permission = null;
    private SharedPreferences.Editor permission_editor = null;


    /********
     * Service 필요 부분
     **************/

    private Intent intent;
    private boolean running = true;
    private Thread countThread;
    private SharedPreferences timer;
    private SharedPreferences.Editor editor;
    private long startTime;
    private long endTime;
    private long notiTime;

    private class GetCountThread implements Runnable
    {
        private Handler handler = new Handler();

        @Override
        public void run()
        {

            while (running)
            {
                handler.post(new Runnable()
                {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    long restTime = (endTime - currentTime) / 1000;

                    @Override
                    public void run()
                    {
                        try
                        {
                            if (restTime > 0)
                                parking_time.setText("남은 주차시간 " + String.format("%02d시간 %02d분", restTime / 3600, (restTime % 3600) / 60));
                            else
                            {
                                parking_time.setText("등록된 주차시간이 없거나 만료되었어요.");

                                editor.putLong("startTime", 0);
                                editor.putLong("endTime", 0);
                                editor.putLong("notiTime", 0);
                                editor.commit();

                                running = false;
                                return;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                // 0.5초 텀을 준다.
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**********************************/


    public CarPositionFragment()
    {
        // Required empty public constructor
    }

    public static CarPositionFragment newInstance(String param1, String param2)
    {
        CarPositionFragment fragment = new CarPositionFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {

        }

        timer = getActivity().getSharedPreferences("timer", 0);
        editor = timer.edit();

        if (timer != null)
        {
            startTime = timer.getLong("startTime", 0);
            endTime = timer.getLong("endTime", 0);
            notiTime = timer.getLong("notiTime", 0);
        }
        else
        {
            startTime = 0;
            endTime = 0;
            notiTime = 0;
        }

        Log.d("START_TIME@@@@@", startTime + "##");
        Log.d("END_TIME@@@@@", endTime + "##");
        Log.d("NOTI_TIME@@@@@", notiTime + "##");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_car_position, container, false);

        addr_text = (TextView) view.findViewById(R.id.addr_text);
        parking_time = (TextView) view.findViewById(R.id.parking_time);

        //주차 위치가 없을 때 주차위치 등록하는 layout
        not_set_layout = (LinearLayout) view.findViewById(R.id.not_set_layout);

        //주차 위치가 있을 때 정보를 확인 하는 layout
        set_layout = (LinearLayout) view.findViewById(R.id.set_layout);
        set_button_layout = (LinearLayout) view.findViewById(R.id.set_button_layout);
        content_layout = (LinearLayout) view.findViewById(R.id.content_layout);
        tmap_layout = (LinearLayout) view.findViewById(R.id.tmap_layout);
        set_time_layout = (LinearLayout) view.findViewById(R.id.set_time_layout);

        image_layout = (ImageView) view.findViewById(R.id.image_layout);

        show_map_btn = (ImageButton) view.findViewById(R.id.show_map_btn);

        position_regist_btn = (Button) view.findViewById(R.id.position_regist_btn);
        position_release_btn = (Button) view.findViewById(R.id.position_release_btn);

        my_car_pos = getActivity().getSharedPreferences("MyCarPos", 0);
        my_car_pos_editor = my_car_pos.edit();

        permission = getActivity().getSharedPreferences("permissionSetting", 0);
        permission_editor = permission.edit();

        countThread = new Thread(new GetCountThread());

        if (startTime > 0)
            countThread.start();

        final String my_car_pos_str = my_car_pos.getString("my_car_pos", "");
        if (my_car_pos_str != "")
        {
            not_set_layout.setVisibility(View.GONE);
            set_layout.setVisibility(View.VISIBLE);
            set_button_layout.setVisibility(View.VISIBLE);
            tmap_layout.setVisibility(View.GONE);
            image_layout.setVisibility(View.VISIBLE);
            content_layout.setBackground(null);

            // TMap 객체생성
            map_view = new TMapView(getActivity());

            tmap_layout.addView(map_view);
            map_view.setSKPMapApiKey(t_map_key);

//            double latitude = Double.parseDouble(my_car_pos_str.split("-")[0]);
//            double longitude = Double.parseDouble(my_car_pos_str.split("-")[1]);

            addr_text.setText(my_car_pos.getString("my_car_addr", "주차위치 없음"));

            String base64_img_str = my_car_pos.getString("my_pos_img", "");
            Bitmap my_pos_img = base64Utility.decode(base64_img_str);
            image_layout.setImageBitmap(my_pos_img);
        }

        show_map_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if( tmap_layout.getVisibility() == View.GONE )
                {
                    // TMap 객체가 생성되어있는지 분기
                    if (!isTMapOn)
                    {
                        String my_car_pos_str = my_car_pos.getString("my_car_pos","");

                        if(my_car_pos_str == null)
                        {
                            Toast.makeText(getActivity(),"등록된 주차위치가 없습니다",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        GPSManager gpsManager = new GPSManager(getActivity());
                        gpsManager.setAsynctaskFinishListener(new AsynctaskFinishListener()
                        {
                            @Override
                            public void succeedTask(Object object)
                            {
                                image_layout.setVisibility(View.GONE);
                                tmap_layout.setVisibility(View.VISIBLE);
                                content_layout.setBackgroundResource(R.drawable.border1);

                                String current_str =(String)object;
                                Log.d("TASK",current_str);

                                String my_car_pos_str = my_car_pos.getString("my_car_pos","");
                                //my_car_pos_str = "37.554521"+"-"+"126.9684543";

                                Log.d("my_car_pos",my_car_pos_str);
                                double my_car_latitude = Double.parseDouble((my_car_pos_str.split("-")[0]).trim());
                                double my_car_longitude = Double.parseDouble((my_car_pos_str.split("-")[1]).trim());
                                TMapPoint my_car_point = new TMapPoint(my_car_latitude,my_car_longitude);

                                double latitude = Double.parseDouble(current_str.split("-")[0]);
                                double longitude = Double.parseDouble(current_str.split("-")[1]);
                                TMapPoint current = new TMapPoint(latitude,longitude);

                                Toast.makeText(getActivity(), "TMap 객체 생성", Toast.LENGTH_SHORT).show();
                                isTMapOn = true;

                                map_view.setLocationPoint(latitude,longitude);
                                //map_view.setZoomLevel(15);
                                map_view.setCenterPoint(current.getLongitude(), current.getLatitude(), true);

                                TMapData tmapdata = new TMapData();
                                tmapdata.findPathDataWithType(TMapData.TMapPathType.BICYCLE_PATH, current, my_car_point, new TMapData.FindPathDataListenerCallback()
                                {
                                    @Override
                                    public void onFindPathData(TMapPolyLine polyLine)
                                    {
                                        polyLine.setLineColor(Color.BLUE);
                                        map_view.addTMapPath(polyLine);

//                                        BitmapFactory.Options tempOptions = new BitmapFactory.Options();
//                                        tempOptions.inSampleSize = 6;
//                                        Bitmap tempIcon = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin, tempOptions);
//                                        TMapMarkerItem tempItem1 = new TMapMarkerItem();
//
//                                        tempItem1.setIcon(tempIcon);
//                                        tempItem1.setTMapPoint(new TMapPoint(current.getLatitude(), current.getLongitude()));
//                                        tempItem1.setCalloutTitle("현재 위치");
//                                        tempItem1.setCanShowCallout(true);
//                                        tempItem1.setVisible(tempItem1.VISIBLE);
//
//                                        map_view.addMarkerItem("현재 위치", tempItem1);
//
//                                        TMapMarkerItem tempItem2 = new TMapMarkerItem();
//                                        tempIcon = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red, tempOptions);
//
//                                        tempItem2.setIcon(tempIcon);
//                                        tempItem2.setTMapPoint(new TMapPoint(my_car_point.getLatitude(), my_car_point.getLongitude()));
//                                        tempItem2.setCalloutTitle("주차 위치");
//                                        tempItem2.setCanShowCallout(true);
//                                        tempItem2.setVisible(tempItem2.VISIBLE);
//
//                                        map_view.addMarkerItem("주차 위치", tempItem2);
                                    }
                                });
                            }

                            @Override
                            public void failTask()
                            {
                                Toast.makeText(getActivity(),"위치 잡기 실패",Toast.LENGTH_SHORT).show();
                            }
                        });
                        gpsManager.execute();

                        //map_view.setLocationPoint(37.554521, 126.9684543);//임시 서울역 test 용
                        //map_view.setZoomLevel(13);
                    }
                }
                else
                {
                    tmap_layout.setVisibility(View.GONE);
                    image_layout.setVisibility(View.VISIBLE);
                    content_layout.setBackground(null);
                }
            }
        });

        position_regist_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "주차 위치를 등록합니다.", Toast.LENGTH_SHORT).show();

                // GPS에서 현재 위치를 가져온 후...
                GPSManager gpsManager = new GPSManager(getActivity());
                gpsManager.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        String string = (String) object;
                        Log.d("TASK", string);

                        double latitude = Double.parseDouble(string.split("-")[0]);
                        double longitude = Double.parseDouble(string.split("-")[1]);

                        TMapData tmapdata = new TMapData();
                        tmapdata.convertGpsToAddress(latitude, longitude, new TMapData.ConvertGPSToAddressListenerCallback()
                        {
                            @Override
                            public void onConvertToGPSToAddress(String strAddress)
                            {
                                my_car_pos_editor.putString("my_car_addr", strAddress);
                                my_car_pos_editor.commit();
                                //Log.d("111111",strAddress);
                                addr_text.setText(strAddress);
                            }
                        });

                        //string = "37.554521"+"-"+"126.9684543";//임시 서울역
                        my_car_pos_editor.putString("my_car_pos", string);
                        my_car_pos_editor.commit();

                        timerDialog.show();
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(getActivity(), "위치 잡기 실패", Toast.LENGTH_SHORT).show();
                    }
                });
                gpsManager.execute();

                /********************************************************/
                timerDialog = new TimerDialog(getActivity(), R.style.Theme_Dialog);
                timerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                timerDialog.setDialogSetting();

                WindowManager.LayoutParams params = timerDialog.getWindow().getAttributes();

                params.width = 1000;
                params.height = 870;
                timerDialog.getWindow().setAttributes(params);

                timerDialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)
                    {
                        HashMap<String, Object> map = (HashMap<String, Object>) obj;

                        if (((String) map.get("header")).equals("setting"))
                        {
                            Toast.makeText(getActivity(), "알림이 등록되었어요.", Toast.LENGTH_SHORT).show();

                            intent = new Intent(getActivity(), TimeNotiService.class);

                            int maxCount = (int) map.get("parking_hour") * 3600 + (int) map.get("parking_minute") * 60;
                            int notiCount = (int) map.get("noti_hour") * 3600 + (int) map.get("noti_minute") * 60;

                            startTime = Calendar.getInstance().getTimeInMillis();
                            endTime = startTime + (maxCount * 1000);
                            notiTime = startTime + (notiCount * 1000);

                            editor.putLong("startTime", startTime);
                            editor.putLong("endTime", endTime);
                            editor.putLong("notiTime", notiTime);
                            editor.commit();

                            if (!getActivity().stopService(intent))
                                getActivity().startService(intent);

                            running = true;

                            if (countThread == null || !countThread.isAlive())
                            {
                                Log.d("TIMER_START", "Timer Thread Start!!");
                                countThread = new Thread(new GetCountThread());
                                countThread.start();
                            }
                        }
                        else if (((String) map.get("header")).equals("cancel"))
                        {
                            Toast.makeText(getActivity(), "알림을 등록하지 않아요.", Toast.LENGTH_SHORT).show();
                        }

                        timerDialog.dismiss();
                        registImageDialog.show();
                    }
                });
                /********************************************************/


                registImageDialog = new RegistImageDialog(getActivity(), R.style.Theme_Dialog);
                registImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                registImageDialog.setDialogSetting();

                params = registImageDialog.getWindow().getAttributes();

                params.width = 1000;
                params.height = 750;
                registImageDialog.getWindow().setAttributes(params);

                registImageDialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                {
                    @Override
                    public void updateComponent(Object obj)
                    {
                        String str = (String) obj;
                        // 이미지 등록 다이얼로그에서 'Yes' 를 눌렀을 경우
                        if (str.equals("yes"))
                        {
                            //권한 얻기
                            boolean finishFlag = permission.getBoolean("permission_file", false);

                            if (!finishFlag)
                            {
                                int permissionReadStorage = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                                int permissionWriteStorage = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED)
                                {
                                    setPermission();
                                }
                                else
                                {
                                    finishFlag = true;
                                }
                            }


                            imageSelectDialog = new ImageSelectDialog(getActivity(), R.style.Theme_Dialog);
                            imageSelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageSelectDialog.setDialogSetting();

                            WindowManager.LayoutParams params = imageSelectDialog.getWindow().getAttributes();

                            params.width = 800;
                            params.height = 780;
                            imageSelectDialog.getWindow().setAttributes(params);

                            imageSelectDialog.show();

                            imageSelectDialog.setUpdateComponentListener(new BaseDialog.UpdateComponentListener()
                            {
                                @Override
                                public void updateComponent(Object obj)
                                {
                                    String str = (String) obj;
                                    // 카메라에서 찍은 이미지 등록
                                    if (str.equals("CAMERA"))
                                    {
                                        doTakePhotoAction();
                                    }
                                    // 갤러리에서 가져와서 이미지 등록
                                    else if (str.equals("GALLERY"))
                                    {
                                        doTakeAlbumAction();
                                    }
                                    // 취소버튼 클릭시 기본 이미지 등록
                                    else if (str.equals("CANCEL"))
                                    {
                                        not_set_layout.setVisibility(View.GONE);
                                        set_layout.setVisibility(View.VISIBLE);
                                        set_button_layout.setVisibility(View.VISIBLE);
                                        tmap_layout.setVisibility(View.GONE);
                                        image_layout.setVisibility(View.VISIBLE);
                                        content_layout.setBackground(null);
                                        imageFile = null;
                                    }

                                    imageSelectDialog.dismiss();
                                }
                            });
                        }
                        else
                        {
                            not_set_layout.setVisibility(View.GONE);
                            set_layout.setVisibility(View.VISIBLE);
                            set_button_layout.setVisibility(View.VISIBLE);
                            tmap_layout.setVisibility(View.GONE);
                            image_layout.setVisibility(View.VISIBLE);
                            content_layout.setBackground(null);
                        }

                        registImageDialog.dismiss();
                    }
                });
            }
        });

        position_release_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "주차 위치를 해제합니다.", Toast.LENGTH_SHORT).show();

                my_car_pos_editor.remove("my_car_pos");
                my_car_pos_editor.remove("my_pos_addr");
                my_car_pos_editor.remove("my_pos_img");
                my_car_pos_editor.commit();

                not_set_layout.setVisibility(View.VISIBLE);
                set_layout.setVisibility(View.GONE);
                set_button_layout.setVisibility(View.GONE);

                if (isTMapOn)
                {
                    //tmap_layout.removeView(map_view);
                    //map_view = null;
                    isTMapOn = false;
                }

                image_layout.setImageBitmap(null);
                image_layout.setBackgroundResource(R.drawable.icon_cloud);
            }
        });

        return view;
    }


    // 카메라 촬영 후 이미지 가져오기
    public void doTakePhotoAction()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode)
        {
            case PICK_FROM_ALBUM:
                mImageCaptureUri = data.getData();
            case PICK_FROM_CAMERA:
            {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);

                // Intent 처리후 set_layout 으로 전환
                not_set_layout.setVisibility(View.GONE);
                set_layout.setVisibility(View.VISIBLE);
                set_button_layout.setVisibility(View.VISIBLE);
                tmap_layout.setVisibility(View.GONE);
                image_layout.setVisibility(View.VISIBLE);
                content_layout.setBackground(null);
                break;
            }
            case CROP_FROM_IMAGE:
            {
                if (resultCode != RESULT_OK)
                    return;

                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Parking/" + System.currentTimeMillis() + ".jpg";

                if (extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    image_layout.setImageBitmap(photo);

                    storeCropImage(photo, filePath);
                    absolutePath = filePath;

                    //저장
                    Log.d("path", absolutePath);
                    break;
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());

                if (f.exists())
                    f.delete();

            }
        }
    }

    // Bitmap을 저장하는 부분
    private void storeCropImage(Bitmap bitmap, String filePath)
    {
        // Parking 폴더를 생성하여 이미지를 저장하는 방식
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Parking/";

        File directory_Parking = new File(dirPath);

        // Parking 디렉터리 존재여부 확인
        if (!directory_Parking.exists())
            directory_Parking.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try
        {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // sendBroadcast를 통해 CROP된 사진을 앨범에 보이도록 갱신
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();

            // 등록한 이미지를 base64로 인코딩
            imageFile = base64Utility.encodeJPEG(copyFile.getPath());

            Log.d("image_file", imageFile);

            my_car_pos_editor.putString("my_pos_img", imageFile);
            my_car_pos_editor.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
//        if (mListener != null)
//        {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setPermission()
    {
        int result = new PermissionRequester.Builder(getActivity())
                .setTitle("권한 요청")
                .setMessage("권한을 요청합니다.")
                .setPositiveButtonName("네")
                .setNegativeButtonName("아니요.")
                .create()
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1000, new PermissionRequester.OnClickDenyButtonListener()
                {
                    @Override
                    public void onClick(Activity activity)
                    {
                        Log.d("RESULT", "취소함.");
                        Toast.makeText(getActivity(), "권한을 허용해주셔야 합니다", Toast.LENGTH_LONG).show();
                        //MainActivity.mainActivity.showDailog();
                    }
                });

        if (result == PermissionRequester.ALREADY_GRANTED)
        {
            Log.d("RESULT", "권한이 이미 존재함.");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getActivity(), "허용", Toast.LENGTH_LONG).show();

        }
        else if (result == PermissionRequester.NOT_SUPPORT_VERSION)
            Log.d("RESULT", "마쉬멜로우 이상 버젼 아님.");
        else if (result == PermissionRequester.REQUEST_PERMISSION)
            Log.d("RESULT", "요청함. 응답을 기다림.");

    }

    /**
     * 신규로 권한을 요청해 그 응답이 돌아왔을 경우 실행됨.
     *
     * @param requestCode  : 권한 요청시 전송했던 코드.
     * @param permissions  : 요청한 권한
     * @param grantResults : 해당 권한에 대한 결과
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        if (requestCode == 1000)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
            /*
             * 권한 획득 완료
             * 해야 할 일을 수행한다.
             */
                Log.d("RESULT", "권한 획득 완료");
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    //finishFlag=true;

                    permission_editor.putBoolean("permission_file", true);
                    //hd.postDelayed(new splashhandler() , 5000); // 5초 후에 hd Handler 실행
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


                permission_editor.putBoolean("permission_file", false);
                //MainActivity.mainActivity.showDailog();
            }
        }
    }
}