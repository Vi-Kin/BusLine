package com.vi.busline.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.database.DateBaseHelper;
import com.vi.busline.database.Search_BusLine;

import java.util.List;


public class SearchStopAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Search_BusLine> mBusLineList;
    DateBaseHelper mDateBaseHelper;
    SQLiteDatabase mSQLiteDatabase;

    public SearchStopAdapter(Context context, List<Search_BusLine> busLineList) {
        this.mContext = context;
        this.mBusLineList = busLineList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_search_stop, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Search_BusLine mBusLine = mBusLineList.get(position);
        ((LinearViewHolder) holder).mTitleTextView.setText(String.valueOf(mBusLine.getLine()));
        ((LinearViewHolder) holder).mStartTextView.setText(String.valueOf(getStart(mBusLine.getLine())));
        ((LinearViewHolder) holder).mEndTextView.setText(String.valueOf(getEnd(mBusLine.getLine())));
    }

    @Override
    public int getItemCount() {
        return mBusLineList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mStartTextView;
        private TextView mEndTextView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.tv_user_search_stop_title);
            mStartTextView = itemView.findViewById(R.id.tv_user_search_stop_start);
            mEndTextView = itemView.findViewById(R.id.tv_user_search_stop_end);
        }
    }

    private String getStart(String line){
        String startStop = null;
        String sqlSelect = "SELECT stopname FROM stop_line INNER JOIN stop ON stop_line.stopid = stop.stopid where line =?";
        mDateBaseHelper = new DateBaseHelper(mContext, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{line});
        while (cursor != null && cursor.moveToNext()) {
            String STOP = cursor.getString(cursor.getColumnIndex("stopname"));
            if (cursor.isFirst()){
                startStop = STOP;
            }
        }
        return startStop;
    }
    private String getEnd(String line){
        String endStop = null;
        String sqlSelect = "SELECT stopname FROM stop_line INNER JOIN stop ON stop_line.stopid = stop.stopid where line =?";
        mDateBaseHelper = new DateBaseHelper(mContext, "BusLineDataBase.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{line});
        while (cursor != null && cursor.moveToNext()) {
            String STOP = cursor.getString(cursor.getColumnIndex("stopname"));
            if (cursor.isLast()){
                endStop = STOP;
            }
        }
        return endStop;
    }
}
