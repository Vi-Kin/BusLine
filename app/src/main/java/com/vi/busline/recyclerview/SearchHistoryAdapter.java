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
import com.vi.busline.database.Search_History;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Search_History> mHistoryList;


    public SearchHistoryAdapter(Context context, List<Search_History> mHistoryList) {
        this.mContext = context;
        this.mHistoryList = mHistoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Search_History mSearch_History = mHistoryList.get(position);
        ((LinearViewHolder) holder).mTextView.setText(String.valueOf(mSearch_History.getHistory()));
        ((LinearViewHolder) holder).mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtil.showToast(mContext,mSearch_History.getHistory());
            }
        });


    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_user_search_history_item);
        }
    }
}
