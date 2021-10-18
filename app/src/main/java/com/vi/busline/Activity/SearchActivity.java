package com.vi.busline.Activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.DateBaseHelper;
import com.vi.busline.fragment.SearchEmptyFragment;
import com.vi.busline.fragment.SearchHistoryFragment;
import com.vi.busline.fragment.SearchLineFragment;
import com.vi.busline.fragment.SearchStopFragment;

public class SearchActivity extends AppCompatActivity {

    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private EditText mEditText;
    private TextView mTextView;
    private String searchInfo;
    private String typing;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private SearchStopFragment mSearchStopFragment;
    private SearchLineFragment mSearchLineFragment;
    private SearchHistoryFragment mSearchHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("HISTORY", "");
        mSearchHistoryFragment = new SearchHistoryFragment();
        transaction.add(R.id.ll_user_fragment, mSearchHistoryFragment);
        mSearchHistoryFragment.setArguments(bundle);
        transaction.commit();
        mEditText = findViewById(R.id.et_user_search);
        mTextView = findViewById(R.id.tv_user_search);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                typing = mEditText.getText().toString();
                transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("HISTORY", typing);
                mSearchHistoryFragment = new SearchHistoryFragment();
                transaction.replace(R.id.ll_user_fragment, mSearchHistoryFragment);
                mSearchHistoryFragment.setArguments(bundle);
                transaction.commit();
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInfo = mEditText.getText().toString();
                if (searchInfo.equals("")) {
                    ToastUtil.showToast(SearchActivity.this, "请输入搜索信息！");
                } else {
                    transaction = manager.beginTransaction();
                    if (CheckIsInLineOrStop(searchInfo) == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("STOP", searchInfo);
                        deleteAlreadyInDtaBseAndSaveSearch();
                        mSearchStopFragment = new SearchStopFragment();
                        transaction.replace(R.id.ll_user_fragment, mSearchStopFragment);
                        mSearchStopFragment.setArguments(bundle);
                        transaction.commit();
                    } else if (CheckIsInLineOrStop(searchInfo) == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("LINE", searchInfo);
                        deleteAlreadyInDtaBseAndSaveSearch();
                        mSearchLineFragment = new SearchLineFragment();
                        transaction.replace(R.id.ll_user_fragment, mSearchLineFragment);
                        mSearchLineFragment.setArguments(bundle);
                        transaction.commit();
                    } else if (CheckIsInLineOrStop(searchInfo) == 2) {
                        transaction.replace(R.id.ll_user_fragment, new SearchEmptyFragment());
                        deleteAlreadyInDtaBseAndSaveSearch();
                        transaction.commit();
                    }

                }
            }
        });
    }

    private boolean checkHistoryAlreadyInDataBase(String string) {
        String mySql;
        mDateBaseHelper = new DateBaseHelper(SearchActivity.this, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        if (getAdminOrNot().equals("0")) {
            mySql = "Select history from history where history = ?";
        } else {
            mySql = "Select admin_history from admin_history where admin_history = ?";
        }
        Cursor cursor = mSQLiteDatabase.rawQuery(mySql, new String[]{string});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private int CheckIsInLineOrStop(String value) {
        mDateBaseHelper = new DateBaseHelper(SearchActivity.this, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        String Sql1 = "Select line from stop_line where line = ?";
        String Sql2 = "Select stopname from stop where stopname = ?";
        Cursor cursor1 = mSQLiteDatabase.rawQuery(Sql1, new String[]{value});
        Cursor cursor2 = mSQLiteDatabase.rawQuery(Sql2, new String[]{value});
        if (cursor1.getCount() == 0 && cursor2.getCount() != 0) {
            cursor1.close();
            cursor2.close();
            return 0;
        } else if (cursor1.getCount() != 0 && cursor2.getCount() == 0) {
            cursor1.close();
            cursor2.close();
            return 1;
        } else {
            cursor1.close();
            cursor2.close();
            return 2;
        }
    }

    private String getAdminOrNot() {
        SharedPreferences mSharedPreferences = getSharedPreferences("AdminOrNot", MODE_PRIVATE);
        String AdminOrNot = mSharedPreferences.getString("admin", "");
        return AdminOrNot;
    }

    private void deleteAlreadyInDtaBseAndSaveSearch() {
        if (getAdminOrNot().equals("0")) {
            if (checkHistoryAlreadyInDataBase(searchInfo)) {
                mSQLiteDatabase.execSQL("DELETE FROM history WHERE history ='" + searchInfo + "'");
                mSQLiteDatabase.execSQL("INSERT INTO history VALUES(null,'" + searchInfo + "')");
            } else {
                mSQLiteDatabase.execSQL("INSERT INTO history VALUES(null,'" + searchInfo + "')");
            }
        } else {
            if (checkHistoryAlreadyInDataBase(searchInfo)) {
                mSQLiteDatabase.execSQL("DELETE FROM admin_history WHERE admin_history ='" + searchInfo + "'");
                mSQLiteDatabase.execSQL("INSERT INTO admin_history VALUES(null,'" + searchInfo + "')");
            } else {
                mSQLiteDatabase.execSQL("INSERT INTO admin_history VALUES(null,'" + searchInfo + "')");
            }
        }
    }
}
