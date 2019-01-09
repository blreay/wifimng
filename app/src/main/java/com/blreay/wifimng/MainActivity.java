package com.blreay.wifimng;

import com.blreay.wifimng.util.HttpUtils;
import com.blreay.wifimng.util.NotificationReceiver;
import com.blreay.wifimng.util.SystemUtils;
import com.blreay.wifimng.util.dbg;
import com.blreay.wifimng.util.WifiAdmin;
import com.blreay.wifimng.util.mobileDataConn;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.os.PowerManager;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.Notification.*;
import android.app.PendingIntent;
import android.graphics.*;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.io.PrintStream;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;


public class MainActivity extends AppCompatActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    //public class MainActivity extends AppCompatActivity  implements OnClickListener{
    private boolean _isExe = false;
    private boolean _Doing = false;
    private static boolean _Checking = false;
    public int debug = 1;
    Button g_btn_login;
    private ImageView image = null;
    private TextView info = null;
    public SimpleCursorAdapter mAdapter;
    public Context mContext = null;
    private Bitmap mDownloadImage = null;
    private String mStrbody = "";
    private WifiAdmin mWifiAdmin;
    public String pwd = "NOVAL";
    public StringBuilder smsBuilder = new StringBuilder();
    private downloadImageTask task;
    private TextView txt = null;
    public String mSSID = "clear-guest";
    public String mSSID_Direct = "clear-internet24";
    public NotificationManager notificationManager;
    public final static String NEWS_LISTEN = "broadcast";
    public Notification mNotification;
    public TimerTask task_check;
    public mobileDataConn mdataconn;
    private Timer mTimer;

    /*
    public final Handler mhander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, "你好", Toast.LENGTH_LONG).show();
        }
    };
    */
    protected void ssid_save()
    {
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ssid_conn", ((EditText)findViewById(R.id.txt_ssid)).getText().toString());
        //editor.putString("password", ((EditText)findViewById(R.id.password)).getText().toString());
        // editor.putBoolean()、editor.putInt()、editor.putFloat()……
        info.append("SSID is saved:" +  ((EditText)findViewById(R.id.txt_ssid)).getText().toString() + "\n");
        editor.commit();
    }

    protected String ssid_load()
    {
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        String ssid = sp.getString("ssid_conn", ""); // 第二个参数为默认值
        //String password = sp.getString("password", ""); // 第二个参数为默认值
        // sp.getBoolean()、sp.getInt()、sp.getFloat()……
        ((EditText)findViewById(R.id.txt_ssid)).setText(ssid);
        info.append("SSID is loaded:" +  ssid + "\n");
        return ssid;
        //((EditText)findViewById(R.id.password)).setText(password);
    }

    // enable data connection can't work, need more investigation
    public void switchDataConn(boolean enabled) {
        ConnectivityManager conManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class[] getArgArray = null;
        Class[] setArgArray = new Class[] {boolean.class};
        Object[] getArgInvoke = null;

        Class<?> c = conManager.getClass();
        Method mGetMethod;
        Method mSetMethod;

        try {
             mGetMethod = c.getMethod("getMobileDataEnabled", getArgArray);
             mSetMethod = c.getMethod("setMobileDataEnabled", setArgArray);
            boolean isOpen = (Boolean) mGetMethod.invoke(conManager, getArgInvoke);
            if (isOpen) {
                info.append("Data connection is enabled \n");
                mSetMethod.invoke(conManager, false);
                info.append("Data connection is switched to disable \n");
            } else {
                info.append("Data connection is disabled \n");
                mSetMethod.invoke(conManager, true);
                info.append("Data connection is switched to enable \n");
            }
        } catch (Exception e) {
            //info.append("exception: " + e.toString() + "\n" + e.getLocalizedMessage() + "\n" + mGetMethod.getName());
            info.append("exception: " + e.toString() + "\n" + e.getLocalizedMessage() + "\n");
            e.printStackTrace();
        }
    }

    // 移动数据开启和关闭, 开启不能工作，需要继续调查，好像跟编译用的SDK版本有关系。
    public void setMobileDataStatus(boolean enabled)  {
        ConnectivityManager conMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //ConnectivityManager类
        Class<?> conMgrClass = null;
        //ConnectivityManager类中的字段
        Field iConMgrField = null;
        //IConnectivityManager类的引用
        Object iConMgr = null;
        //IConnectivityManager类
        Class<?> iConMgrClass = null;
        //setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try{
            //取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            //取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            //设置mService可访问
            iConMgrField.setAccessible(true);
            //取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            //取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());

            //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

            //设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            //调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
            //setMobileDataEnabledMethod.invoke(conMgr, enabled);
        }catch(ClassNotFoundException e)  {
            info.append(e.toString());
            e.printStackTrace();
        }catch(NoSuchFieldException e)    {
            e.printStackTrace();
            info.append(e.toString());
        }catch(SecurityException e){
            e.printStackTrace();
            info.append(e.toString());
        }catch(NoSuchMethodException e){
            e.printStackTrace();
            info.append(e.toString());
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            info.append(e.toString());
        }catch(IllegalAccessException e){
            e.printStackTrace();
            info.append(e.toString());
        }catch(InvocationTargetException e){
            e.printStackTrace();
            info.append(e.toString());
        }
    }
    //获取移动数据开关状态
    public boolean getMobileDataStatus(String getMobileDataEnabled){
        ConnectivityManager cm;
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = cm.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;
        Boolean isOpen = false;
        try{
            Method method = cmClass.getMethod(getMobileDataEnabled, argClasses);
            isOpen = (Boolean)method.invoke(cm, argObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        return isOpen;
    }

    private void initWidgets() {
        dbg.out("zzy", "initWidgets");
        image = (ImageView) findViewById(R.id.img);
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_ssid).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_hide).setOnClickListener(this);
        this.info = ((TextView) findViewById(R.id.info2));
        //this.info.setMovementMethod(ScrollingMovementMethod.getInstance());
        ((TextView) findViewById(R.id.info2)).setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void runHttpsConnection(Map<String, String> paramMap) {
        new CreateHttpsConnTask().execute(new Map[]{paramMap});
    }

    public void infoappend(String msg) {
        info.append(msg + "\n");
        int offset = info.getLineCount() * info.getLineHeight();
        if (offset > info.getHeight()) {
            info.scrollTo(0, offset - info.getHeight());
        }
    }

    public void infoset(String msg) {
        info.setText(msg + "\n");
        info.scrollTo(0,0);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        dbg.out("onKeyDown, will convert to HOME key");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView tv = new TextView(this);
        //tv.setText("Hello World,zzy");


        //setContentView(tv);
        dbg.out("begin");
        mContext = this;
        initWidgets();

        this.g_btn_login = ((Button) findViewById(R.id.btn_login));
        txt = (TextView) findViewById(R.id.info);

        // read SSID
        if ( "" == ssid_load()) {
            // initialize config
            ssid_save();
        }
        mSSID_Direct=ssid_load();
        this.txt.setText("Keep WIFI connected to the specified SSID");

        mWifiAdmin = new WifiAdmin(this.mContext) {
            public Intent myRegisterReceiver(BroadcastReceiver paramAnonymousBroadcastReceiver, IntentFilter paramAnonymousIntentFilter) {
                MainActivity.this.registerReceiver(paramAnonymousBroadcastReceiver, paramAnonymousIntentFilter);
                return null;
            }

            public void myUnregisterReceiver(BroadcastReceiver paramAnonymousBroadcastReceiver) {
                MainActivity.this.unregisterReceiver(paramAnonymousBroadcastReceiver);
            }

            public void onNotifyWifiConnectFailed() {
                Log.v("WIFI", "have connected failed!");
                Log.v("WIFI", "###############################");
            }

            public void onNotifyWifiConnected() {
                Log.v("WIFI", "have connected success!");
                Log.v("WIFI", "###############################");
            }
        };

        task = new downloadImageTask();

        // init timer
        mTimer = new Timer();

        showNotification4(1);
        ScreenListener l = new ScreenListener(this);
        l.begin(new ScreenStateListener() {
            @Override
            public void onUserPresent() {
                dbg.out("onUserPresent");
                //dologin();
            }

            @Override
            public void onScreenOn() {
                dbg.out("onScreenOn");
                dologin();
            }

            @Override
            public void onScreenOff() {
                dbg.out("onScreenOff");
            }

            @Override
            public void onNetworkChange() {

                dbg.out("onNetworkChange");
                dologin();
            }
        });

        getLoaderManager().initLoader(0, null, this);
        runOnUiThread(new Runnable() {
            public void run() {
            }
        });
    }

    private void setTimerTask(int delay, int period) {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, delay, period);
        //}, 1000*5, 1000*60*5/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }
    private void cancelTimerTask() {
        mTimer.cancel();
    }

    /**
     * do some action
     */
    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    // do some action
                    dbg.showmsg(mContext, "Timer: check wifi connection");
                    int ret = 0;
                    // can't call following function here, otherwise app crash, maybe caused by internet http requet
                    //ret = test_internet("http://www.baidu.com", "百度");

                    infoappend(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(System.currentTimeMillis()) + " Timer: check wifi connection");
                    //new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(System.currentTimeMillis()) + " " + ((new Exception()).getStackTrace())[1].getMethodName() + "\n"
                    g_btn_login.performClick();
                    if (0 == ret) {
                        // internet is ready, don't login again
                        //dbg.showmsg(mContext, "Internet is ready, don't login again");
                    } else {
                        //dbg.showmsg(mContext, "Internet is NOT ready, don login");
                        //dologin();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        //mWifiAdmin.closeWifi();
        infoappend(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(System.currentTimeMillis()) + " onDestroy called");
        cancelTimerTask();
        //mTimer.cancel();
    }

    public Loader<Cursor> onCreateLoader(int paramInt, Bundle paramBundle) {
        //paramBundle = android.provider.ContactsContract.Contacts.CONTENT_URI;
        //new StringBuilder();
        return new CursorLoader(this, Uri.parse("content://sms/"), new String[]{"_id", "address", "person", "body", "date", "type"}, "body LIKE '%zzy%test%'", null, "date desc");
    }

    public void onLoadFinished(Loader<Cursor> paramLoader1, Cursor paramCursor) {
        //paramLoader = "";
        Object localObject = "";
        String paramLoader4 = "";
        int i;
        if (paramCursor.moveToFirst()) {
            int m = paramCursor.getColumnIndex("address");
            int n = paramCursor.getColumnIndex("person");
            i = paramCursor.getColumnIndex("body");
            int j = paramCursor.getColumnIndex("date");
            int k = paramCursor.getColumnIndex("type");
            localObject = paramCursor.getString(m);
            m = paramCursor.getInt(n);
            String str3 = paramCursor.getString(i);
            long l = paramCursor.getLong(j);
            i = paramCursor.getInt(k);
            SimpleDateFormat paramLoader = new SimpleDateFormat("yyyy-MM-dd");
            String str1 = paramLoader.format(new java.sql.Date(l));
            String str2 = paramLoader.format(new java.util.Date());
            if (i == 1) {
                //paramLoader = "接收";
                this.smsBuilder.append("[ ");
                this.smsBuilder.append(localObject + ", ");
                this.smsBuilder.append(m + ", ");
                this.smsBuilder.append(str3 + ", ");
                this.smsBuilder.append(str1 + ", ");
                this.smsBuilder.append(paramLoader);
                this.smsBuilder.append(" ]\n\n");
                Matcher paramLoader2 = Pattern.compile("[\\s\\S]*正文：(.+)<完>[\\s\\S]*").matcher(str3);
                if (paramLoader2.find()) {
                    paramLoader2.group(0);
                    this.pwd = paramLoader2.group(1);
                }
                paramLoader4 = str1;
                localObject = str2;
                if (!paramCursor.isClosed()) {
                    paramCursor.close();
                    localObject = str2;
                    paramLoader4 = str1;
                }
            }
        }
        for (; ; ) {
            this.smsBuilder.append("getSmsInPhone has executed!");
            if (this.pwd.equals("NOVAL")) {
                // cann't find password
            } else {
                this.txt.setText(this.pwd + " [" + paramLoader4 + "] [" + (String) localObject + "]");
                ((ClipboardManager) getSystemService("clipboard")).setText(this.pwd);
            }

            this.g_btn_login.performClick();
            setTimerTask(1000*5, 1000*60*13);  /* 表示1000毫秒之後，每隔5 minutes 毫秒執行一次 */
            return;
        }
    }

    public void onLoaderReset(Loader<Cursor> paramLoader) {
        this.mAdapter.swapCursor(null);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        dbg.out("onStop is called.");
        if (_isExe) {
            task.cancel(true); // 取消操作
        }
        super.onStop();
    }

    public void dbg(String paramString) {
        this.debug = 1;
        if (this.debug == 1) {
            System.out.println(paramString);
            Log.e("zzy", paramString);
        }
    }


    void dologin() {
        int i = 0;
        int j = 0;
        dbg.showmsg(mContext, String.format("begin dologin(pwd=%s) from (%s)", pwd, ((new Exception()).getStackTrace())[1].getMethodName()));

        //for debug only
        //switchDataConn(true);
        //setMobileDataStatus(true);

        if (this.pwd.equals("NOVAL")) {
            dbg.out("password is empty(NOVAL)");
            info.append("password is empty(NOVAL)\n");
            // if no auth, passwork is always empty
            // return;
        }

        if (_Doing) {
            dbg.showmsg(mContext, "HTTPS CONNECTING, do nothing");
            dbg.out("HTTPS CONNECTING, do nothing");
            info.append("******** In processing, do nothing ******** \n");
            return;
        }


        //infoset(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(System.currentTimeMillis()) + " " + ((new Exception()).getStackTrace())[1].getMethodName() + "\n");
        infoappend(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(System.currentTimeMillis()) + " " + ((new Exception()).getStackTrace())[1].getMethodName() + "\n");
        //infoappend("Begin to login wifi: " + mSSID);
        infoappend(String.format("Begin to connect wifi: [%s] or [%s]", mSSID, mSSID_Direct));

        boolean bConn = getMobileDataStatus("getMobileDataEnabled");
        info.append("\nMobileDataConnection: " + bConn + "\n");

        // turn off mobile data connection
        info.append("Turn off mobile data connection\n");
        setMobileDataStatus(false);

        try {
            HashMap maplogin = new HashMap();
            // 20170718: the login URL was changed from https to http (by GIT)
            //maplogin.put("url_action", "https://webauth-redirect.oracle.com/login.html");
            maplogin.put("url_action", "http://webauth-redirect.oracle.com/login.html");
            maplogin.put("buttonClicked", "4");
            maplogin.put("redirect_url", "");
            maplogin.put("err_flag", "0");
            maplogin.put("username", "guest");
            maplogin.put("password", this.pwd);

            // this if for internal use in the child thread
            maplogin.put("connwifi", "yes");

            _Doing = true;
            runHttpsConnection(maplogin);
            dbg.out("END");
            return;
        } catch (Exception localException) {
            dbg.out(localException.toString());
            localException.printStackTrace();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        final Intent intent = new Intent(mContext,foregroundService.class);
        switch (v.getId()) {
            case R.id.btn_exit:
                dbg.out("exit button is clicked");

                boolean bConn = getMobileDataStatus("getMobileDataEnabled");
                info.append("MobileDataConnection: " + bConn + "\n");
                if (! bConn) {
                    setMobileDataStatus(true);
                    info.append("Tried to connect mobile data conection \n");
                }

                mWifiAdmin.closeWifi();
                cancelTimerTask();


                intent.putExtra("cmd",1);//0,开启前台服务,1,关闭前台服务
                dbg.out("stop foreground service");
                //followint method makes no sense
                //startService(intent);
                stopService(intent);

                //notificationManager.cancel(0);  //Just cancel the first notification icon
                notificationManager.cancelAll();
                dbg.out("after cancelAll()");

                //this.finish();
                //System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
                dbg.out("after finish() -- THIS SHOULD NOT BE SHOWN");
                break;

            case R.id.btn_download:
                if (!_isExe) {
                    task.execute("http://f.hiphotos.baidu.com/image/w%3D2048/sign=3b06d28fc91349547e1eef6462769358/d000baa1cd11728b22c9e62ccafcc3cec2fd2cd3.jpg"); // 执行异步操作
                    _isExe = true;
                }
                break;
            case R.id.btn_ssid:
                ssid_save();
                mSSID_Direct=ssid_load();
                break;
            case R.id.btn_hide:
                moveTaskToBack(true);
                info.append("MoveTaskToBack\n");
                break;

            case R.id.btn_login:
                intent.putExtra("cmd",0);//0,开启前台服务,1,关闭前台服务
                dbg.out("start foreground service");
                startService(intent);
                dologin();
                //int ret = test_internet("http://www.baidu.com", "百度");
                break;
            case R.id.btn_logout:
                try {

                    if (_Doing) {
                        info.append("******** In processing, do nothing ******** \n");
                        return;
                    } else {
                        _Doing = true;
                    }

                    infoset("Begin to logoff wifi: " + mSSID + "\n");
                    // for debug
                    //switchDataConn(true);
                    //setMobileDataStatus(true);
                    HashMap maplogoff = new HashMap();
                    // 20170718: the login URL was changed from https to http (by GIT)
                    //maplogoff.put("url_action", "https://webauth-redirect.oracle.com/logout.html");
                    maplogoff.put("url_action", "http://webauth-redirect.oracle.com/logout.html");
                    maplogoff.put("userStatus", "1");
                    maplogoff.put("err_flag", "0");
                    maplogoff.put("err_msg", "");

                    // this if for internal use in the child thread
                    maplogoff.put("connwifi", "no");

                    runHttpsConnection(maplogoff);
                    dbg.out("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    return;
                } catch (Exception paramView) {
                    dbg.out(paramView.toString());
                    dbg.out(paramView.getMessage());
                    paramView.printStackTrace();
                    return;
                }
            default:
                break;
        }
    }

    class downloadImageTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            System.out.println("[downloadImageTask->]doInBackground "
                    + params[0]);
            mDownloadImage = HttpUtils.getNetWorkBitmap(params[0]);
            //mStrbody = HttpUtils.getNetWorkUrl("http://www.baidu.com");
            System.out.println("result = " + mStrbody);
            return true;
        }

        // 下载完成回调
        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            image.setImageBitmap(mDownloadImage);

            System.out.println("result = " + result);
            super.onPostExecute(result);
        }

        // 更新进度回调
        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }


    }

    private class CreateHttpsConnTask extends AsyncTask<Map<String, String>, String, Integer> {
        private static final String HTTPS_EXAMPLE_URL = "https://webauth-redirect.oracle.com/login.html";
        private StringBuffer sBuffer = new StringBuffer();
        //private String request="";

        private CreateHttpsConnTask() {
        }

        private void notifyui(String msg) {
            dbg.out(String.format("publish progress [%s]", msg));
            publishProgress(msg);
        }

        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dbg.out("onPreExecute() called");
/**
 * 通知栏动态中左侧的下载图标动画
 * @Title: updateDownloadAnim
 * @Description: TODO(这里用一句话描述这个方法的作用)
 * @throws
 * @date 2015-5-7
 */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    while (_Doing) {
                        if (count > 1) count = 0;
                        showNotification4(count);
                        count++;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            dbg.out(e.toString());
                            e.printStackTrace();
                        }
                    }
                    showNotification4(1);
                }
            }).start();
        }


        @Override
        protected Integer doInBackground(Map<String, String>... params) {
            //HttpUriRequest httpPost = new HttpPost(HTTPS_EXAMPLE_URL);
            Map<String, String> kv = params[0];
            int i = 0;
            int j = 0;
            int retrymax = 100;

            if (kv.get("connwifi").equals("yes")) {
                // connect wifi
                try {
                    notifyui("Begin to enable wifi");
                    mWifiAdmin.openWifi();
                    dbg.out("begin to check wifi status");
                    retrymax = 100;
                    for (i = 0; i < retrymax; i++) {
                        if (!mWifiAdmin.IsWifiOpened()) {
                            notifyui(String.format("WIFI not opened: retry[%d/%d]", i, retrymax));
                            Thread.sleep(1000L);
                            continue;
                        } else {
                            notifyui("WIFI has been enabled");
                            break;
                        }
                    }
                    if (i == retrymax) {
                        notifyui(String.format("Can't enable WIFI after retry %d times, will NOT connect", retrymax));
                        return 1;
                    }

                    // set active mSSID
                    if (null != mWifiAdmin.IsExsits(mSSID_Direct)) {
                        // this is the new WIFI which don't need to do http post login
                        mSSID = mSSID_Direct;
                        notifyui(String.format("Found non-authorization wifi SSID(%s)", mSSID));
                    } else {
                        notifyui(String.format("Can not find non-authorization wifi SSID(%s), will use (%s)", mSSID_Direct, mSSID));
                    }

                    //Conect mSSID
                    notifyui("Begin to connect wifi [" + mSSID + "]");

                    retrymax = 3;
                    for (i = 0; i < retrymax; i++) {
                        j = mWifiAdmin.ConnNetworkBySSID(mSSID);
                        if (j == 2) {
                            notifyui(String.format("Can't find SSID[%s] retry[%d/%d]", mSSID, i, retrymax));
                            Thread.sleep(1000L);
                            continue;
                        } else {
                            break;
                        }
                    }
                    if (i == retrymax) {
                        notifyui(String.format("Can't find SSID(%s) after retry %d times, will NOT connect", mSSID, retrymax));
                        return 2;
                    }
                    dbg.out("after connect: " + mSSID);
                    dbg.out("Connect result: " + String.valueOf(j));
                    notifyui("IP address:" + mWifiAdmin.getIPAddressStr());

                    dbg.out("begin to check wifi status");
                    retrymax = 100;
                    for (i = 0; i < retrymax; i++) {
                        if (mWifiAdmin.isWifiContected() != mWifiAdmin.WIFI_CONNECTED) {
                            notifyui(String.format("wifi not connected: retry[%d/%d]", i, retrymax));
                            Thread.sleep(1000L);
                            continue;
                        } else {
                            if (!mWifiAdmin.getSSID().equals("\"" + mSSID + "\"")) {
                                notifyui(String.format("Other wifi [%s] is connected, need reconnect", mWifiAdmin.getSSID()));
                                j = mWifiAdmin.ConnNetworkBySSID(mSSID);
                                if (j == 2) {
                                    notifyui(String.format("Can't find SSID(%s) will terminate", mSSID));
                                    return 2;
                                }
                            }
                            int m = mWifiAdmin.getIPAddress();
                            if (m == 0) {
                                notifyui(String.format("Get IPAddress failed: retry[%d/%d]", i, retrymax));
                                Thread.sleep(1000L);
                                continue;
                            } else {
                                notifyui("Get IP address success:" + mWifiAdmin.getIPAddressStr());
                                break;
                            }
                        }
                    }
                    if (i == retrymax) {
                        notifyui(String.format("can't connect to SSID(%s) after %d retry", mSSID, retrymax));
                        return 3;
                    }
                } catch (Exception e) {
                    dbg.out(e.toString());
                    e.printStackTrace();
                    return 4;
                }
            } // end of connwifi=yes

            // don't need it anymore
            //kv.remove("connwifi");
            // end connect wifi

            // test if wifi is ready， only login need this
            if (kv.get("connwifi").equals("yes")) {
                int ret = test_internet("http://www.baidu.com", "百度");
                if (0 == ret) {
                    // internet is ready, don't login again
                    notifyui("Internet is ready, don't login again, NO HTTP request");
                    sBuffer.append("*****DUMY result: Web Authentication Failure： statusCode=1*****");
                    return 0;
                }else {
                    notifyui("Internet is NOT ready, do login");
                }
            }

            if (mSSID == mSSID_Direct) {
                // don't need do login
                notifyui("Don't do wifilogin for SSID: " + mSSID);
                sBuffer.append("*****DUMY result: Web Authentication Failure： statusCode=1*****");
                return 0;
            }

            // begin login wifi
            String request = kv.get("url_action");
            //kv.put("password", "MQ2EdRUF");
            HttpPost httpPost = new HttpPost(request);
            HttpClient httpClient = HttpUtils.getHttpsClient();
            try {
                // set header
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                try {
                    Iterator iterator = kv.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry<String, String> elem = (Entry<String, String>) iterator.next();
                        list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
                    }
                    if (list.size() > 0) {
                        //UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
                        httpPost.setEntity(entity);
                    }
                } catch (Exception e) {
                    Log.e("https", e.getMessage());
                }

                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse != null) {
                    StatusLine statusLine = httpResponse.getStatusLine();
                    if (statusLine != null
                            && statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    httpResponse.getEntity().getContent(),
                                    "UTF-8"));
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sBuffer.append(line);
                            }

                        } catch (Exception e) {
                            Log.e("https", e.getMessage());
                        } finally {
                            if (reader != null) {
                                reader.close();
                                reader = null;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("https", e.getMessage());
            } finally {

            }

            return null;
        }

        protected void onPostExecute(Integer paramBoolean) {
            info.append(this.sBuffer);
            if (this.sBuffer.lastIndexOf("Web Authentication Failure") != -1) {
                MainActivity.this.info.append("\n*********************\n");
                if ((this.sBuffer.lastIndexOf("statusCode=3") != -1) || (this.sBuffer.lastIndexOf("statusCode=5") != -1)) {
                    MainActivity.this.info.append("** Password wrong **");
                    dbg.showmsg(mContext, String.format("Password wrong [%s]", pwd));
                } else if (this.sBuffer.lastIndexOf("statusCode=1") != -1) {
                    MainActivity.this.info.append("** Already login **");
                    dbg.showmsg(mContext, String.format("** Already login **"));
                } else {
                    MainActivity.this.info.append("** UNKNOWN **");
                    dbg.showmsg(mContext, String.format("UNKNOWN ERROR"));
                }
            } else {
                dbg.showmsg(mContext, "ACTION END: " + mSSID);
            }

            // set to GUI
            infoappend("\n*********************\n");
            System.out.println("result = " + paramBoolean);

            _Doing = false;
            // if sucdessed, exit immediatelly.
            //findViewById(R.id.btn_exit).performClick();

            super.onPostExecute(paramBoolean);
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(String... progress) {
            //super.onProgressUpdate(progress);
            dbg.out(String.format("onProgressUpdate() called, progress=[%s]", progress[0]));
            infoappend(progress[0]);
            //dbg.toast(mContext, progress[0]);
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            dbg.out("onCancelled() called");
            infoappend("cancelled");
            _Doing = false;
        }

    }

    protected void showNotification2() {
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Builder builder = new Notification.Builder(MainActivity.this);
        //                new Intent(MainActivity.this,MainActivity.class),

        PendingIntent contentIndent = PendingIntent.getActivity(
                MainActivity.this,
                0,
                new Intent(MainActivity.this, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIndent)
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏里面的图标（小图标）
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))//下拉下拉列表里面的图标（大图标）
                .setTicker("Start monitor WIFI " + mSSID) //设置状态栏的显示的信息
                .setWhen(System.currentTimeMillis())//设置时间发生时间
                .setAutoCancel(false)//设置NO可以清除
                .setContentTitle("Monitor Wifi")//设置下拉列表里的标题
                .setContentText("this is SSID: " + mSSID)//设置上下文内容
                .setOngoing(true);
        //.setColor(Color.BLUE);

        Notification notification = builder.getNotification();
        //加i是为了显示多条Notification
        //notificationManager.notify(i, notification);
        notificationManager.notify(0, notification);
    }

    protected void showNotification3() {
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent broadcastIntent = new Intent(mContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Builder builder = new Builder(mContext);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏里面的图标（小图标）
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
        notificationManager.notify(0, mNotification);
    }

    public void showNotification4(int idx) {
        dbg.out("idx=" + idx);
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int[] res = new int[]{R.drawable.myicon, R.mipmap.ic_launcher};
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent broadcastIntent = new Intent(mContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Builder builder = new Builder(mContext);
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
        notificationManager.notify(1, mNotification);

        // /test how to set app as foregraound.
        //notificationManager.notify(0, mNotification);
        //startForeground(1, mNotification);
    }

    public class ScreenListener {
        private Context mContext;
        private ScreenBroadcastReceiver mScreenReceiver;
        private ScreenStateListener mScreenStateListener;

        public ScreenListener(Context context) {
            mContext = context;
            mScreenReceiver = new ScreenBroadcastReceiver();
        }

        /**
         * screen状态广播接收者
         */
        public class ScreenBroadcastReceiver extends BroadcastReceiver {
            private String action = null;

            @Override
            public void onReceive(Context context, Intent intent) {
                action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                    mScreenStateListener.onScreenOn();
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                    mScreenStateListener.onScreenOff();
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                    mScreenStateListener.onUserPresent();
                } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) { // 解锁
                    mScreenStateListener.onNetworkChange();
                }
            }
        }

        /**
         * 开始监听screen状态
         *
         * @param listener
         */
        public void begin(ScreenStateListener listener) {
            mScreenStateListener = listener;
            registerListener();
            getScreenState();
        }

        /**
         * 获取screen状态
         */
        private void getScreenState() {
            PowerManager manager = (PowerManager) mContext
                    .getSystemService(Context.POWER_SERVICE);
            if (manager.isScreenOn()) {
                if (mScreenStateListener != null) {
                    mScreenStateListener.onScreenOn();
                }
            } else {
                if (mScreenStateListener != null) {
                    mScreenStateListener.onScreenOff();
                }
            }
        }

        /**
         * 停止screen状态监听
         */
        public void unregisterListener() {
            mContext.unregisterReceiver(mScreenReceiver);
        }

        /**
         * 启动screen状态广播接收器
         */
        private void registerListener() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            mContext.registerReceiver(mScreenReceiver, filter);
        }


    }

    public interface ScreenStateListener {// 返回给调用者屏幕状态信息

        public void onScreenOn();

        public void onScreenOff();

        public void onUserPresent();

        public void onNetworkChange();
    }

    public static int test_internet(String path, String keyword){
        int nRet = 0;
        if (_Checking) {
            // in checking, return directly
            return 0;
        } else {
            _Checking = true;
        }

        try {
            URL url = new URL(path.trim());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setUseCaches(false); //不使用缓冲
            urlConnection.setRequestMethod("GET"); //使用get请求

            if(200 == urlConnection.getResponseCode()){
                //得到输入流
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024*4];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                if (baos.toString("utf-8").indexOf(keyword) != -1) {
                    dbg.out("wifi is ready");
                    nRet = 0;
                } else {
                    dbg.out("wifi is not ready");
                    nRet = 1;
                }
            } else {
                dbg.out("connect code:" + urlConnection.getResponseCode());
                nRet = 1;
            }
        }  catch (IOException e) {
            e.printStackTrace();
            nRet = 1;
        }
        _Checking = false;
        return nRet;
    }
}
