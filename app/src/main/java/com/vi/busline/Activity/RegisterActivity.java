package com.vi.busline.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.DateBaseHelper;

public class RegisterActivity extends AppCompatActivity {
    Button mReturnButton;
    Button mRegisterButton;
    DateBaseHelper mDateBaseHelper;
    SQLiteDatabase mSQLiteDatabase;
    EditText username;
    EditText password;
    String uid = null;
    String pwd = null;
    String sql;

    public boolean CheckIsDataAlreadyInDBorNot(String value) {
        String Query = "Select * from user where uid =?";
        Cursor cursor = mSQLiteDatabase.rawQuery(Query, new String[]{value});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.et_admin_username);
        password = findViewById(R.id.et_admin_password);
        mDateBaseHelper = new DateBaseHelper(this, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        mReturnButton = findViewById(R.id.btn_return);
        mRegisterButton = findViewById(R.id.btn_register);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRegisterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uid = username.getText().toString();
                pwd = password.getText().toString();
                if (!uid.equals("") & !pwd.equals("")) {
                    if (!CheckIsDataAlreadyInDBorNot(uid)) {
                        sql = "insert into user values(" + uid + ",'" + pwd + "',0)";
                        mSQLiteDatabase.execSQL(sql);
                        username.setText("");
                        password.setText("");
                        ToastUtil.showToast(RegisterActivity.this, "注册成功！");
                        finish();
                    } else {
                        ToastUtil.showToast(RegisterActivity.this, "用户名已存在！");
                    }
                } else {
                    ToastUtil.showToast(RegisterActivity.this, "请输入用户名及密码！");
                }
            }
        });
    }
}
