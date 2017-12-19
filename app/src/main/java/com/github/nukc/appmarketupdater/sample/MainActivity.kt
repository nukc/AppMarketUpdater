package com.github.nukc.appmarketupdater.sample

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.nukc.appmarketupdater.common.AppMarket
import com.github.nukc.appmarketupdater.rxjava2.AppMarketUpdater
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private var mCompositeDisposable = CompositeDisposable()
    private var mJavaTask: AsyncTask<Any, Any, Any>? = null
    private var mKotlinTask: AsyncTask<Void, Void, List<AppMarket>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val et = findViewById<EditText>(R.id.et_package_name)
        fun verifyPackageName(onVerified: (packageName: String) -> Unit) {
            val packageName = et.text.toString()
            if (packageName.indexOf(".") > 0) {
                onVerified(packageName)
            } else {
                Toast.makeText(this, "Please enter the package name~", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_open).setOnClickListener({
            verifyPackageName { packageName ->
                mJavaTask = com.github.nuck.appmarketupdater.AppMarketUpdater.show(this, packageName)
            }
        })

        findViewById<Button>(R.id.btn_open_rxjava2).setOnClickListener({
            verifyPackageName { packageName ->
                val disposable = AppMarketUpdater.show(this, packageName)
                mCompositeDisposable.add(disposable)
            }
        })

        findViewById<Button>(R.id.btn_open_kotlin).setOnClickListener {
            verifyPackageName { packageName ->
                mKotlinTask = com.github.nukc.appmarketupdater.kotlin.AppMarketUpdater.show(this, packageName)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.dispose()
        if (mJavaTask != null) {
            mJavaTask?.cancel(true)
        }
        if (mKotlinTask != null) {
            mKotlinTask?.cancel(true)
        }
    }
}
