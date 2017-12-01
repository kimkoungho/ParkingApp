package com.parking.kani.parking.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parking.kani.parking.R;
import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ImageDownTask;
import com.parking.kani.parking.connection.ServerConnection;
import com.parking.kani.parking.utility.FavoriteListAdapter;
import com.parking.kani.parking.utility.ParkItem;

import java.util.ArrayList;

public class FavoriteFragment extends BaseFragment
{
    private OnFragmentInteractionListener mListener;

    private ArrayList<ParkItem> favorite_list = null;
    private ListView listView = null;
    private FavoriteListAdapter adapter = null;

    private ParkItem selected_item = null;

    LinearLayout no_listView_layout, listView_layout;

    public FavoriteFragment()
    {
        // Required empty public constructor
    }


    public static FavoriteFragment newInstance()
    {
        FavoriteFragment fragment = new FavoriteFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        no_listView_layout = (LinearLayout) view.findViewById(R.id.no_listView_layout);
        listView_layout = (LinearLayout) view.findViewById(R.id.listView_layout);

        Intent intent=getActivity().getIntent();
        String str=intent.getExtras().getString("server_data");

        if(str != null)
        {
            Log.d("fragment", str);
            favorite_list = parseParkList(str,false,false);

            for(ParkItem item : favorite_list)
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
        else
            favorite_list = new ArrayList<>();

        // 즐겨찾기 항목이 존재하는지 확인
        if( favorite_list.size() > 0 )
        {
            no_listView_layout.setVisibility(View.GONE);
            listView_layout.setVisibility(View.VISIBLE);

            listView = (ListView) view.findViewById(R.id.listView);
            adapter = new FavoriteListAdapter(getActivity(), R.layout.favorite_list_item, favorite_list, no_listView_layout, listView_layout);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    //Log.d("11111",Integer.toString(position));
                    //Toast.makeText(getActivity(), favorite_list.get(position).getName(),Toast.LENGTH_SHORT).show();
                    selected_item = favorite_list.get(position);

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

}