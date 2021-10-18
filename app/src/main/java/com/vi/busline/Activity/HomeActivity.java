package com.vi.busline.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vi.busline.R;
import com.vi.busline.fragment.HomeFragment;
import com.vi.busline.fragment.LineFragment;
import com.vi.busline.fragment.UserFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private RadioButton mrb_home, mrb_line, mrb_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.ll_main_content, new HomeFragment());
        transaction.commit();

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

    public void initView() {
        mrb_home = findViewById(R.id.rd_home);
        mrb_line = findViewById(R.id.rd_line);
        mrb_user = findViewById(R.id.rd_user);

        mrb_home.setOnClickListener(this);
        mrb_line.setOnClickListener(this);
        mrb_user.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        transaction = manager.beginTransaction();
        switch (v.getId()) {
            case R.id.rd_home:
                transaction.replace(R.id.ll_main_content, new HomeFragment());
                break;
            case R.id.rd_line:
                transaction.replace(R.id.ll_main_content, new LineFragment());
                break;
            case R.id.rd_user:
                transaction.replace(R.id.ll_main_content, new UserFragment());
                break;
        }
        transaction.commit();
    }
}
