package com.vi.busline.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.Activity.SearchActivity;
import com.vi.busline.Activity.SelectCityActivity;
import com.vi.busline.R;
import com.vi.busline.ToastUtil.DistanceUtil;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.BusStop;
import com.vi.busline.database.DateBaseHelper;
import com.vi.busline.recyclerview.HomeRecyclerViewAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private ArrayList<BusStop> busStopList = new ArrayList<>();
    private TextView mLocationTextView;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, null);
        initBusStop();
        mLocationTextView = view.findViewById(R.id.home_location);
        mTextView = view.findViewById(R.id.home_search);
        if (MapFragment.currentPosition != null) {
            mLocationTextView.setText(MapFragment.currentPosition);
        }
        mLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SelectCityActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.home_recyclerview);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(new HomeRecyclerViewAdapter(getActivity(), busStopList));
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        manager.setInitialPrefetchItemCount(4);
        HomeRecyclerViewAdapter.setOnItemClickListener(new HomeRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(String stop) {
                ToastUtil.showToast(getActivity().getApplicationContext(), stop);
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initBusStop() {
        String sqlSelect = "SELECT * FROM stop ";
        mDateBaseHelper = new DateBaseHelper(getActivity(), "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{});
        while (cursor.moveToNext()) {
            String stopname = cursor.getString(cursor.getColumnIndex("stopname"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            if (DistanceUtil.getDistance(longitude,latitude,MapFragment.currentLongitude,MapFragment.currentLatitude) <= 1000){
                BusStop busstop = new BusStop(stopname);
                busStopList.add(busstop);
            }
        }
    }

}