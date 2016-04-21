package com.example.tester.new_2;

/**
 * Created by Administrator on 2016/4/10.
 */
public class DataEvent  {
    public static final int SUCC = 1;//成功
    public static final int HEADER = 2;//头
    public static final int FAIL = 3;//失败

    public int state;//状态
    public int error;
    public Object msg;//信息，例如HttpError，Map<String,String>Header
    public int id;//id，当前事件所属ID
}