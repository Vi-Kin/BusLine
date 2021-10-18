package com.vi.busline.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.database.Search_BusStop;

import java.util.List;


public class SearchLineAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Search_BusStop> mBusStopList;

    public SearchLineAdapter(Context context, List<Search_BusStop> busStopList) {
        this.mContext = context;
        this.mBusStopList = busStopList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_search_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Search_BusStop mBusStop = mBusStopList.get(position);
        ((LinearViewHolder) holder).mTextView.setText(String.valueOf(mBusStop.getStopName()));
    }

    @Override
    public int getItemCount() {
        return mBusStopList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_user_search_line_item);

        }
    }

}
