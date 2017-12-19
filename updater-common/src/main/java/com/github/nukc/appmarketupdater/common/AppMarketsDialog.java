package com.github.nukc.appmarketupdater.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 选择要跳转哪个市场更新的 Dialog
 *
 * @author Nukc.
 */

public class AppMarketsDialog extends BottomSheetDialog {

    public AppMarketsDialog(@NonNull Context context, @NonNull List<AppMarket> appMarkets,
                            OnAppMarketSelectedListener listener) {
        this(context, 0, appMarkets, listener);
    }

    public AppMarketsDialog(@NonNull Context context, @StyleRes int theme, List<AppMarket> appMarkets,
                            final OnAppMarketSelectedListener listener) {
        super(context, theme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_app_markets, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        OnAppMarketSelectedListener mChooseListener = new OnAppMarketSelectedListener() {
            @Override
            public void onSelected(int position) {
                dismiss();
                listener.onSelected(position);
            }
        };
        recyclerView.setAdapter(new MarketsAdapter(appMarkets, mChooseListener));
        setContentView(view);
    }

    public interface OnAppMarketSelectedListener {
        void onSelected(int position);
    }

    private static class MarketsAdapter extends RecyclerView.Adapter<MarketsAdapter.MarketHolder> {

        private List<AppMarket> mAppMarkets;
        private OnAppMarketSelectedListener mListener;

        public MarketsAdapter(List<AppMarket> appMarkets, OnAppMarketSelectedListener listener) {
            mAppMarkets = appMarkets;
            mListener = listener;
        }

        @Override
        public MarketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_dialog_app_market, parent, false);
            return new MarketHolder(view, mListener);
        }

        @Override
        public void onBindViewHolder(MarketHolder holder, int position) {
            AppMarket appMarket = mAppMarkets.get(position);
            holder.mIvIcon.setImageDrawable(appMarket.icon);
            holder.mTvName.setText(appMarket.name);
        }

        @Override
        public int getItemCount() {
            return mAppMarkets.size();
        }

        static class MarketHolder extends RecyclerView.ViewHolder {
            private ImageView mIvIcon;
            private TextView mTvName;

            public MarketHolder(View itemView, final OnAppMarketSelectedListener listener) {
                super(itemView);

                mIvIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onSelected(getAdapterPosition());
                        }
                    }
                });
            }
        }
    }
}
