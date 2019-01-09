package com.blreay.wifimng.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.blreay.wifimng.MainActivity;

import java.util.List;

/**
 * Created by zhaozhan on 2017/4/20.
 */

public class SystemUtils {
    /**
     * 判断应用是否已经启动
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            dbg.out(String.format("[%d]: %s", i, processInfos.get(i).processName));
            if (processInfos.get(i).processName.equals(packageName)) {
                dbg.out(String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        dbg.out(String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }

    public static void startDetailActivity(Context context, String name, String price,
                                           String detail) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("price", price);
        intent.putExtra("detail", detail);
        context.startActivity(intent);
    }

}
