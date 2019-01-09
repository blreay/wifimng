package com.blreay.wifimng.util;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;
import java.sql.Date;
import java.text.SimpleDateFormat;


import java.sql.Date;
import java.text.SimpleDateFormat;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;


public class sms
{
    private Activity activity1;
    public int m;

    public sms(int paramInt)
    {
        this.m = paramInt;
    }

    public String getSmsInPhone(Activity paramActivity)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        this.activity1 = paramActivity;

        Cursor localCursor;
        String str1;
        int i2;
        String str2;
        int i3;
        String str3;

        for (;;)
        {
            try
            {

                /*
                Uri uri = Uri.parse("content://sms/");
                String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
                Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");      // 获取手机内部短信
                */

                //paramActivity = Uri.parse("content://sms/");
                Uri uri = Uri.parse("content://sms/");
                localCursor = this.activity1.managedQuery(uri, new String[] { "_id", "address", "person", "body", "date", "type" }, null, null, "date desc");
                if (!localCursor.moveToFirst()) {
                    continue;
                }
                int i = localCursor.getColumnIndex("address");
                int j = localCursor.getColumnIndex("person");
                int k = localCursor.getColumnIndex("body");
                int n = localCursor.getColumnIndex("date");
                int i1 = localCursor.getColumnIndex("type");
                str1 = localCursor.getString(i);
                i2 = localCursor.getInt(j);
                str2 = localCursor.getString(k);
                long l = localCursor.getLong(n);
                i3 = localCursor.getInt(i1);
                str3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(l));
                if (i3 != 1) {
                    continue;
                }
                //paramActivity = "接收";
            }
            catch (SQLiteException excep)
            {
                Log.d("SQLiteException in getSmsInPhone", excep.getMessage());
                continue;
            }
            localStringBuilder.append("[ ");
            localStringBuilder.append(str1 + ", ");
            localStringBuilder.append(i2 + ", ");
            localStringBuilder.append(str2 + ", ");
            localStringBuilder.append(str3 + ", ");
            localStringBuilder.append(paramActivity);
            localStringBuilder.append(" ]\n\n");
            if (!localCursor.moveToNext()) {
                if (!localCursor.isClosed()) {
                    localCursor.close();
                }
            }
            break;
        }
        for (;;)
        {
            localStringBuilder.append("getSmsInPhone has executed!");
            return localStringBuilder.toString();
        }
    }

    public String getWifiPWDFromSms(Activity paramActivity)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        this.activity1 = paramActivity;
        Cursor localCursor;
        String str1;
        int i2;
        String str2;
        int i3;
        String str3;

        for (;;)
        {
            try
            {
                Uri uri = Uri.parse("content://sms/");
                localCursor = this.activity1.managedQuery(uri, new String[] { "_id", "address", "person", "body", "date", "type" }, null, null, "date desc");
                if (!localCursor.moveToFirst()) {
                    continue;
                }
                int i = localCursor.getColumnIndex("address");
                int j = localCursor.getColumnIndex("person");
                int k = localCursor.getColumnIndex("body");
                int n = localCursor.getColumnIndex("date");
                int i1 = localCursor.getColumnIndex("type");
                str1 = localCursor.getString(i);
                i2 = localCursor.getInt(j);
                str2 = localCursor.getString(k);
                long l = localCursor.getLong(n);
                i3 = localCursor.getInt(i1);
                str3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(l));
                if (i3 != 1) {
                    continue;
                }
                //paramActivity = "接收";
            }
            catch (SQLiteException except)
            {
                Log.d("SQLiteException in getSmsInPhone", except.getMessage());
                continue;
            }
            localStringBuilder.append("[ ");
            localStringBuilder.append(str1 + ", ");
            localStringBuilder.append(i2 + ", ");
            localStringBuilder.append(str2 + ", ");
            localStringBuilder.append(str3 + ", ");
            localStringBuilder.append(paramActivity);
            localStringBuilder.append(" ]\n\n");
            if (!localCursor.moveToNext()) {
                if (!localCursor.isClosed()) {
                    localCursor.close();
                }
            }
            break;
        }
        for (;;)
        {
            localStringBuilder.append("getSmsInPhone has executed!");
            return localStringBuilder.toString();
        }
    }
}
