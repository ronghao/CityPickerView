package com.haohaohu.citypickerview;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 读取城市信息
 *
 * @author haohao(ronghao3508@gmail.com) on 2017/10/13 下午 02:37
 * @version v1.0
 */
public class CityJsonReadUtil {

    /**
     * 获取地址信息
     * 
     * @author ronghao3508@gmail.com 
     * @date 2017/10/16 
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf =
                    new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
