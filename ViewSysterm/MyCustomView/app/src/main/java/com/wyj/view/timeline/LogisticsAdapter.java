package com.wyj.view.timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.wyj.view.R;

public class LogisticsAdapter extends RecyclerView.Adapter<LogisticsAdapter.LogisticsViewHolder> {
    private static final String TAG = "LogisticsAdapter";

    private List<LogisticsInfo> mDatas;
    private LayoutInflater mInflater;

    public LogisticsAdapter(Context context, List<LogisticsInfo> infos) {
        mInflater = LayoutInflater.from(context);
        this.mDatas = infos;
    }


    @Override
    public LogisticsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LogisticsViewHolder(mInflater.inflate(R.layout.item_view_logistics, null));
    }

    @Override
    public void onBindViewHolder(LogisticsViewHolder holder, int position) {
        if (mDatas == null) return;
        LogisticsInfo logisticsInfo = mDatas.get(position);
        if (logisticsInfo == null) return;
        String state = logisticsInfo.getState();
        String dateStr = logisticsInfo.getDateStr();
        String desc = logisticsInfo.getDesc();
        holder.getTvState().setText(state);
        holder.getTvDate().setText(dateStr);
        holder.getTvDesc().setText(desc);
        holder.getTvState().setVisibility(TextUtils.isEmpty(state) ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    static class LogisticsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvState, tvDate, tvDesc;

        public LogisticsViewHolder(View itemView) {
            super(itemView);
            tvState = itemView.findViewById(R.id.tv_state);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDesc = itemView.findViewById(R.id.tv_desc);
        }

        public TextView getTvState() {
            return tvState;
        }

        public TextView getTvDate() {
            return tvDate;
        }

        public TextView getTvDesc() {
            return tvDesc;
        }
    }
}
