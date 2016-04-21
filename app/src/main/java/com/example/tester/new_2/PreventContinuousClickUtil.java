package com.example.tester.new_2;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PreventContinuousClickUtil {
    private static long lastClickTime;

    public static boolean isContinuousClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < Constants.HALF_SECOND) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
