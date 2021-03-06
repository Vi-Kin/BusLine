package com.vi.busline.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.DateBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private TextView mTextView;
    private Button mButtonLogOut;
    private Button mButtonAddNewStop;
    private Button mButtonAddNewLine;
    private EditText addNewStop_et_name;
    private EditText addNewStop_et_longitude;
    private EditText addNewStop_et_latitude;
    private EditText addNewLine_et_name;
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private String lineName = null;
    private String stopName = null;
    private String longitude = null;
    private String latitude = null;
    private Spinner mSpinner;
    private List stopList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        mTextView = findViewById(R.id.tv_admin_main);
        mButtonLogOut = findViewById(R.id.btn_admin_logout);
        mButtonAddNewStop = findViewById(R.id.btn_addNewStop);
        mButtonAddNewLine = findViewById(R.id.btn_addNewLine);


        setListeners();
    }

    @Override
    protected void onDestroy() {
        clearAdminOrNot();
        super.onDestroy();
    }

    private void clearAdminOrNot() {
        SharedPreferences mSharedPreferences = getSharedPreferences("AdminOrNot", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void setListeners() {
        AdminMainActivity.OnClick onClick = new AdminMainActivity.OnClick();
        mTextView.setOnClickListener(onClick);
        mButtonLogOut.setOnClickListener(onClick);
        mButtonAddNewStop.setOnClickListener(onClick);
        mButtonAddNewLine.setOnClickListener(onClick);
    }


    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.tv_admin_main:
                    intent = new Intent(AdminMainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_admin_logout:
                    intent = new Intent(AdminMainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    ToastUtil.showToast(AdminMainActivity.this, "???????????????");
                    finish();
                    break;
                case R.id.btn_addNewStop:
                    LayoutInflater inflater = LayoutInflater.from(AdminMainActivity.this);
                    final View view = inflater.inflate(R.layout.dialog_layout_addnewstop, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminMainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("???????????????");
                    builder.setView(view);
                    builder.setPositiveButton("??????", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setNegativeButton("??????", new AlertDialog.OnClickListener() {

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
                            addNewStop_et_name = view.findViewById(R.id.et_addNewStop_name);
                            addNewStop_et_longitude = view.findViewById(R.id.et_addNewStop_longitude);
                            addNewStop_et_latitude = view.findViewById(R.id.et_addNewStop_latitude);
                            stopName = addNewStop_et_name.getText().toString();
                            longitude = addNewStop_et_longitude.getText().toString();
                            latitude = addNewStop_et_latitude.getText().toString();
                            mDateBaseHelper = new DateBaseHelper(AdminMainActivity.this, "BusLineDataBase.db", null, 1);
                            mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
                            boolean wantToCloseDialog = false;
                            if (!stopName.equals("") & !longitude.equals("") & !latitude.equals("")) {
                                if (CheckIsStopAlreadyInDBorNot(stopName)) {
                                    String sql = "insert into stop values(null,'" + stopName + "'," + longitude + "," + latitude + ")";
                                    mSQLiteDatabase.execSQL(sql);
                                    ToastUtil.showToast(AdminMainActivity.this, "????????????????????????");
                                    wantToCloseDialog = true;
                                } else
                                    ToastUtil.showToast(AdminMainActivity.this, "??????????????????");
                            } else if (stopName.equals("") & !longitude.equals("") & !latitude.equals("")) {
                                ToastUtil.showToast(AdminMainActivity.this, "?????????????????????");
                            } else if (!stopName.equals("") & longitude.equals("") & !latitude.equals("")) {
                                ToastUtil.showToast(AdminMainActivity.this, "????????????????????????");
                            } else if (!stopName.equals("") & !longitude.equals("") & latitude.equals("")) {
                                ToastUtil.showToast(AdminMainActivity.this, "????????????????????????");
                            } else if (stopName.equals("") & longitude.equals("") & !latitude.equals("")) {
                                ToastUtil.showToast(AdminMainActivity.this, "????????????????????????????????????");
                            } else if (!stopName.equals("") & longitude.equals("") & latitude.equals("")) {
                                ToastUtil.showToast(AdminMainActivity.this, "???????????????????????????????????????");
                            } else if (stopName.equals("") & !longitude.equals("") & latitude.equals("")) {
                                ToastUtil.showToast(AdminMainActivity.this, "????????????????????????????????????");
                            } else {
                                ToastUtil.showToast(AdminMainActivity.this, "????????????????????????");
                            }
                            if (wantToCloseDialog) {
                                dialog.dismiss();
                            }
                        }
                    });
                    break;
                case R.id.btn_addNewLine:
                    LayoutInflater inflater2 = LayoutInflater.from(AdminMainActivity.this);
                    final View view2 = inflater2.inflate(R.layout.dialog_layout_addnewline, null);
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminMainActivity.this);
                    builder2.setCancelable(false);
                    builder2.setTitle("???????????????");
                    builder2.setView(view2);
                    mSpinner = view2.findViewById(R.id.sp_admin_addNewLine);
                    initSpinnerData();
                    ArrayAdapter myAdapter = new ArrayAdapter(view2.getContext(), android.R.layout.simple_spinner_item, stopList);
                    myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinner.setAdapter(myAdapter);
                    builder2.setPositiveButton("??????", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder2.setNegativeButton("??????", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                    dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            addNewLine_et_name = view2.findViewById(R.id.et_addNewLine_name);
                            lineName = addNewLine_et_name.getText().toString();
                            String stop = (String) mSpinner.getSelectedItem();
                            boolean wantToCloseDialog = false;
                            if (!lineName.equals("") & !stop.equals("??????????????????")) {
                                if (CheckIsLineAlreadyInDBorNot(lineName)) {
                                    String sqlSelect = "SELECT stopid FROM stop WHERE stopname = ?";
                                    Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{stop});
                                    while (cursor != null && cursor.moveToNext()) {
                                        String id = cursor.getString(cursor.getColumnIndex("stopid"));
                                        String sqlInset = "insert into stop_line values('" + lineName + "'," + id + ",null)";
                                        mSQLiteDatabase.execSQL(sqlInset);
                                        ToastUtil.showToast(AdminMainActivity.this, "????????????????????????");
                                        wantToCloseDialog = true;
                                    }
                                } else ToastUtil.showToast(AdminMainActivity.this, "?????????????????????");
                            } else if (lineName.equals("") & !stop.equals("??????????????????")) {
                                ToastUtil.showToast(AdminMainActivity.this, "?????????????????????");
                            } else if (!lineName.equals("") & stop.equals("??????????????????")) {
                                ToastUtil.showToast(AdminMainActivity.this, "?????????????????????");
                            } else if (lineName.equals("") & stop.equals("??????????????????")) {
                                ToastUtil.showToast(AdminMainActivity.this, "????????????????????????");
                            }
                            if (wantToCloseDialog) {
                                dialog2.dismiss();
                            }
                        }
                    });
                    break;
            }
        }
    }

    private List initSpinnerData() {
        stopList.clear();
        stopList.add("??????????????????");
        String sqlSelect = "SELECT stopname FROM stop ";
        mDateBaseHelper = new DateBaseHelper(AdminMainActivity.this, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{});
        while (cursor.moveToNext()) {
            String stopname = cursor.getString(cursor.getColumnIndex("stopname"));
            stopList.add(stopname);
        }
        return stopList;
    }

    private boolean CheckIsStopAlreadyInDBorNot(String value) {
        String Query = "Select stopname from stop where stopname = ?";
        Cursor cursor = mSQLiteDatabase.rawQuery(Query, new String[]{value});
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private boolean CheckIsLineAlreadyInDBorNot(String value) {
        String Query = "Select line from stop_line where line = ?";
        Cursor cursor = mSQLiteDatabase.rawQuery(Query, new String[]{value});
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

}
