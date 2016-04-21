package com.example.tester.new_2;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/10.
 */
public class StringUtils {

    public static final String VERSION_SEPERATOR = ".";

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private final static Pattern mobiler = Pattern
            .compile("^(13|14|15|17|18)\\d{9}$");

    private final static Pattern chineseName = Pattern.compile("^[a-zA-Z\\u4e00-\\u9fa5]{2,16}$");

    private final static Pattern password = Pattern.compile("^[a-zA-Z0-9~!@#$%^&*()_+-=`;:',.<>/\\\\|?\"\\{}\\]\\[]{6,20}$");

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim());
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是不是一个合法的手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (mobile == null || mobile.trim().length() == 0)
            return false;
        return mobiler.matcher(mobile).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转长整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 对象转小数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception e) {
        }
        return 0.0;
    }

    /**
     * 对象转浮点数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static float toFloat(String obj) {
        try {
            return Float.parseFloat(obj);
        } catch (Exception e) {
        }
        return 0.0f;
    }

    /**
     * 如果含小数位 输出小数位，否则去掉小数位
     *
     * @param price
     * @return
     */
    public static String getPrettyPrice(float price) {
        int intPrice = (int) price;
        if ((price - intPrice) == 0f) {
            return String.valueOf(intPrice);
        }
        return String.valueOf(price);
    }

    public static List<String> stringToList(String str, String seperator) {
        List<String> itemList = new ArrayList<String>();
        if (isEmpty(str)) {
            return itemList;
        }
        StringTokenizer st = new StringTokenizer(str, seperator);
        while (st.hasMoreTokens()) {
            itemList.add(st.nextToken());
        }

        return itemList;
    }

    /**
     * 去str中的空格
     *
     * @param str
     * @return
     */
    public static String getNoSpaceStr(String str) {
        if (str.contains(" ")) {
            str.replace(" ", "");
        }
        return str;
    }

    /**
     * 在url上添加参数
     */
    public static String appendParam(String url, String param, String value) {
        //hotfix for old version
        Pattern p = Pattern.compile(".*token=$");
        Matcher matcher = p.matcher(url);
        if (matcher.matches() && "token".equals(param)) {
            return url + value;
        }

        //normal start here
        StringBuilder result = new StringBuilder(url);
        if (url.contains("?")) result.append("&");
        else result.append("?");
        result.append(param).append("=").append(value);
        return result.toString();
    }

    /**
     * 检测路径是否为正常的图片路径
     *
     * @param path
     * @return
     */
    public static boolean isNormalImagePath(String path) {
        String extension = path.substring(path.lastIndexOf('.'));
        boolean ret = extension != null && (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".png"));
        return ret;
    }
}
