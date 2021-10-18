package com.vi.busline.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vi.busline.R;
import com.vi.busline.fragment.MapFragment;

public class SelectCityActivity extends AppCompatActivity {

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        mTextView = findViewById(R.id.tv_select_city);
        mTextView.setText(MapFragment.currentPosition);

    }
}
