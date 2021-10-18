package com.vi.busline.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.ToastUtil.ToastUtil;
import com.vi.busline.database.BusLine;

import java.util.List;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter {
    private List<BusLine> mBusLineList;
    private Context mContext;

    public HorizontalRecyclerViewAdapter(Context context, List<BusLine> busLineList) {
        this.mContext = context;
        mBusLineList = busLineList;
    }

    @NonNull
    @Override
    public HorizontalRecyclerViewAdapter.LinearLayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new LinearLayoutViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_horizontal_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BusLine mBusLine = mBusLineList.get(position);
        ((HorizontalRecyclerViewAdapter.LinearLayoutViewHolder) holder).mTextView.setText(String.valueOf(mBusLine.getLine()));
        ((HorizontalRecyclerViewAdapter.LinearLayoutViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(mContext, mBusLine.getLine());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBusLineList.size();
    }

    class LinearLayoutViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public LinearLayoutViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_horizontal);
        }
    }
}
