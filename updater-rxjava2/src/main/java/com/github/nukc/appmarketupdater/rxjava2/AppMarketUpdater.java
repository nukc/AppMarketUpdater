package com.github.nukc.appmarketupdater.rxjava2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.nukc.appmarketupdater.common.AppMarket;
import com.github.nukc.appmarketupdater.common.AppMarketsDialog;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
     * @return Disposable
     */
    public static Disposable show(@NonNull final Context context, @NonNull final String appPkgName) {
        return getAppMarkets(context)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AppMarket>>() {
                    @Override
                    public void accept(final List<AppMarket> appMarkets) throws Exception {
                        new AppMarketsDialog(context, appMarkets,
                                new AppMarketsDialog.OnAppMarketSelectedListener() {
                                    @Override
                                    public void onSelected(int position) {
                                        AppMarket appMarket = appMarkets.get(position);
                                        AppMarketUpdater.startAppMarket(context,
                                                appMarket.packageName, appPkgName);
                                    }
                                }).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
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
     * 获取 {@link AppMarket}
     */
    public static Observable<AppMarket> getAppMarkets(final Context context) {
        List<ResolveInfo> activityList = queryAppMarkets(context);
        return Observable.fromIterable(activityList)
                .map(new Function<ResolveInfo, AppMarket>() {
                    @Override
                    public AppMarket apply(ResolveInfo resolveInfo) throws Exception {
                        return new AppMarket(context, resolveInfo.activityInfo);
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
