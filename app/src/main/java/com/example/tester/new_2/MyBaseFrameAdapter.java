package com.example.tester.new_2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
abstract public class MyBaseFrameAdapter<E> extends BaseAdapter {
    /* 设置ListView的Items数据 */
    public abstract void setItems(List<E> items);

    /* 追加ListView的Items数据 */
    public abstract void addItems(List<E> items);

    /* 追加某个ListView的Item数据 */
    public abstract void addItem(E item);

    /* 在某个ListView顶部增加Items数据 */
    public  void addItemsInHead(List<E> items){}

    /* 在某个ListView顶部增加Item数据 */
    public  void addItemInHead(E item){}

    /* 获取ListView的Items数据 */
    public abstract List<E> getItems();

    /* 往Listview插入一条数据 */
    public void insert(E item, int index) {}

    /* 删除Listview的一条数据 */
    public void remove(E item) {}

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public abstract E getItem(int position);

    @Override
    public abstract long getItemId(int position);

    /* 清空ListView的Items数据 */
    public abstract void clearItems();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
