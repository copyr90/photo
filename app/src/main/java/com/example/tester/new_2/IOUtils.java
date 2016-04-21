package com.example.tester.new_2;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2016/4/10.
 */
public class IOUtils {
    public static void closeCloseable(Closeable obj) {
        try {
            // 修复小米MI2的JarFile没有实现Closeable导致崩溃问题
            if (obj instanceof Closeable && obj != null)
                obj.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}