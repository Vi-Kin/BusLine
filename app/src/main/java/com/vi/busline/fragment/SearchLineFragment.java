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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.DateBaseHelper;
import com.vi.busline.database.Search_BusStop;
import com.vi.busline.recyclerview.SearchLineAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchLineFragment extends Fragment {
    private ArrayList<Search_BusStop> busStopList = new ArrayList<>();
    private SearchLineAdapter mSearchLineAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;
    private TextView mTextView;
    private TextView mStartTextView;
    private TextView mEndTextView;
    private TextView mTextViewChange;
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private Button mDeleteButton;
    private Button mAddButton;
    private Button mChangeNameButton;
    private Button mDeleteAllButton;
    private List stopList = new ArrayList();
    private List stopList2 = new ArrayList();
    private Spinner mDeleteStopSpinner;
    private Spinner mAddSelectStopSpinner;
    private Spinner mAddStopSpinner;
    private EditText mChangeNameEditText;
    private boolean ASC = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_line, null);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mTextView = view.findViewById(R.id.tv_user_search_line);
        mStartTextView = view.findViewById(R.id.tv_user_search_start);
        mEndTextView = view.findViewById(R.id.tv_user_search_end);
        mTextViewChange = view.findViewById(R.id.tv_user_search_line_change);
        mRecyclerView = view.findViewById(R.id.rv_user_search_line);
        mRecyclerView.setLayoutManager(manager);
        mLinearLayout = view.findViewById(R.id.ll_user_search_admin);
        getAdminOrNot();
        mSearchLineAdapter = new SearchLineAdapter(getActivity(), busStopList);
        mRecyclerView.setAdapter(mSearchLineAdapter);
        mTextViewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ASC) {
                    ASC = false;
                    initBusLine(false);
                    mSearchLineAdapter.notifyDataSetChanged();
                } else {
                    ASC = true;
                    initBusLine(true);
                    mSearchLineAdapter.notifyDataSetChanged();
                }
            }
        });
        initBusLine(ASC);
        mDeleteButton = view.findViewById(R.id.search_line_delete_stop);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View view = inflater.inflate(R.layout.dialog_layout_delete_stop, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("删除站点");
                builder.setView(view);
                mDeleteStopSpinner = view.findViewById(R.id.sp_delete_line);
                initDeleteStopSpinnerData();
                ArrayAdapter myAdapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_item, stopList);
                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mDeleteStopSpinner.setAdapter(myAdapter);
                builder.setPositiveButton("删除", new AlertDialog.OnClickListener() {

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
                        String stop = (String) mDeleteStopSpinner.getSelectedItem();
                        Bundle bundle = getArguments();
                        String result = bundle.getString("LINE");
                        if (stop.equals("选择需要删除的站点")){
                            ToastUtil.showToast(getActivity(),"请选择需要删除的站点");
                        }else {
                            mSQLiteDatabase.execSQL("DELETE FROM stop_line WHERE line = "+ result +" AND stopid IN (SELECT stopid FROM stop WHERE stopname = '"+ stop +"')");
                            ToastUtil.showToast(getActivity(),"删除站点 "+ stop +" 成功！");
                            dialog.dismiss();
                            initBusLine(ASC);
                            mSearchLineAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        mDeleteAllButton = view.findViewById(R.id.search_line_delete_all);
        mDeleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("是否删除该条线路？");
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
                        Bundle bundle = getArguments();
                        String result = bundle.getString("LINE");
                        mDateBaseHelper = new DateBaseHelper(getContext(), "BusLineDataBase.db", null, 1);
                        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
                        mSQLiteDatabase.execSQL("DELETE FROM stop_line WHERE line = "+ result +"");
                        mDateBaseHelper.close();
                        mSQLiteDatabase.close();
                        dialog.dismiss();
                    }
                });
            }
        });
        mChangeNameButton = view.findViewById(R.id.search_line_change_name);
        mChangeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View view = inflater.inflate(R.layout.dialog_layout_change_name, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("更改线路名称");
                builder.setView(view);
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
                        Bundle bundle = getArguments();
                        String result = bundle.getString("LINE");
                        mChangeNameEditText = view.findViewById(R.id.et_change_name);
                        String newName = mChangeNameEditText.getText().toString();
                        mDateBaseHelper = new DateBaseHelper(getContext(), "BusLineDataBase.db", null, 1);
                        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
                        if (CheckIsDataAlreadyInDBorNot(newName)){
                            ToastUtil.showToast(getActivity(),"该线路已存在！");
                        }
                        else {
                            Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM stop_line WHERE line = ?", new String[]{result});
                            while (cursor != null && cursor.moveToNext()) {
                                String line = cursor.getString(cursor.getColumnIndex("line"));
                                int stopid = cursor.getInt(cursor.getColumnIndex("stopid"));
                                mSQLiteDatabase.execSQL("DELETE FROM stop_line WHERE line = '"+line+"' AND stopid ="+stopid+"");
                                mSQLiteDatabase.execSQL("INSERT INTO stop_line VALUES ('"+newName+"',"+stopid+",NULL)");
                            }
                            ToastUtil.showToast(getActivity(),"修改成功！");
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        mAddButton = view.findViewById(R.id.search_line_add_stop);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View view = inflater.inflate(R.layout.dialog_layout_add_stop, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("增加站点");
                builder.setView(view);
                initAddStopSpinnerData();
                mAddStopSpinner = view.findViewById(R.id.sp_add_line);
                ArrayAdapter myAdapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_item, stopList);
                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mAddStopSpinner.setAdapter(myAdapter);
                initAddSelectStopSpinnerData();
                mAddSelectStopSpinner = view.findViewById(R.id.sp_addSelect_line);
                ArrayAdapter mySelectAdapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_item, stopList2);
                mySelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mAddSelectStopSpinner.setAdapter(mySelectAdapter);
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
                        String position = (String) mAddStopSpinner.getSelectedItem();
                        String stop = (String) mAddSelectStopSpinner.getSelectedItem();
                        Bundle bundle = getArguments();
                        String result = bundle.getString("LINE");
                        if (stop.equals("选择需添加的站点") & position.equals("选择站点添加的位置（站点之后）")){
                            ToastUtil.showToast(getActivity(),"请选择站点信息！");
                        }else if (!stop.equals("选择需添加的站点") & position.equals("选择站点添加的位置（站点之后）")){
                            ToastUtil.showToast(getActivity(),"请选择站点的添加位置！");
                        }else if (stop.equals("选择需添加的站点") & !position.equals("选择站点添加的位置（站点之后）")){
                            ToastUtil.showToast(getActivity(),"请选择需添加的站点！");
                        }else {
                            Cursor cursor = mSQLiteDatabase.rawQuery("SELECT stopid FROM stop WHERE stopname = ?", new String[]{stop});
                            cursor.moveToFirst();
                            int id = cursor.getInt(cursor.getColumnIndex("stopid"));
                            cursor.close();

                            if (position.equals("起点站")){
                                Cursor cursor1 = mSQLiteDatabase.rawQuery("SELECT * FROM stop_line WHERE line = ?", new String[]{result});
                                while (cursor1 != null && cursor1.moveToNext()) {
                                    if (cursor1.isFirst()){
                                        mSQLiteDatabase.execSQL("INSERT INTO stop_line VALUES ('"+result+"',"+id+",NULL)");
                                    }
                                    int stopid = cursor1.getInt(cursor1.getColumnIndex("stopid"));
                                    mSQLiteDatabase.execSQL("DELETE FROM stop_line WHERE line = '"+result+"' AND stopid ="+stopid+"");
                                    mSQLiteDatabase.execSQL("INSERT INTO stop_line VALUES ('"+result+"',"+stopid+",NULL)");
                                }
                                cursor1.close();
                            }else if (position.equals("终点站")){
                                mSQLiteDatabase.execSQL("INSERT INTO stop_line VALUES ('"+result+"',"+id+",NULL)");
                            }else {
                                Cursor cursor2 = mSQLiteDatabase.rawQuery("SELECT stopid FROM stop WHERE stopname = ?", new String[]{position});
                                cursor2.moveToFirst();
                                int id_position = cursor2.getInt(cursor2.getColumnIndex("stopid"));
                                cursor2.close();
                                Cursor cursor3 = mSQLiteDatabase.rawQuery("SELECT * FROM stop_line WHERE line = ?", new String[]{result});
                                Boolean I = false;
                                while (cursor3 != null && cursor3.moveToNext()) {
                                    int stopid = cursor3.getInt(cursor3.getColumnIndex("stopid"));
                                    if (I = true){
                                        mSQLiteDatabase.execSQL("DELETE FROM stop_line WHERE line = '"+result+"' AND stopid ="+stopid+"");
                                        mSQLiteDatabase.execSQL("INSERT INTO stop_line VALUES ('"+result+"',"+stopid+",NULL)");
                                    }
                                    if (stopid == id_position) {
                                        I = true;
                                        mSQLiteDatabase.execSQL("INSERT INTO stop_line VALUES ('"+result+"',"+id+",NULL)");
                                    }
                                }
                                cursor3.close();
                            }
                            ToastUtil.showToast(getActivity(),"增加站点 "+ stop +" 成功！");
                            dialog.dismiss();
                            initBusLine(ASC);
                            mSearchLineAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
        return view;
    }

    private void initBusLine(boolean ASC) {
        busStopList.clear();
        Bundle bundle = getArguments();
        String result = bundle.getString("LINE");
        mTextView.setText(result);
        String sqlSelect;
        if (ASC) {
            sqlSelect = "select stopname,position from stop_line INNER JOIN stop ON stop_line.stopid = stop.stopid  WHERE line = ?";
        } else {
            sqlSelect = "select stopname,position from stop_line INNER JOIN stop ON stop_line.stopid = stop.stopid  WHERE line = ? ORDER BY position DESC";
        }
        mDateBaseHelper = new DateBaseHelper(getContext(), "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{result});
        while (cursor != null && cursor.moveToNext()) {
            String mStopName = cursor.getString(cursor.getColumnIndex("stopname"));
            if (cursor.isFirst()){
                mStartTextView.setText(mStopName);
            }
            if (cursor.isLast()){
                mEndTextView.setText(mStopName);
            }
            int mPosition = cursor.getInt(cursor.getColumnIndex("position"));
            Search_BusStop busStop = new Search_BusStop(mStopName, mPosition);
            busStopList.add(busStop);
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

    private void initDeleteStopSpinnerData() {
        String sqlSelect;
        Bundle bundle = getArguments();
        assert bundle != null;
        String result = bundle.getString("LINE");
        stopList.clear();
        stopList.add("选择需要删除的站点");
        if (ASC) {
            sqlSelect = "SELECT stopname,position FROM stop INNER JOIN stop_line ON stop.stopid = stop_line.stopid where line =?";
        } else {
            sqlSelect = "SELECT stopname,position FROM stop INNER JOIN stop_line ON stop.stopid = stop_line.stopid where line =? ORDER BY position DESC";
        }
        mDateBaseHelper = new DateBaseHelper(getActivity(), "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{result});
        while (cursor.moveToNext()) {
            String stopname = cursor.getString(cursor.getColumnIndex("stopname"));
            stopList.add(stopname);
        }
        cursor.close();
    }

    private void initAddStopSpinnerData() {
        Bundle bundle = getArguments();
        String result = bundle.getString("LINE");
        stopList.clear();
        stopList.add("选择站点添加的位置（站点之后）");
        stopList.add("起点站");
        String sql = "SELECT stopname FROM stop INNER JOIN stop_line ON stop.stopid = stop_line.stopid where line =?";
        mDateBaseHelper = new DateBaseHelper(getActivity(), "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, new String[]{result});
        while (cursor.moveToNext()) {
            String stopname = cursor.getString(cursor.getColumnIndex("stopname"));
            stopList.add(stopname);
        }
        stopList.add("终点站");
        cursor.close();
    }

    private void initAddSelectStopSpinnerData() {
        stopList2.clear();
        stopList2.add("选择需添加的站点");
        String sql = "SELECT stopname FROM stop";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()) {
            String stopname = cursor.getString(cursor.getColumnIndex("stopname"));
            if (!stopList.contains(stopname)){
                stopList2.add(stopname);
            }
        }
        cursor.close();
    }
    public boolean CheckIsDataAlreadyInDBorNot(String value) {
        String Query = "Select * from stop_line where line =?";
        Cursor cursor = mSQLiteDatabase.rawQuery(Query, new String[]{value});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
}
