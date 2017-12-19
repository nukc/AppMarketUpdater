package com.github.nukc.appmarketupdater.kotlin

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.github.nukc.appmarketupdater.common.AppMarket
import com.github.nukc.appmarketupdater.common.AppMarketsDialog
import java.lang.ref.WeakReference

/**
 * @author Nukc.
 */
class AppMarketUpdater {

    companion object {
        private val TAG = "AppMarketUpdater"

        fun show(context: Context, appPkgName: String): AsyncTask<Void, Void, List<AppMarket>> {
            return GetAppMarketsTask(context, appPkgName).execute()
        }

        fun getAppMarkets(context: Context): List<AppMarket> {
            val intent = Intent()
            intent.action = "android.intent.action.MAIN"
            intent.addCategory("android.intent.category.APP_MARKET")
            val pm = context.packageManager
            val activityList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

            return activityList.map { AppMarket(context, it.activityInfo) }
        }

        fun startAppMarket(context: Context, marketPkgName: String, appPkgName: String) {
            val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPkgName))
            intent.`package` = marketPkgName
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Log.e(TAG, "打开该应用市场失败，请选择其他，谢谢~")
            }
        }
    }

    class GetAppMarketsTask(context: Context, appPkgName: String) : AsyncTask<Void, Void, List<AppMarket>>() {
        private var mContextWeakReference: WeakReference<Context> = WeakReference(context)
        private var mAppPkgName: String = appPkgName

        override fun doInBackground(vararg p0: Void?): List<AppMarket>? {
            val context = mContextWeakReference.get()
            return if (context != null) {
                getAppMarkets(context)
            } else {
                null
            }
        }

        override fun onPostExecute(appMarkets: List<AppMarket>?) {
            val context = mContextWeakReference.get()
            if (context != null && appMarkets != null && !isCancelled) {
                AppMarketsDialog(context, appMarkets,
                        AppMarketsDialog.OnAppMarketSelectedListener { position ->
                            val appMarket = appMarkets.get(position)
                            AppMarketUpdater.startAppMarket(context,
                                    appMarket.packageName, mAppPkgName)
                        }).show()
            }
        }

    }

}