package com.parking.kani.parking.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ConvertGpsToAddressAsync;
import com.parking.kani.parking.connection.ImageDownTask;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.utility.ParkItem;
import com.parking.kani.parking.utility.ParkListAdapter;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;


public class TargetPositionFragment extends BaseFragment
{

    private OnFragmentInteractionListener mListener;

    private LinearLayout tmap_layout;
    private TMapView map_view;

    private TextView addr;

    private ArrayList<ParkItem> park_list=null;
    private ListView listView=null;
    private ParkListAdapter adapter=null;

    private ParkItem selected_item = null;

    LinearLayout tmap_wrap_layout, no_listView_layout, listView_layout;

    public TargetPositionFragment()
    {
        // Required empty public constructor
    }

    public static TargetPositionFragment newInstance(String json_data)
    {
        TargetPositionFragment fragment = new TargetPositionFragment();
        Bundle args = new Bundle();
        args.putString("json_data",json_data);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view=inflater.inflate(R.layout.fragment_target_position, container, false);

        tmap_wrap_layout = (LinearLayout) view.findViewById(R.id.tmap_wrap_layout);
        no_listView_layout = (LinearLayout) view.findViewById(R.id.no_listView_layout);
        listView_layout = (LinearLayout) view.findViewById(R.id.listView_layout);

        String str=getArguments().getString("json_data");

        SharedPreferences setting = getActivity().getSharedPreferences("setting", 0);
        boolean api_flag = false;
        int search_type = setting.getInt("search",0);
        if(search_type == 3)
            api_flag = true;

        addr=(TextView)view.findViewById(R.id.addr_text);

        if( str.trim().equals("결과 집합 만들기 실패") )
            park_list = new ArrayList<ParkItem> ();
        else
        {
            park_list=parseParkList(str,true,api_flag);

            for(ParkItem item : park_list)
            {
                String park_images[] = item.getImage_urls();
                ImageDownTask imageDownTask = new ImageDownTask();
                imageDownTask.item = item;
                imageDownTask.setDialog(getActivity());
                imageDownTask.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        //Log.d("TAG",object.toString());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failTask()
                    {
                        Log.d("TAG","fail");
                    }
                });
                imageDownTask.execute(park_images);
            }
        }

//        tmap_layout=(LinearLayout)view.findViewById(R.id.tmap_layout);

        String start_position=getActivity().getIntent().getExtras().getString("start_position");
        double start_latitude=Double.parseDouble(start_position.split("-")[0]);
        double start_longitude=Double.parseDouble(start_position.split("-")[1]);

        Log.d("target_pos",start_position);

        map_view = new TMapView(getActivity());
        map_view.setLocationPoint(start_latitude, start_longitude);//임시 서울역 test 용
        map_view.setSKPMapApiKey(t_map_key);
        map_view.setCenterPoint(start_longitude, start_latitude, true);
//        map_view.setZoomLevel(13);

        TMapPoint centerPoint = new TMapPoint(start_latitude, start_longitude);
        TMapData tmapdata = new TMapData();

        tmapdata.convertGpsToAddress(centerPoint.getLatitude(), centerPoint.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback()
        {
            @Override
            public void onConvertToGPSToAddress(String strAddress)
            {
                ConvertGpsToAddressAsync convertGpsToAddressAsync = new ConvertGpsToAddressAsync(addr);
                convertGpsToAddressAsync.execute(strAddress);
            }
        });

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_black_location);
        //map_view.setIcon(bitmap);

        // 검색결과가 있을경우...
        if( park_list.size() > 0 )
        {
            tmap_wrap_layout.setVisibility(View.VISIBLE);
            no_listView_layout.setVisibility(View.GONE);
            listView_layout.setVisibility(View.VISIBLE);

            tmap_layout = (LinearLayout) view.findViewById(R.id.tmap_layout);

            tmap_layout.addView(map_view);

            BitmapFactory.Options tempOptions = new BitmapFactory.Options();
            tempOptions.inSampleSize = 4;
            Bitmap tempIcon = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin, tempOptions);
            TMapMarkerItem tempItem = new TMapMarkerItem();

            tempItem.setIcon(tempIcon);
            tempItem.setTMapPoint(new TMapPoint(start_latitude, start_longitude));
            tempItem.setCalloutTitle("목적지");
            tempItem.setCanShowCallout(true);
            tempItem.setVisible(tempItem.VISIBLE);

            map_view.addMarkerItem(addr.getText().toString(), tempItem);

            for (int i = 0; i < park_list.size(); ++i)
            {
                double latitude = park_list.get(i).getLatitude();
                double longitude = park_list.get(i).getLongitude();
                TMapPoint point = new TMapPoint(latitude, longitude);
                TMapMarkerItem item = new TMapMarkerItem();

                String strID = (String) park_list.get(i).getAddr();

                item.setID(strID);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red, options);

                item.setIcon(icon);

                item.setTMapPoint(point);
                item.setCalloutTitle(park_list.get(i).getName());
                item.setCanShowCallout(true);
                item.setVisible(item.VISIBLE);


                map_view.addMarkerItem(strID, item);
            }

            listView = (ListView) view.findViewById(R.id.listView);
            adapter = new ParkListAdapter(getActivity(), R.layout.park_list_item, park_list);

            //헤더 등록
            //        tmap_layout_view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700));
            //        listView.addHeaderView(tmap_layout_view, null, false);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                //ListView의 아이템 중 하나가 클릭될 때 호출되는 메소드
                //첫번째 파라미터: 클릭된 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)
                //두번째 파라미터: 클릭된 아이템 뷰
                //세번째 파라미터: 클릭된 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3...)
                //네번째 파라미터: 클릭된 아이템의 아이디(특별한 설정이 없다면 세번째 파라미터인 position과 같은 값)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.d("click", Integer.toString(position));

                    selected_item = park_list.get(position);

                    ServerConnection conn = new ServerConnection();
                    conn.setDialog(getActivity());
                    conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                    {
                        @Override
                        public void succeedTask(Object object)
                        {
                            String string = (String) object;

                            FragmentManager fragmentManager = getActivity().getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.fragment_place, ParkingDetailFragment.newInstance(selected_item, string));
                            fragmentTransaction.addToBackStack("park_detail");
                            fragmentTransaction.commit();

                            selected_item = null;
                        }

                        @Override
                        public void failTask()
                        {
                            Log.d("SERVER_SCORE", "실패");
                        }
                    });
                    SharedPreferences login = getActivity().getSharedPreferences("login", 0);

                    String document = "/search_parking_score.php";
                    conn.execute(document + "?member_id=" + login.getString("member_id", "") + "&park_no=" + selected_item.getPark_no());
                }
            });
        }
        else
        {
            tmap_wrap_layout.setVisibility(View.GONE);
            no_listView_layout.setVisibility(View.VISIBLE);
            listView_layout.setVisibility(View.GONE);
        }

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
}
