package com.vi.busline.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.DateBaseHelper;

public class LoginActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mTextView1;
    private TextView mTextView2;
    private EditText admin_username;
    private EditText admin_password;
    private EditText username;
    private EditText password;
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private String uid = null;
    private String pwd = null;
    private String admin_uid = null;
    private String admin_pwd = null;
    CheckBox mCheckBox;


    // 判断是否为管理员
    private void saveAdminOrNot(String uid, String admin) {
        SharedPreferences mSharedPreferences = getSharedPreferences("AdminOrNot", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("uid", uid);
        editor.putString("admin", admin);
        editor.apply();
    }

    //清除  记住密码
    private void clearDataBase() {
        SharedPreferences mSharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //保存数据 记住密码
    private void saveDataBase() {
        SharedPreferences mSharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("username", username.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putBoolean("isChecked", true);
        editor.putBoolean("save", true);
        editor.apply();            //写入数据
    }

    //读取数据  记住密码
    private void getDataBase() {
        SharedPreferences mSharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String MyUserName = mSharedPreferences.getString("username", "");
        String MyPassWord = mSharedPreferences.getString("password", "");
        Boolean isChecked = mSharedPreferences.getBoolean("isChecked", false);
        mCheckBox.setChecked(isChecked);
        username.setText(MyUserName);
        password.setText(MyPassWord);
    }

    public boolean login(String uid, String pwd, String admin) {
        String sql = "Select * from user where uid=? and pwd=? and admin=?";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, new String[]{uid, pwd, admin});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mButton = findViewById(R.id.btn_login);
        mTextView1 = findViewById(R.id.tv_admin_login);
        mTextView2 = findViewById(R.id.tv_user_register);
        username = findViewById(R.id.et_user_username);
        password = findViewById(R.id.et_user_password);
        mCheckBox = findViewById(R.id.cb_password);
        mDateBaseHelper = new DateBaseHelper(this, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        SharedPreferences mSharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        if (mSharedPreferences.getBoolean("save", false)) {    //判断是否写入了数值save==true
            getDataBase();
        }
        setListeners();
    }

    private void setListeners() {
        LoginActivity.OnClick onClick = new LoginActivity.OnClick();
        mButton.setOnClickListener(onClick);
        mTextView1.setOnClickListener(onClick);
        mTextView2.setOnClickListener(onClick);

    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    uid = username.getText().toString();
                    pwd = password.getText().toString();
                    if (uid.equals("")) {
                        if (pwd.equals("")) {
                            ToastUtil.showToast(LoginActivity.this, "请输入用户名和密码！");
                        } else if (!pwd.equals("")) {
                            ToastUtil.showToast(LoginActivity.this, "请输入用户名！");
                        }
                    } else if (!uid.equals("")) {
                        if (pwd.equals("")) {
                            ToastUtil.showToast(LoginActivity.this, "请输入密码！");
                        } else if (!uid.equals("")) {
                            if (login(uid, pwd, "0")) {
                                saveAdminOrNot(uid, "0");
                                ToastUtil.showToast(LoginActivity.this, "登录成功！");
                                if (mCheckBox.isChecked()) {                    //当多选按钮按下时执行报损数据
                                    saveDataBase();
                                } else {
                                    clearDataBase();
                                }
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (login(uid, pwd, "1")) {
                                saveAdminOrNot(uid, "0");
                                ToastUtil.showToast(LoginActivity.this, "管理员登录成功！");
                                if (mCheckBox.isChecked()) {                    //当多选按钮按下时执行报损数据
                                    saveDataBase();
                                } else {
                                    clearDataBase();
                                }
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showToast(LoginActivity.this, "密码错误！");
                            }
                        }
                    }
                    break;
                case R.id.tv_user_register:
                    Intent intent2 = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent2);
                    break;

                case R.id.tv_admin_login:
                    LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
                    final View view = inflater.inflate(R.layout.dialog_layout_admin_login, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("管理员登录");
                    builder.setView(view);
                    builder.setPositiveButton("登录", new AlertDialog.OnClickListener() {

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
                            Boolean wantToCloseDialog = false;
                            admin_username = view.findViewById(R.id.register_et_username);
                            admin_password = view.findViewById(R.id.register_et_password);
                            admin_uid = admin_username.getText().toString();
                            admin_pwd = admin_password.getText().toString();
                            if (admin_uid.equals("")) {
                                if (admin_pwd.equals("")) {
                                    ToastUtil.showToast(LoginActivity.this, "请输入用户名和密码！");
                                } else if (!admin_pwd.equals("")) {
                                    ToastUtil.showToast(LoginActivity.this, "请输入用户名！");
                                }
                            } else if (!admin_uid.equals("")) {
                                if (admin_pwd.equals("")) {
                                    ToastUtil.showToast(LoginActivity.this, "请输入密码！");
                                } else if (!admin_uid.equals("")) {
                                    if (login(admin_uid, admin_pwd, "0")) {
                                        ToastUtil.showToast(LoginActivity.this, "该用户不是管理员！");
                                    } else if (login(admin_uid, admin_pwd, "1")) {
                                        saveAdminOrNot(null, "1");
                                        ToastUtil.showToast(LoginActivity.this, "登录成功！");
                                        Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                        startActivity(intent);
                                        wantToCloseDialog = true;
                                    } else {
                                        ToastUtil.showToast(LoginActivity.this, "密码错误！");
                                    }
                                }
                            }
                            if (wantToCloseDialog) {
                                dialog.dismiss();
                                finish();
                            }
                        }
                    });
                    break;
            }
        }
    }
}
