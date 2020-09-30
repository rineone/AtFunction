package com.rine.atfunction;

/**
 * @author rine
 * @date:2020/9/30
 * 方法类
 */
public class StringAtUtil {

    /**
     * int转换
     * @param intStr
     * @return
     */
    public static int getInt(String intStr,int def){
        int res = def;
        try {
            res = Integer.parseInt(intStr);
        }catch (Exception e){
        }
        return res;
    }

}
