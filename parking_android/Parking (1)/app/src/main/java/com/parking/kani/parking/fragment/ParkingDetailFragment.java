package com.parking.kani.parking.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.dialog.ExternalAppAdapter;
import com.parking.kani.parking.utility.CustomPagerAdapter;
import com.parking.kani.parking.utility.DBHelper;
import com.parking.kani.parking.utility.ParkItem;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParkingDetailFragment extends BaseFragment
{

    private ParkItem item=null;

    private PackageManager navi_manager;
    private ArrayList<HashMap<String, Object>> ext_app_list;

    private TextView park_name_txt, park_addr_txt, park_hour_cost,park_day_cost,park_base_time_cost,park_month_cost,
            park_add_time_cost, weekday_time,weekend_time,holiday_time,available_coverage,available_car_num;


    private TMapView mapView;

    private ViewPager park_img_changer;
    private LinearLayout park_img_pagination;
    private int currentImageIndex = 0;

    private SimpleRatingBar park_star;

    private Button navi_btn, favorite_btn, view_map_btn, score_btn, score_set_btn, park_tel_btn;
    private LinearLayout image_layout, tmap_route_layout, set_score_layout;
    private FlowLayout park_theme;


    private boolean isTMapOn = false;

    private SharedPreferences login;

    // 내가 해당 주차장에 준 점수, 총 점수, 점수준 인원수
    private double default_score = 0.0;
    private double total_score = 0.0;
    private int vote_num = 0;//평점을 준 사람의 수

    private ArrayList<String> themeList;


    public ParkingDetailFragment()
    {
        // Required empty public constructor
    }


    public static ParkingDetailFragment newInstance(ParkItem item, String score_data)
    {
        ParkingDetailFragment fragment = new ParkingDetailFragment();
        Bundle args = new Bundle();

        args.putSerializable("park_item",(Serializable)item);
        args.putString("score_data",score_data);

//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            item=(ParkItem)getArguments().getSerializable("park_item");
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            //Log.d("111",item.getAddr());


//            Log.d("@@getPark_no@@", item.getPark_no() + "");
//            Log.d("@@getName@@", item.getName() + "");
//            Log.d("@@getAddr@@", item.getAddr() + "");
//            Log.d("@@getType1@@", item.getType1() + "");
//            Log.d("@@getType2@@", item.getType2() + "");
//            Log.d("@@getTel@@", item.getTel() + "");
//            Log.d("@@getSpace@@", item.getSpace() + "");
//            Log.d("@@getCost_type@@", item.getCost_type() + "");
//            Log.d("@@getWeekday_start@@", item.getWeekday_start() + "");
//            Log.d("@@getWeekday_end@@", item.getWeekday_end() + "");
//            Log.d("@@getHoliday_start@@", item.getHoliday_start() + "");
//            Log.d("@@getHoliday_end@@", item.getHoliday_end() + "");
//            Log.d("@@getWeekend_cost_tp@@", item.getWeekend_cost_tp() + "");
//            Log.d("@@getHoliday_cost_tp@@", item.getHoliday_cost_tp() + "");
//            Log.d("@@getMonth_cost@@", item.getMonth_cost() + "");
//            Log.d("@@getLatitude@@", item.getLatitude() + "");
//            Log.d("@@getLongitude@@", item.getLongitude() + "");
//            Log.d("@@getBase_cost@@", item.getBase_cost() + "");
//            Log.d("@@getBase_time@@", item.getBase_time() + "");
//            Log.d("@@getAdd_cost@@", item.getAdd_cost() + "");
//            Log.d("@@getAdd_time@@", item.getAdd_time() + "");
//            Log.d("@@getDay_cost@@", item.getDay_cost() + "");
//            Log.d("@@getHour_cost@@", item.getHour_cost() + "");
//            Log.d("@@getDistance@@", item.getDistance() + "");
//            Log.d("@@getRank@@", item.getRank() + "");
//            Log.d("@@getCurrent_park_cnt@@", item.getCurrent_park_cnt() + "");

            themeList = new ArrayList<String> ();

            //로그인 정보 추출
            login=getActivity().getSharedPreferences("login",0);

            String score_date = getArguments().getString("score_data");
            try
            {
                JSONObject jsonObject = new JSONObject(score_date);

                default_score = jsonObject.getDouble("default_score");
                total_score = jsonObject.getDouble("total_score");
                vote_num = jsonObject.getInt("vote_num");

                if( total_score >= 4 )
                    themeList.add("평점 높음");

            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

            if( item.getType1() != null && !item.getType1().trim().equals("") )
                themeList.add(item.getType1().trim());

            if( item.getType2() != null && !item.getType2().trim().equals("") )
                themeList.add(item.getType2().trim());

            if( item.getSpace() > 50 )
                themeList.add("넓음");

            if( item.getCost_type() != null && !item.getCost_type().trim().equals("유료") )
                themeList.add(item.getCost_type().trim());

            int timeCost = 0;

            try
            {
                if (Integer.parseInt(item.getBase_time()) == 60)
                    timeCost = item.getBase_cost();
                else
                    timeCost = (60 / Integer.parseInt(item.getBase_time())) * item.getBase_cost();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            // 1시간 당 5000원을 기준으로 저렴하고 비싼것을 구분
            if( timeCost > 0 &&  timeCost <= 5000 )
                themeList.add("저렴");
            else
                themeList.add("비쌈");

            if( item.getCost_type() != null && !item.getCost_type().trim().equals("유료") )
                themeList.add(item.getCost_type().trim());

            if( item.getWeekend_cost_tp() != null && !item.getWeekend_cost_tp().trim().equals("유료") )
                themeList.add("주말 " + item.getWeekend_cost_tp().trim());

            if( item.getHoliday_cost_tp() != null && !item.getHoliday_cost_tp().trim().equals("유료") )
                themeList.add("공휴일 " + item.getHoliday_cost_tp().trim());


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view=inflater.inflate(R.layout.fragment_parking_detail, container, false);


        //스와이퍼를 위한 viewpager
        park_img_changer = (ViewPager) view.findViewById(R.id.park_img_changer);

        // 이미지 수에 따른 pagination 을 동적으로 추가
        park_img_pagination = (LinearLayout) view.findViewById(R.id.park_img_pagination);
        image_layout = (LinearLayout) view.findViewById(R.id.image_layout);
        //tmap layout
        tmap_route_layout = (LinearLayout) view.findViewById(R.id.tmap_route_layout);
        //score_layout
        set_score_layout = (LinearLayout) view.findViewById(R.id.set_score_layout);

        park_theme = (FlowLayout) view.findViewById(R.id.park_theme);

        navi_btn=(Button)view.findViewById(R.id.navi_btn);
        view_map_btn = (Button) view.findViewById(R.id.view_map_btn);
        score_btn = (Button) view.findViewById(R.id.score_btn);

        score_set_btn = (Button) view.findViewById(R.id.score_set_btn);
        park_star = (SimpleRatingBar) view.findViewById(R.id.park_star);

        favorite_btn=(Button)view.findViewById(R.id.favorite_btn);
        park_tel_btn=(Button)view.findViewById(R.id.park_tel_btn);//전화걸기 버튼

        park_name_txt=(TextView)view.findViewById(R.id.park_name_txt);
        park_addr_txt=(TextView)view.findViewById(R.id.park_addr_txt);
        park_hour_cost=(TextView)view.findViewById(R.id.park_hour_cost);
        park_day_cost=(TextView)view.findViewById(R.id.park_day_cost);
        park_base_time_cost=(TextView)view.findViewById(R.id.park_base_time_cost);
        park_month_cost=(TextView)view.findViewById(R.id.park_month_cost);
        park_add_time_cost=(TextView)view.findViewById(R.id.park_add_time_cost);
        weekday_time=(TextView)view.findViewById(R.id.weekday_time);
        weekend_time=(TextView)view.findViewById(R.id.weekend_time);
        holiday_time=(TextView)view.findViewById(R.id.holiday_time);
//        available_coverage=(TextView)view.findViewById(R.id.available_coverage);
        available_car_num=(TextView)view.findViewById(R.id.available_car_num);

        //
        // 들어갈 임의의 이미지....서버에서 받은 이미지를 어떻게 넣을지 생각해 봐야함..
//        int imageList[] = {
//                R.drawable.icon_rain,
//                R.drawable.icon_cloud,
//                R.drawable.icon_error,
//                R.drawable.icon_sun,
//                R.drawable.icon_snow
//        };

        // 서버에서 받은 주차장의 테마정보 리스트
//        String themeList[] = {"넓음", "노상", "기계식 주차장" , "주말 무료"};

        Bitmap imageList[] = null;

        ArrayList<byte[]> img_list = item.getPark_imges();
        imageList = new Bitmap[img_list.size()];

        if( img_list.size() == 0 )
        {
            imageList = new Bitmap[1];
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.parking_no_image);
            imageList[0] = drawable.getBitmap();
        }
        else
        {
            for (int i = 0; i < img_list.size(); i++)
            {
                imageList[i] = BitmapFactory.decodeByteArray(img_list.get(i), 0, img_list.get(i).length);
            }
        }


        // = item.getPark_imges();


        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        //ViewPager에 Adapter 설정
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getActivity().getLayoutInflater(), imageList);

        park_img_changer.setAdapter(customPagerAdapter);

        park_img_changer.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                park_img_pagination.getChildAt(currentImageIndex).setBackgroundResource(R.drawable.icon_pagi_off);
                park_img_pagination.getChildAt(position).setBackgroundResource(R.drawable.icon_pagi_on);
                currentImageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });



        // 동적 ImageView에 지정할 (layout_width, layout_height, layout_weight)을 세팅
        LinearLayout.LayoutParams image_layoutParams = new LinearLayout.LayoutParams(convertDpToPixel(15), convertDpToPixel(15), convertDpToPixel(0));
        image_layoutParams.setMargins(convertDpToPixel(3), convertDpToPixel(3), convertDpToPixel(3), convertDpToPixel(3));

        // image의 수만큼 반복해서 pagination을 출력
        for( int i = 0; i < customPagerAdapter.getCount(); i++ )
        {
            ImageButton imageButton = new ImageButton(getActivity());
            imageButton.setId(i);
            imageButton.setLayoutParams(image_layoutParams);

            // 최초에 생성시 첫 이미지가 선택되어있으므로 분기
            if( i == 0 )
                imageButton.setBackgroundResource(R.drawable.icon_pagi_on);
            else
                imageButton.setBackgroundResource(R.drawable.icon_pagi_off);

            // ImageButton 클릭시 해당 이미지가 보여주기 위한 이벤트 처리
            imageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // 현재 선택되어 있는 페이지의 pagination을 off
                    park_img_pagination.getChildAt(currentImageIndex).setBackgroundResource(R.drawable.icon_pagi_off);

                    // 클릭되 페이지의 pagination을 on
                    //첫번째 파라미터: 설정할 현재 위치
                    //두번째 파라미터: 변경할 때 부드럽게 이동하는가? false면 팍팍 바뀜
                    park_img_changer.setCurrentItem(v.getId(), true);
                    park_img_pagination.getChildAt(v.getId()).setBackgroundResource(R.drawable.icon_pagi_on);
                    currentImageIndex = v.getId();
                }
            });

            // pagination Container에 imageButton 추가
            park_img_pagination.addView(imageButton);
        }

        //길 찾기 버튼 - 네비게이션 어플 연동
        navi_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                navi_manager=getActivity().getPackageManager();//네비를 실행시켜줄 매니저

                ext_app_list=new ArrayList<>();
                //설치된 어플중 네비게이션 찾기
                List<ApplicationInfo> app_list=navi_manager.getInstalledApplications(0);
                for(ApplicationInfo app_info : app_list)
                {
                    String p_name=app_info.packageName;
                    for(int i=0; i<ext_pakage_list.length; ++i)
                    {
                        if(p_name.equals(ext_pakage_list[i]))
                        {
                            Drawable icon=app_info.loadIcon(navi_manager);
                            String name=String.valueOf(app_info.loadLabel(navi_manager));
                            HashMap<String, Object> hash=new HashMap();
                            hash.put("icon",icon);
                            hash.put("name",name);
                            hash.put("package",p_name);
                            ext_app_list.add(hash);
                        }
                    }
                }

                ExternalAppAdapter ext_adapter=new ExternalAppAdapter(getActivity(),R.layout.external_app_list_item,ext_app_list);

                // 뷰 호출
                final View dialog_view = getActivity().getLayoutInflater().inflate(R.layout.listview_dialog, null);
                // 해당 뷰에 리스트뷰 호출
                ListView listview = (ListView)dialog_view.findViewById(R.id.dialog_listview);
                // 리스트뷰에 어뎁터 설정
                listview.setAdapter(ext_adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(navi_manager==null) return;

                        Intent navi_intent=navi_manager.getLaunchIntentForPackage((String)ext_app_list.get(i).get("package"));
                        startActivity(navi_intent);
                    }
                });
                // 다이얼로그 생성
                AlertDialog.Builder listViewDialog = new AlertDialog.Builder(getActivity());
                // 리스트뷰 설정된 레이아웃
                listViewDialog.setView(dialog_view);

                // 타이틀
                listViewDialog.setTitle("네비게이션 선택");
                // 다이얼로그 보기
                listViewDialog.show();
            }
        });

        // 점수 입력
        double round_total_score = ((int) ((total_score + 0.05) * 10)) / 10.0;
        score_btn.setText(round_total_score + " / 5.0");

        // 지도보기 버튼 클릭
        view_map_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(item.getDistance() == -1.0) // 이전 페이지가 즐겨찾기 이면
                {
                    Toast.makeText(getActivity(),"시작 지점이 없어 경로를 표시할 수 없습니다",Toast.LENGTH_SHORT).show();
                    return;
                }
                // 이미지와 지도 중 어느 것이 켜져있는지 분기
                if (image_layout.getVisibility() == View.VISIBLE)
                {
                    // TMap 객체가 생성되어있는지 분기
                    if (!isTMapOn)
                    {
                        mapView = new TMapView(getActivity());

                        String start_position=getActivity().getIntent().getExtras().getString("start_position");
                        double start_latitude=Double.parseDouble(start_position.split("-")[0]);
                        double start_longitude=Double.parseDouble(start_position.split("-")[1]);

                        Log.d("start_position",start_position);

                        mapView.setLocationPoint(start_latitude,start_longitude);
                        mapView.setZoomLevel(13);

                        tmap_route_layout.addView(mapView);
                        mapView.setSKPMapApiKey(t_map_key);

                        TMapPoint center=new TMapPoint(start_latitude, start_longitude);
                        mapView.setCenterPoint(center.getLongitude(), center.getLatitude(), true);

                        TMapPoint point2 = new TMapPoint(item.getLatitude(),item.getLongitude());

                        TMapData tmapdata = new TMapData();

                        tmapdata.findPathData(center, point2, new TMapData.FindPathDataListenerCallback()
                        {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine)
                            {
                                mapView.addTMapPath(polyLine);
                            }
                        });

                        // TMap 객체생성
//                        map_view = new TMapView(getActivity());
//                        map_view.setLocationPoint(37.554521, 126.9684543);//임시 서울역 test 용
//                        map_view.setZoomLevel(13);
//
//                        tmap_route_layout.addView(map_view);
//                        map_view.setSKPMapApiKey(t_map_key);

                        Toast.makeText(getActivity(), "TMap 객체 생성", Toast.LENGTH_SHORT).show();
                        isTMapOn = true;
                    }

                    image_layout.setVisibility(View.GONE);
                    tmap_route_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    tmap_route_layout.setVisibility(View.GONE);
                    image_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        score_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(login.getString("member_password","")=="")
                {
                    Toast.makeText(getActivity(),"로그인이 필요한 서비스입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                // score layout 이 열려있는지 분기
                if( set_score_layout.getVisibility() == View.GONE )
                {
                    // 내가 준 점수로 레이팅 초기화
                    park_star.setRating((float) default_score);
                    set_score_layout.setVisibility(View.VISIBLE);
                }
                else
                    set_score_layout.setVisibility(View.GONE);
            }
        });

        score_set_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ServerConnection conn = new ServerConnection();
                conn.setDialog(getActivity());
                conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        //안드로이드 작업
                        // total_score 갱신
                        if(default_score == -1.0)//처음 이면
                            total_score = (total_score * vote_num + park_star.getRating()) / ++vote_num;
                        else
                            total_score = (total_score * vote_num - default_score + park_star.getRating()) / vote_num;

                        Log.d("!!!!!",total_score+"...");
                        double round_total_score = ((int) ((total_score + 0.05) * 10)) / 10.0;

                        score_btn.setText(round_total_score + " / 5.0");

                        // 점수 반영 후 score_layout을 닫음
                        Toast.makeText(getActivity(), "@@@@ " + park_star.getRating() + " @@@@점수가 반영되었습니다.", Toast.LENGTH_SHORT).show();
                        default_score = park_star.getRating();
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(getActivity(),"평점 주기 실패",Toast.LENGTH_SHORT).show();
                    }
                });
                String document = "/insert_recommend.php";
                String query_string = "?member_id="+login.getString("member_id","")+"&park_no="+item.getPark_no()+"&score="+park_star.getRating();
                conn.execute(document+query_string);

                set_score_layout.setVisibility(View.GONE);
            }
        });

        favorite_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DBHelper dbHelper = new DBHelper(getActivity(),"favorite.db",null, 1);
                //dbHelper.deleteAll();
                //dbHelper.drop();

                if(!dbHelper.insert(item.getPark_no()))
                {
                    Toast.makeText(getActivity(), "이미 즐겨찾기에 추가된 주차장입니다.", Toast.LENGTH_SHORT).show();
                }

//                favorite_btn.setBackgroundResource(R.drawable.detail_favorite_no_input_regist);

//                Toast.makeText(getActivity(),dbHelper.select(),Toast.LENGTH_SHORT).show();
            }
        });


//        for( int i = 0; i < themeList.length; i++ )
        for( int i = 0; i < themeList.size(); i++ )
        {
            // 동적 TextView에 지정할 (layout_width, layout_height, layout_weight)을 세팅
            FlowLayout.LayoutParams theme_layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, convertDpToPixel(22));
            theme_layoutParams.setMargins(convertDpToPixel(3), convertDpToPixel(3), convertDpToPixel(3), convertDpToPixel(3));

            TextView textView = new TextView(getActivity());

            textView.setLayoutParams(theme_layoutParams);
            textView.setPadding(convertDpToPixel(2), convertDpToPixel(2), convertDpToPixel(2), convertDpToPixel(2));
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(12);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.WHITE);
            textView.setText(themeList.get(i));
//            textView.setText(themeList[i]);
            textView.setBackgroundResource(R.drawable.round_rect);

            park_theme.addView(textView);
        }

        //전화 걸기 버튼
        park_tel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(park_tel_btn.getText().equals("연락처 없음"))
                    return;

                Intent dial_intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+park_tel_btn.getText()));
                startActivity(dial_intent);
            }
        });

        //값 넣기
        park_name_txt.setText(item.getName());
        park_addr_txt.setText(item.getAddr());
        park_tel_btn.setText(item.getTel());

//        park_base_time_cost.setText(item.getBase_time()+"/"+item.getBase_cost()+" 분/원");
//        park_add_time_cost.setText(item.getAdd_time()+"/"+item.getAdd_cost()+" 분/원");
//        park_hour_cost.setText(item.getHour_cost()+" 원");
//        park_month_cost.setText(item.getMonth_cost()+" 원");
//        park_day_cost.setText(item.getDay_cost()+" 원");

        NumberFormat nf = NumberFormat.getInstance();

        park_base_time_cost.setText(item.getBase_time()+" 분 / "+nf.format(item.getBase_cost())+" 원");
        park_add_time_cost.setText(item.getAdd_time()+" 분 / "+nf.format(item.getAdd_cost())+" 원");
        park_hour_cost.setText(nf.format(item.getHour_cost())+" 원");
        park_month_cost.setText(nf.format(item.getMonth_cost())+" 원");
        park_day_cost.setText(nf.format(item.getDay_cost())+" 원");

        weekday_time.setText(item.getWeekday_start()+" ~ "+item.getWeekday_end());
        //weekend_time.setText(item.getHo);
        holiday_time.setText(item.getHoliday_start()+" ~ "+item.getHoliday_end());

        //weekend_time
        //available_coverage.setText();
        String available_str = item.getSpace()+"대";
        if(item.getCurrent_park_cnt()!=-1)
            available_str = item.getSpace()-item.getCurrent_park_cnt()+"대";
        available_car_num.setText(available_str);

        return view;
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
//        if (context instanceof OnFragmentInteractionListener)
//        {
//            mListener = (OnFragmentInteractionListener) context;
//        }
//        else
//        {
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

    // dp값을 pixel 값으로 변환하는 메소드
    public int convertDpToPixel(int dp)
    {
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel =  (int)(dp * scale + 0.5f);

        return pixel;
    }
}
