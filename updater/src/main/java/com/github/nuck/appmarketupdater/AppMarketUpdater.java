package com.github.nuck.appmarketupdater;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.nukc.appmarketupdater.common.AppMarket;
import com.github.nukc.appmarketupdater.common.AppMarketsDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nukc.
 */

public class AppMarketUpdater {

    private static final String TAG = AppMarketUpdater.class.getSimpleName();

    /**
     * 显示可选择的市场列表 dialog
     *
     * @param context    Context
     * @param appPkgName 需要更新的 app 包名
     */
    public static AsyncTask show(final Context context, final String appPkgName) {
        return new GetAppMarketsTask(context, appPkgName).execute();
    }

    /**
     * 查询 App 应用市场
     */
    private static List<ResolveInfo> queryAppMarkets(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        return pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    /**
     * 打开 app 应用市场，到具体的 app 详情页，id= 填写具体的 app 包名
     *
     * @param context       {@link Context}
     * @param marketPkgName app 应用市场的包名
     */
    public static void startAppMarket(Context context, String marketPkgName, String appPkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appPkgName));
        intent.setPackage(marketPkgName);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "打开该应用市场失败，请选择其他，谢谢~");
        }
    }


    /**
     * 获取 AppMarket
     *
     * @param context {@link Context}
     * @return List
     */
    public static List<AppMarket> getAppMarkets(final Context context) {
        List<ResolveInfo> activityList = queryAppMarkets(context);
        List<AppMarket> appMarkets = new ArrayList<>();
        for (ResolveInfo resolveInfo : activityList) {
            AppMarket appMarket = new AppMarket(context, resolveInfo.activityInfo);
            appMarkets.add(appMarket);
        }
        return appMarkets;
    }

    static class GetAppMarketsTask extends AsyncTask<Void, Void, List<AppMarket>> {

        private WeakReference<Context> mContextWeakReference;
        private String mAppPkgName;

        GetAppMarketsTask(Context context, String appPkgName) {
            mContextWeakReference = new WeakReference<>(context);
            mAppPkgName = appPkgName;
        }

        @Override
        protected List<AppMarket> doInBackground(Void... voids) {
            Context context = mContextWeakReference.get();
            if (context != null) {
                return getAppMarkets(context);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<AppMarket> appMarkets) {
            final Context context = mContextWeakReference.get();
            if (context != null && appMarkets != null && !isCancelled()) {
                new AppMarketsDialog(context, appMarkets,
                        new AppMarketsDialog.OnAppMarketSelectedListener() {
                            @Override
                            public void onSelected(int position) {
                                AppMarket appMarket = appMarkets.get(position);
                                AppMarketUpdater.startAppMarket(context,
                                        appMarket.packageName, mAppPkgName);
                            }
                        }).show();
            }
        }
    }
}
