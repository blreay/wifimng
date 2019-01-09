package com.blreay.wifimng;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.blreay.wifimng.util.HttpUtils;
import com.blreay.wifimng.util.NotificationReceiver;
import com.blreay.wifimng.util.SystemUtils;
import com.blreay.wifimng.util.dbg;
import com.blreay.wifimng.util.WifiAdmin;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class foregroundService extends Service {
    public NotificationManager notificationManager;
    public Notification mNotification;
    public String mSSID = "clear-guest:service";

    public foregroundService() {
    }
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;

    private boolean isRemove=false;//是否需要移除

    public void createNotification(int idx) {
        dbg.out("idx=" + idx);
        android.content.Context mContext=this;
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int[] res = new int[]{R.drawable.myicon, R.mipmap.ic_launcher};
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent broadcastIntent = new Intent(mContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(res[idx])//设置状态栏里面的图标（小图标）
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))//下拉下拉列表里面的图标（大图标）
                .setTicker("Start monitor WIFI " + mSSID) //设置状态栏的显示的信息
                .setWhen(System.currentTimeMillis())//设置时间发生时间
                .setAutoCancel(false)//设置NO可以清除
                .setContentTitle("Monitor Wifi")//设置下拉列表里的标题
                .setContentText("this is SSID: " + mSSID)//设置上下文内容
                .setOngoing(true);
        //.setColor(Color.BLUE);
        dbg.out("showNotification");
        mNotification = builder.build();

        //following work correctly,but this app is treated as "background", may be kill be system
        //notificationManager.notify(0, mNotification);

        // /test how to set app as foregraound.
        //notificationManager.notify(0, mNotification);
        startForeground(1, mNotification);
    }

    /**
     * Notification
     */
    /*
    public void createNotification(){
        //使用兼容版本
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //设置通知栏横条的图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.screenflash_logo));
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(false);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle("I am Foreground Service!!!");
        //创建通知
        Notification notification = builder.build();
        //设置为前台服务
        startForeground(NOTIFICATION_DOWNLOAD_PROGRESS_ID,notification);
    }
    */


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i=intent.getExtras().getInt("cmd");
        if(i==0){
            if(!isRemove) {
                createNotification(1);
            }
            isRemove=true;
        }else {
            //移除前台服务
            if (isRemove) {
                stopForeground(true);
            }
            isRemove=false;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //移除前台服务
        if (isRemove) {
            stopForeground(true);
        }
        isRemove=false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
