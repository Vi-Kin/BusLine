package com.vi.busline.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.database.DateBaseHelper;
import com.vi.busline.database.Search_History;
import com.vi.busline.recyclerview.SearchHistoryAdapter;

import java.util.ArrayList;

public class SearchHistoryFragment extends Fragment {
    private ArrayList<Search_History> historyList = new ArrayList<>();
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TextView mTextView;
    private TextView mEmptyTextView;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private boolean isHistory = true;
    private String result = null;
    String sql;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_history, null);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mTextView = view.findViewById(R.id.tv_user_search_history);
        mEmptyTextView = view.findViewById(R.id.iv_user_search_history_empty);
        mImageView = view.findViewById(R.id.iv_user_search_history);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("是否删除所有搜索记录？");
                builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDateBaseHelper = new DateBaseHelper(getContext(), "BusLineDataBase.db", null, 1);
                        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
                        if (getAdminOrNot().equals("0")) {
                            sql = "DELETE FROM history";
                        } else {
                            sql = "DELETE FROM admin_history";
                        }
                        mSQLiteDatabase.execSQL(sql);
                        initHistory(result);
                        mSearchHistoryAdapter.notifyDataSetChanged();
                        mDateBaseHelper.close();
                        mSQLiteDatabase.close();
                        dialog.dismiss();
                    }
                });
            }
        });
        mRecyclerView = view.findViewById(R.id.rv_user_search_history);
        mRecyclerView.setLayoutManager(manager);
        mSearchHistoryAdapter = new SearchHistoryAdapter(getActivity(), historyList);
        mRecyclerView.setAdapter(mSearchHistoryAdapter);
        Bundle bundle = getArguments();
        result = bundle.getString("HISTORY");
        if (result.equals("")) {
            mTextView.setText("搜索记录");
            isHistory = true;
            initHistory(result);
            mSearchHistoryAdapter.notifyDataSetChanged();
        } else {
            mTextView.setText("猜你想找");
            isHistory = false;
            mImageView.setVisibility(View.INVISIBLE);
            mEmptyTextView.setVisibility(View.INVISIBLE);
            initHistory(result);
            mSearchHistoryAdapter.notifyDataSetChanged();
        }
        return view;
    }

    private void initHistory(String info) {
        historyList.clear();
        mDateBaseHelper = new DateBaseHelper(getContext(), "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        if (isHistory) {
            if (getAdminOrNot().equals("0")) {
                String sqlSelect = "SELECT * FROM history ORDER BY hid DESC";
                Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{});
                if (cursor.getCount() != 0) {
                    mImageView.setVisibility(View.VISIBLE);
                    mEmptyTextView.setVisibility(View.INVISIBLE);
                    while (cursor != null && cursor.moveToNext()) {
                        String history = cursor.getString(cursor.getColumnIndex("history"));
                        Search_History mHistory = new Search_History(history);
                        historyList.add(mHistory);
                    }
                } else {
                    mImageView.setVisibility(View.INVISIBLE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    cursor.close();
                }
            } else {
                String sqlSelect = "SELECT * FROM admin_history ORDER BY hid DESC";
                Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{});
                if (cursor.getCount() != 0) {
                    mImageView.setVisibility(View.VISIBLE);
                    mEmptyTextView.setVisibility(View.INVISIBLE);
                    while (cursor != null && cursor.moveToNext()) {
                        String history = cursor.getString(cursor.getColumnIndex("admin_history"));
                        Search_History mHistory = new Search_History(history);
                        historyList.add(mHistory);
                    }
                } else {
                    mImageView.setVisibility(View.INVISIBLE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    cursor.close();
                }
            }

        } else {
            String sqlSelect1 = "SELECT stopname FROM stop WHERE stopname LIKE '%" + info + "%'";
            String sqlSelect2 = "SELECT DISTINCT line FROM stop_line WHERE line LIKE '%" + info + "%'";
            Cursor cursor1 = mSQLiteDatabase.rawQuery(sqlSelect1, new String[]{});
            Cursor cursor2 = mSQLiteDatabase.rawQuery(sqlSelect2, new String[]{});
            if (cursor1.getCount() != 0 & cursor2.getCount() == 0) {
                cursor2.close();
                while (cursor1 != null && cursor1.moveToNext()) {
                    String history = cursor1.getString(cursor1.getColumnIndex("stopname"));
                    Search_History mHistory = new Search_History(history);
                    historyList.add(mHistory);
                }
                cursor1.close();
            } else if (cursor1.getCount() == 0 & cursor2.getCount() != 0) {
                cursor1.close();
                while (cursor2 != null && cursor2.moveToNext()) {
                    String history = cursor2.getString(cursor2.getColumnIndex("line"));
                    Search_History mHistory = new Search_History(history);
                    historyList.add(mHistory);
                }
                cursor2.close();
            } else if (cursor1.getCount() != 0 & cursor2.getCount() != 0) {
                while (cursor1 != null && cursor1.moveToNext()) {
                    String history = cursor1.getString(cursor1.getColumnIndex("stopname"));
                    Search_History mHistory = new Search_History(history);
                    historyList.add(mHistory);
                }
                while (cursor2 != null && cursor2.moveToNext()) {
                    String history = cursor2.getString(cursor2.getColumnIndex("line"));
                    Search_History mHistory = new Search_History(history);
                    historyList.add(mHistory);
                }
                cursor1.close();
                cursor2.close();
            }else {
                Search_History mHistory = new Search_History("暂无信息");
                historyList.add(mHistory);
                cursor1.close();
                cursor2.close();
            }
            mDateBaseHelper.close();
            mSQLiteDatabase.close();
        }
    }

    private String getAdminOrNot() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("AdminOrNot", Activity.MODE_PRIVATE);
        String AdminOrNot = mSharedPreferences.getString("admin", "");
        return AdminOrNot;
    }
}
