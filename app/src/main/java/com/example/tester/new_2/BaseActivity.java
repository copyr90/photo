package com.example.tester.new_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;

import com.nostra13.universalimageloader.utils.L;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by Administrator on 2016/4/10.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    /* 用于该Activity的Message Box, 延迟实例化 */
    private CommAlertDialog mAlertDialog;

    public String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d(getName());
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        L.d(getName());
        super.onPostCreate(savedInstanceState);
        initial();

    }

    protected abstract int getLayoutResourceId();

    protected abstract void initial();

    @Override
    public void onStart() {
        super.onStart();
        L.d(getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        L.d(getName());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        L.d(getName());
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        L.d(getName());
        super.onRestart();
    }

    @Override
    protected void onResume() {
        L.d(getName());
        super.onResume();
    }

    @Override
    protected void onPause() {
        L.d(getName());
        super.onPause();
    }

    @Override
    public void onStop() {
        L.d(getName());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        L.d(getName());
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_right_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (PreventContinuousClickUtil.isContinuousClick()) {
                return false;
            }
            dismiss();
            return true;
        }
        return true;
    }

    /* 调用系统默认返回按钮 */
    protected void dismiss() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (PreventContinuousClickUtil.isContinuousClick()) {
            return;
        }
    }

    @Subscribe
    public void onEventMainThread(LoginSuccEvent event){

    }

    public synchronized CommAlertDialog getAlertDialog() {
        if (mAlertDialog == null) {
            synchronized (BaseActivity.class) {
                if (mAlertDialog == null) {
                    mAlertDialog = new CommAlertDialog(this,R.style.TransparentDialog);
                }
            }
        }
        return mAlertDialog;
    }
}