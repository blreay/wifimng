package com.blreay.wifimng.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaozhan on 2017/4/19.
 */

public class dbg {
    public static int debug = 1;
    public static String TAG = "zzy";
    public static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showmsg(Context ctx, String msg) {
        if (debug == 1) {
            Toast toast=Toast.makeText(ctx.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
            Log.d(TAG, getFileLineMethod() + msg);
        }
    }
    public static void toast(Context ctx, String msg) {
        if (debug == 1) {
            //Toast toast=Toast.makeText(ctx.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            if (toast != null)            {
                Log.d(TAG, getFileLineMethod() + "reuse the existing toast");
                toast.setText(msg);
                //toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.d(TAG, getFileLineMethod() + "create new toast");
                toast = Toast.makeText(ctx.getApplicationContext(), msg, Toast.LENGTH_SHORT);
                //toast = Toast.makeText(ctx.getApplicationContext(), msg, 2);
                toast.show();
                //oneTime=System.currentTimeMillis();
            }
            //Toast toast=Toast.makeText(ctx.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            //toast.show();
            Log.d(TAG, getFileLineMethod() + msg);
        }
    }
    public static void out(String paramString) {
        if (debug == 1) {
            System.out.println(paramString);
            Log.d(TAG, getFileLineMethod() + paramString);
        }
    }
    public static void out(String tag, String paramString) {
        if (debug == 1) {
            System.out.println(paramString);
            Log.d(tag, getFileLineMethod() + paramString);
        }
    }

    public static String getFileLineMethod() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("]");
        return toStringBuffer.toString();
    }

    // 当前文件名
    public static String _FILE_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getFileName();
    }

    // 当前方法名
    public static String _FUNC_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getMethodName();
    }

    // 当前行号
    public static int _LINE_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getLineNumber();
    }

    // 当前时间
    public static String _TIME_() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(now);
    }
}
