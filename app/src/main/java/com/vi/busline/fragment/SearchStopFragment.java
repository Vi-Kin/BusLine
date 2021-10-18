package com.vi.busline.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.database.DateBaseHelper;
import com.vi.busline.database.Search_BusLine;
import com.vi.busline.recyclerview.SearchStopAdapter;

import java.util.ArrayList;

public class SearchStopFragment extends Fragment {
    private ArrayList<Search_BusLine> busLineList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_stop, null);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mTextView = view.findViewById(R.id.tv_user_search_stop);
        mLinearLayout = view.findViewById(R.id.ll_user_search_admin_stop);
        getAdminOrNot();
        mRecyclerView = view.findViewById(R.id.rv_user_search_stop);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(new SearchStopAdapter(getActivity(), busLineList));
        initBusLine();
        return view;
    }

    private void initBusLine() {
        Bundle bundle = getArguments();
        String result = bundle.getString("STOP");
        mTextView.setText(result);
        String sqlSelect = "SELECT line FROM stop_line INNER JOIN stop ON stop_line.stopid = stop.stopid WHERE stopname = '" + result + "'";
        mDateBaseHelper = new DateBaseHelper(getContext(), "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{});
        while (cursor != null && cursor.moveToNext()) {
            String line = cursor.getString(cursor.getColumnIndex("line"));
            Search_BusLine busline = new Search_BusLine(line);
            busLineList.add(busline);
        }
    }

    private void getAdminOrNot() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("AdminOrNot", Activity.MODE_PRIVATE);
        String AdminOrNot = mSharedPreferences.getString("admin", "");
        if (AdminOrNot.equals("1")) {
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mLinearLayout.setVisibility(View.GONE);
        }
    }

}
