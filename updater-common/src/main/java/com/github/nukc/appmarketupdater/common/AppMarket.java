package com.github.nukc.appmarketupdater.common;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * App 应用市场
 * @author Nukc.
 */

public class AppMarket {
    public String packageName;
    public String path;
    public String name;
    public Drawable icon;

    public AppMarket(Context context, ActivityInfo activityInfo) {
        packageName = activityInfo.packageName;
        ApplicationInfo appInfo = activityInfo.applicationInfo;
        path = appInfo.publicSourceDir;
        PackageManager pm = context.getPackageManager();
        try {
            CharSequence sequence = appInfo.loadLabel(pm);
            if (sequence != null) {
                name = sequence.toString();
            }
            icon = appInfo.loadIcon(pm);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
