# AppMarketUpdater

通过应用市场更新 app（也就是打开指定包名的详情页面）。

<img src="https://raw.githubusercontent.com/nukc/AppMarketUpdater/master/art/dialog.jpg">

依赖其中一个即可：
```groovy
    implementation 'com.github.nukc.appmarketupdater:updater:0.0.2'
    implementation 'com.github.nukc.appmarketupdater:updater-rxjava2:0.0.2'
    implementation 'com.github.nukc.appmarketupdater:updater-kotlin:0.0.2'
```

## 用法

[Sample](https://github.com/nukc/AppMarketUpdater/blob/master/app/src/main/java/com/github/nukc/appmarketupdater/sample/MainActivity.kt)

```java

    AppMarketUpdater.show(context, packageName);
```

## 分析

```java

    private static List<ResolveInfo> queryAppMarkets(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        return pm.queryIntentActivities(intent, 0);
    }
```

通过这个方法是不能匹配到手机上所有的应用市场，为什么呢？来看下面这几个 activity 清单信息：

这个是可以匹配到的。
```xml
        <activity
            android:theme="@ref/0x7f0b00b5"
            android:name="com.xx.xx.xx.xx.MainActivity"
            android:configChanges="0x6a0">

            <intent-filter>

                <action
                    android:name="android.intent.action.MAIN" />

                <category
                    android:name="android.intent.category.LAUNCHER" />

                <category
                    android:name="android.intent.category.MULTIWINDOW_LAUNCHER" />

                <category
                    android:name="android.intent.category.APP_MARKET" />

                <category
                    android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <intent-filter>

                ......
            </intent-filter>

            <meta-data
                android:name="android.app.shortcuts"
                android:resource="@ref/0x7f080006" />
        </activity>

```

这个是不能匹配到的。
```xml

        <activity
            android:theme="@ref/0x7f0a003c"
            android:name="com.xx.activity.xxActivity"
            android:launchMode="1"
            android:screenOrientation="1"
            android:configChanges="0x4a0"
            android:windowSoftInputMode="0x10">

            <intent-filter>

                <action
                    android:name="android.intent.action.MAIN" />

                <category
                    android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <intent-filter>

                <category
                    android:name="android.intent.category.DEFAULT" />

                <category
                    android:name="android.intent.category.APP_MARKET" />
            </intent-filter>

            <intent-filter>

                <action
                    android:name="android.intent.action.VIEW" />

                <category
                    android:name="android.intent.category.DEFAULT" />

                <data
                    android:scheme="market"
                    android:host="details"
                    android:path="@string/0x27" />
            </intent-filter>
        </activity>

```

不难发现，android.intent.category.APP_MARKET 所在的 intent-filter 是有些许不一样的，匹配不到的缺少了
android.intent.action.MAIN（把 android.intent.category.APP_MARKET 放在了一个没有 action 的 intent-filter 里）

之后我查看了另一个匹配不到的某手机助手：
以前的版本能匹配到的：
```xml
        <activity
            android:theme="@ref/0x7f0b0059"
            android:name="com.xx.LauncherActivity"
            android:exported="true"
            android:screenOrientation="1"
            android:alwaysRetainTaskState="true">

            <intent-filter>

                <action
                    android:name="android.intent.action.MAIN" />

                <category
                    android:name="android.intent.category.LAUNCHER" />

                <category
                    android:name="android.intent.category.APP_MARKET" />

                <category
                    android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
```

新版，不能匹配到：
```xml
        <activity
            android:theme="@ref/0x7f0b005c"
            android:name="com.xx.LauncherActivity"
            android:exported="true"
            android:screenOrientation="1">

            <intent-filter>

                <category
                    android:name="android.intent.category.APP_MARKET" />

                <category
                    android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
```

没有 action 是没有问题的，但是无法匹配到。

If the filter does not list any actions, there is nothing for an intent to match, so all intents fail the test.
However, if an Intent does not specify an action, it passes the test as long as the filter contains at least one action

还是直接看文档吧 [intents-filters.html#ActionTest](https://developer.android.com/guide/components/intents-filters.html#ActionTest)
如果是开发应用市场的同学看到了，可以注意一下。


## License

    The MIT License (MIT)

    Copyright (c) 2017 Nukc

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.