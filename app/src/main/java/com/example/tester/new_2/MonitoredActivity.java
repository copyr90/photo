package com.example.tester.new_2;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/10.
 */
public abstract class MonitoredActivity extends BaseActivity {

    private final ArrayList<LifeCycleListener> mListeners =
            new ArrayList<LifeCycleListener>();

    public interface LifeCycleListener {

        void onActivityCreated(MonitoredActivity activity);

        void onActivityDestroyed(MonitoredActivity activity);

        void onActivityPaused(MonitoredActivity activity);

        void onActivityResumed(MonitoredActivity activity);

        void onActivityStarted(MonitoredActivity activity);

        void onActivityStopped(MonitoredActivity activity);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {

        public void onActivityCreated(MonitoredActivity activity) {

        }

        public void onActivityDestroyed(MonitoredActivity activity) {

        }

        public void onActivityPaused(MonitoredActivity activity) {

        }

        public void onActivityResumed(MonitoredActivity activity) {

        }

        public void onActivityStarted(MonitoredActivity activity) {

        }

        public void onActivityStopped(MonitoredActivity activity) {

        }
    }

    public void addLifeCycleListener(LifeCycleListener listener) {

        if (mListeners.contains(listener)) return;
        mListeners.add(listener);
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {

        mListeners.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityCreated(this);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityDestroyed(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStarted(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStopped(this);
        }
    }

}