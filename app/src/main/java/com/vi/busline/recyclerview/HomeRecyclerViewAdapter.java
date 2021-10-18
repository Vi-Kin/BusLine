package com.vi.busline.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vi.busline.R;
import com.vi.busline.database.BusLine;
import com.vi.busline.database.BusStop;
import com.vi.busline.database.DateBaseHelper;

import java.util.ArrayList;
import java.util.List;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static OnItemClickListener mOnItemClickListener;
    private List<BusStop> mBusStopList;
    DateBaseHelper mDateBaseHelper;
    SQLiteDatabase mSQLiteDatabase;
    private Context mContext;
    private static final int TYPE_MAP = 0;
    private static final int TYPE_TITLE = 1;
    private static final int TYPE_LINE = 2;


    public interface OnItemClickListener {
        void onClick(String stop);
    }

    public static void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;

    }

    public HomeRecyclerViewAdapter(Context context, List<BusStop> busStopList) {
        this.mContext = context;
        this.mBusStopList = busStopList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_MAP) {
            View itemView = inflater.inflate(R.layout.layout_recycler_map, null, true);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lp);
            return new MapViewHolder(itemView);
        } else if (viewType == TYPE_TITLE) {
            View itemView = inflater.inflate(R.layout.layout_recycler_title, null, true);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lp);
            return new TitleViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.layout_recycler_line, null, true);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lp);
            return new LineViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MapViewHolder) {
        }
        if (holder instanceof TitleViewHolder) {
        }
        if (holder instanceof LineViewHolder) {
            final BusStop mBusStop = mBusStopList.get(position - 2);
            ArrayList<BusLine> busLineList = new ArrayList<>();
            String sqlSelect = "SELECT line FROM stop_line INNER JOIN stop ON stop_line.stopid = stop.stopid where stopname ='" + mBusStop.getStopName() + "'";
            mDateBaseHelper = new DateBaseHelper(mContext, "BusLineDataBase.db", null, 1);
            mSQLiteDatabase = mDateBaseHelper.getWritableDatabase();
            Cursor cursor = mSQLiteDatabase.rawQuery(sqlSelect, new String[]{});
            while (cursor != null && cursor.moveToNext()) {
                String line = cursor.getString(cursor.getColumnIndex("line"));
                BusLine busline = new BusLine(line);
                busLineList.add(busline);
            }
            cursor.close();
            ((LineViewHolder) holder).mTextView.setText(String.valueOf(mBusStop.getStopName()));
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            ((LineViewHolder) holder).mRecyclerView.setLayoutManager(layoutManager);
            ((LineViewHolder) holder).mRecyclerView.setAdapter(new HorizontalRecyclerViewAdapter(mContext, busLineList));
            ((LineViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(mBusStop.getStopName());
                }
            });
        }
    }

    // 判断recycleview视图的类型
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_MAP;
        } else if (position == 1) {
            return TYPE_TITLE;
        } else {
            return TYPE_LINE;
        }
    }

    @Override
    public int getItemCount() {
        return mBusStopList.size() + 2;
    }

    final class MapViewHolder extends RecyclerView.ViewHolder {

        public MapViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    final class TitleViewHolder extends RecyclerView.ViewHolder {

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    final class LineViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        View MyView;
        public RecyclerView mRecyclerView;

        public LineViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_recyclerview);
            MyView = itemView;
            mRecyclerView = itemView.findViewById(R.id.horizontal_recycler);


        }
    }

}
