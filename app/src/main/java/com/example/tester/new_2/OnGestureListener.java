package com.example.tester.new_2;

/**
 * Created by Administrator on 2016/4/10.
 */
public interface OnGestureListener {

    public void onDrag(float dx, float dy);

    public void onFling(float startX, float startY, float velocityX,
                        float velocityY);

    public void onScale(float scaleFactor, float focusX, float focusY);

}
