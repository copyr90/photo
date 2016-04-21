package com.example.tester.new_2;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MyBaseAdapter<E> extends MyBaseFrameAdapter<E> {
    private Object mLock = new Object();
    private List<E> mItems = new ArrayList<E>();

    @Override
    public void setItems(List<E> items) {
        synchronized (mLock) {
            if (items != null) {
                mItems.clear();
                mItems.addAll(items);
            } else {
                if (mItems != null) {
                    mItems.clear();
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<E> items) {
        synchronized (mLock) {
            if (items != null && !items.isEmpty()) {
                if (mItems == null) {
                    mItems = new ArrayList<E>();
                }
                mItems.addAll(items);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void addItem(E item) {
        synchronized (mLock) {
            if (item != null) {
                if (mItems == null) {
                    mItems = new ArrayList<E>();
                }
                mItems.add(item);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public List<E> getItems() {
        return mItems;
    }

    @Override
    public void insert(E item, int index) {
        synchronized (mLock) {
            if (getItems() != null) {
                getItems().add(index, item);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void remove(E item) {
        synchronized (mLock) {
            if (getItems() != null) {
                getItems().remove(item);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void addItemInHead(E item) {
        synchronized (mLock) {
            if (item != null) {
                if (mItems == null) {
                    mItems = new ArrayList<E>();
                }
                mItems.add(0, item);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void addItemsInHead(List<E> items) {
        synchronized (mLock) {
            if (items != null && !items.isEmpty()) {
                if (mItems == null) {
                    mItems = new ArrayList<E>();
                }
                mItems.addAll(0, items);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public E getItem(int position) {
        if (position >= mItems.size())
            return null;
        return mItems != null ? mItems.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void clearItems() {
        if (mItems != null) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    public boolean isEmpty() {
        return null == mItems || mItems.size() == 0 ? true : false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
