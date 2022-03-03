package com.viomi.ovensocommon.utils;

/**
 * @description:
 * @data:2021/9/16
 */
public class CommonStringUtils {
    public static final int MAX_SSID_LENGTH = 18;
    /**
     * 从字符串中裁切指定长度（中文算两个），大于指定则末尾补...  比如（123abc安博）是 10 个
     *
     * @param srcStr 源字符串
     * @param length 指定的长度 字符商都
     * @return
     */
    public static String getFixCharString(String srcStr, int length) {
        if (srcStr.length() <= length / 2) {
            return srcStr;
        }
        int totalLength = 0;//总长度
        int index = 0;//第几个字符
        String destStr = "";
        while (totalLength < length) {//计算出来的length小于
            if (index + 1 > srcStr.length()) {//已经切到最后了则退出
                break;
            }
            String temp = srcStr.substring(index, index + 1);
            if (totalLength + getStrlength(temp) > length) {//加上上面的totalLength大于设定的了也退出
                break;
            }
            totalLength += getStrlength(temp);
            index++;
            destStr = destStr + temp;
        }
        //上一步循环会获得length或者length-1个长度，需要进一步处理增加。。。
        if (index + 1 <= srcStr.length()) {
            if (totalLength < length) {
                return destStr + "...";
            } else {
                return destStr.substring(0, destStr.length() - 1) + "...";
            }
        } else {
            return destStr;
        }
    }

    /**
     * 获取长度，中文字符返回2，英文返回1
     *
     * @param srcString
     */
    private static int getStrlength(String srcString) {
        return srcString.matches("[\u0391-\uFFE5]") ? 2 : 1;
    }


}
