package com.parking.kani.parking.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.kani.parking.R;
import com.parking.kani.parking.activity.MainActivity;
import com.parking.kani.parking.activity.NaviActivity;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.PoiRequestManager;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.utility.PoiListAdapter;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class TargetSearchFragment extends BaseFragment
{
    private OnFragmentInteractionListener mListener;

    private EditText search_text;
    private Button search_btn;

    private TextView search_value;
    private TextView search_count;

    private ListView search_listview;
    private ArrayList<TMapPOIItem> poi_list=null;
    private PoiListAdapter adapter;

    public TargetSearchFragment()
    {
        // Required empty public constructor
    }

    public static TargetSearchFragment newInstance()
    {
        TargetSearchFragment fragment = new TargetSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view=inflater.inflate(R.layout.fragment_target_search, container, false);

        search_text=(EditText)view.findViewById(R.id.search_text);
        search_btn=(Button)view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String value=search_text.getText().toString().trim();
                //Log.d("검색 할 값..",value);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);

                if(value.length()>10)
                    value=value.substring(0,10)+"...";

                search_value.setText("\'"+value+"\'");

                TMapView mapView=new TMapView(getActivity());
                mapView.setSKPMapApiKey(t_map_key);
                //아 이거 안하면 그지 같음

                PoiRequestManager requestManager=new PoiRequestManager(getActivity());
                requestManager.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        ArrayList<TMapPOIItem> list=(ArrayList<TMapPOIItem>)object;
                        if(list==null)
                        {
                            Toast.makeText(getActivity(),"검색 결과가 없습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //poi_list=list;
                        poi_list.clear();
                        for(int i=0; i<list.size(); i++)
                        {
                            poi_list.add(list.get(i));
                            //Log.d(poi_list.get(i).getPOIName(),poi_list.get(i).getPOIAddress().replace("null",""));
                        }

                        adapter.notifyDataSetChanged();

                        search_count.setText(Integer.toString(poi_list.size())+" 건");
                    }

                    @Override
                    public void failTask()
                    {
                        Log.d("서버","실패");
                    }
                });
                requestManager.execute(value);

            }
        });
        search_value=(TextView)view.findViewById(R.id.search_value);
        search_count=(TextView)view.findViewById(R.id.search_count);
        search_listview=(ListView)view.findViewById(R.id.search_listView);

        poi_list=new ArrayList<>();

        adapter=new PoiListAdapter(getActivity(),R.layout.poi_list_item,poi_list);
        search_listview.setAdapter(adapter);
        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TMapPoint point=poi_list.get(i).getPOIPoint();

                String start_position=point.getLatitude()+"-"+point.getLongitude();
                getActivity().getIntent().putExtra("start_position",start_position);

                Log.d("start_pos",start_position);
                Log.d("start_pos2",poi_list.get(i).getPOIAddress());

                ServerConnection conn=new ServerConnection();
                conn.setDialog(getActivity());
                conn.setAsynctaskFinishListener(new AsynctaskFinishListener()
                {
                    @Override
                    public void succeedTask(Object object)
                    {
                        String data=(String)object;

                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.fragment_place, TargetPositionFragment.newInstance(data));
                        fragmentTransaction.addToBackStack("target_position");
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void failTask()
                    {
                        Toast.makeText(getActivity(),"SERVER 데이터 받기 실패",Toast.LENGTH_SHORT).show();
                    }
                });
                SharedPreferences setting=getActivity().getSharedPreferences("setting", 0);
                String query_str=MainActivity.makeQueryString(setting);
                String document="/search_parking.php";

                conn.execute(document+query_str+"&latitude="+point.getLatitude()+"&longitude="+point.getLongitude());
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
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
