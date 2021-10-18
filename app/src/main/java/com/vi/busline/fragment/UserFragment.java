package com.vi.busline.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vi.busline.Activity.LoginActivity;
import com.vi.busline.Activity.SelectCityActivity;
import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;

public class UserFragment extends Fragment {

    private TextView mTextView;
    private TextView profileTextView;
    private Button mButton;
    LinearLayout mLinearLayout;
    LinearLayout mCityLinearLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);
        mTextView = view.findViewById(R.id.tv_line_position);
        mTextView.setText(MapFragment.currentPosition);
        profileTextView = view.findViewById(R.id.tv_profile);
        getAdminOrNot();
        mLinearLayout = view.findViewById(R.id.user_fragment_profile);
        mButton = view.findViewById(R.id.btn_user_logout);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                ToastUtil.showToast(getActivity(), "退出成功！");
                getActivity().finish();
            }
        });
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getActivity().getApplicationContext(), "开发中..");
            }
        });

        mCityLinearLayout = view.findViewById(R.id.ll_user_fragment_city);
        mCityLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SelectCityActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getAdminOrNot() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("AdminOrNot", Activity.MODE_PRIVATE);
        String AdminOrNot = mSharedPreferences.getString("uid", "");
        profileTextView.setText(AdminOrNot);
    }
}
