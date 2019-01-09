package com.blreay.wifimng.util;

//import com.blreay.wifimng.util.dbg;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import android.text.format.Formatter;

import static android.content.Context.WIFI_SERVICE;


public abstract class WifiAdmin {

  private static final String TAG = "WifiAdmin";

  private WifiManager mWifiManager;
  private WifiInfo mWifiInfo;
  private ConnectivityManager mConnectivityManager;
  // 扫描出的网络连接列表
  private List<ScanResult> mWifiList;
  private List<WifiConfiguration> mWifiConfiguration;


  private WifiLock mWifiLock;

  private String mPasswd = "";
  private String mSSID = "";

  private Context mContext = null;

  public WifiAdmin(Context context) {

    mContext = context;

    // 取得WifiManager对象
    mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
    // 取得WifiInfo对象
    mWifiInfo = mWifiManager.getConnectionInfo();
    mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //dbg.out("getIpAddress = " + mWifiInfo.getIpAddress());
  }

  // 打开WIFI
  public void openWifi() {
    if (!mWifiManager.isWifiEnabled()) {
      mWifiManager.setWifiEnabled(true);
      //by zzy
      mWifiInfo = mWifiManager.getConnectionInfo();
    }
  }

  // 关闭WIFI
  public void closeWifi() {
    if (mWifiManager.isWifiEnabled()) {
      mWifiManager.setWifiEnabled(false);
    }
  }

  public abstract Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter);

  public abstract void myUnregisterReceiver(BroadcastReceiver receiver);

  public abstract void onNotifyWifiConnected();

  public abstract void onNotifyWifiConnectFailed();

  // 添加一个网络并连接
  public void addNetwork(WifiConfiguration wcg) {

    register();

    WifiApAdmin.closeWifiAp(mContext);

    int wcgID = mWifiManager.addNetwork(wcg);
    boolean b = mWifiManager.enableNetwork(wcgID, true);
  }

  public static final int TYPE_NO_PASSWD = 0x11;
  public static final int TYPE_WEP = 0x12;
  public static final int TYPE_WPA = 0x13;

  public void addNetwork(String ssid, String passwd, int type) {
    if (ssid == null || passwd == null || ssid.equals("")) {
      Log.e(TAG, "addNetwork() ## nullpointer error!");
      return;
    }

    if (type != TYPE_NO_PASSWD && type != TYPE_WEP && type != TYPE_WPA) {
      Log.e(TAG, "addNetwork() ## unknown type = " + type);
    }

    stopTimer();
    unRegister();

    addNetwork(createWifiInfo(ssid, passwd, type));
  }

  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      // TODO Auto-generated method stub
      if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
        Log.d(TAG, "RSSI changed");

        //有可能是正在获取，或者已经获取了
        Log.d(TAG, " intent is " + WifiManager.RSSI_CHANGED_ACTION);

        if (isWifiContected() == WIFI_CONNECTED) {
          stopTimer();
          onNotifyWifiConnected();
          unRegister();
        } else if (isWifiContected() == WIFI_CONNECT_FAILED) {
          stopTimer();
          closeWifi();
          onNotifyWifiConnectFailed();
          unRegister();
        } else if (isWifiContected() == WIFI_CONNECTING) {

        }
      }
    }
  };

  private final int STATE_REGISTRING = 0x01;
  private final int STATE_REGISTERED = 0x02;
  private final int STATE_UNREGISTERING = 0x03;
  private final int STATE_UNREGISTERED = 0x04;

  private int mHaveRegister = STATE_UNREGISTERED;
  private synchronized void register() {
    Log.v(TAG, "register() ##mHaveRegister = " + mHaveRegister);

    if (mHaveRegister == STATE_REGISTRING
            || mHaveRegister == STATE_REGISTERED) {
      return ;
    }

    mHaveRegister = STATE_REGISTRING;
    myRegisterReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    mHaveRegister = STATE_REGISTERED;

    startTimer();
  }

  private synchronized void unRegister() {
    Log.v(TAG, "unRegister() ##mHaveRegister = " + mHaveRegister);

    if (mHaveRegister == STATE_UNREGISTERED
            || mHaveRegister == STATE_UNREGISTERING) {
      return ;
    }

    mHaveRegister = STATE_UNREGISTERING;
    myUnregisterReceiver(mBroadcastReceiver);
    mHaveRegister = STATE_UNREGISTERED;
  }

  private Timer mTimer = null;
  private void startTimer() {
    if (mTimer != null) {
      stopTimer();
    }

    mTimer = new Timer(true);
//      mTimer.schedule(mTimerTask, 0, 20 * 1000);// 20s
    mTimer.schedule(mTimerTask, 30 * 1000);
  }

  private TimerTask mTimerTask = new TimerTask() {

    @Override
    public void run() {
      // TODO Auto-generated method stub
      Log.e(TAG, "timer out!");
      onNotifyWifiConnectFailed();
      unRegister();
    }
  };

  private void stopTimer() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }

  @Override
  protected void finalize() {
    try {
      super.finalize();
      unRegister();
    } catch (Throwable e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public WifiConfiguration createWifiInfo(String SSID, String password, int type) {

    Log.v(TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);

    WifiConfiguration config = new WifiConfiguration();
    config.allowedAuthAlgorithms.clear();
    config.allowedGroupCiphers.clear();
    config.allowedKeyManagement.clear();
    config.allowedPairwiseCiphers.clear();
    config.allowedProtocols.clear();
    config.SSID = "\"" + SSID + "\"";

    WifiConfiguration tempConfig = this.IsExsits(SSID);
    if (tempConfig != null) {
        // don't know why remote this network from system
      //mWifiManager.removeNetwork(tempConfig.networkId);
    }

    // 分为三种情况：1没有密码2用wep加密3用wpa加密
    if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS
      config.wepKeys[0] = "";
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.wepTxKeyIndex = 0;

    } else if (type == TYPE_WEP) {  //  WIFICIPHER_WEP
      config.hiddenSSID = true;
      config.wepKeys[0] = "\"" + password + "\"";
      config.allowedAuthAlgorithms
              .set(WifiConfiguration.AuthAlgorithm.SHARED);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
      config.allowedGroupCiphers
              .set(WifiConfiguration.GroupCipher.WEP104);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.wepTxKeyIndex = 0;
    } else if (type == TYPE_WPA) {   // WIFICIPHER_WPA
      config.preSharedKey = "\"" + password + "\"";
      config.hiddenSSID = true;
      config.allowedAuthAlgorithms
              .set(WifiConfiguration.AuthAlgorithm.OPEN);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
      config.allowedPairwiseCiphers
              .set(WifiConfiguration.PairwiseCipher.TKIP);
      // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedPairwiseCiphers
              .set(WifiConfiguration.PairwiseCipher.CCMP);
      config.status = WifiConfiguration.Status.ENABLED;
    }

    return config;
  }

  public static final int WIFI_CONNECTED = 0x01;
  public static final int WIFI_CONNECT_FAILED = 0x02;
  public static final int WIFI_CONNECTING = 0x03;
  /**
   * 判断wifi是否连接成功,不是network
   *
   * @param context
   * @return
   */
  public int isWifiContected() {
    dbg.out("isWifiContected begin");

    NetworkInfo wifiNetworkInfo=mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    dbg.out("isConnectedOrConnecting = " + wifiNetworkInfo.isConnectedOrConnecting());
    dbg.out("wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());
    if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR
            || wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING) {
      return WIFI_CONNECTING;
    } else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED) {
      return WIFI_CONNECTED;
    } else {
      dbg.out("getDetailedState() == " + wifiNetworkInfo.getDetailedState());
      return WIFI_CONNECT_FAILED;
    }
  }

  public WifiConfiguration IsExsits(String SSID) {
    List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
    for (WifiConfiguration existingConfig : existingConfigs) {
      if (existingConfig.SSID.equals("\"" + SSID + "\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {
        return existingConfig;
      }
    }
    return null;
  }



  // 断开指定ID的网络
  public void disconnectWifi(int netId) {
    mWifiManager.disableNetwork(netId);
    mWifiManager.disconnect();
  }

  // 检查当前WIFI状态
  public int checkState() {
    return mWifiManager.getWifiState();
  }

  // 锁定WifiLock
  public void acquireWifiLock() {
    mWifiLock.acquire();
  }

  // 解锁WifiLock
  public void releaseWifiLock() {
    // 判断时候锁定
    if (mWifiLock.isHeld()) {
      mWifiLock.acquire();
    }
  }

  // 创建一个WifiLock
  public void creatWifiLock() {
    mWifiLock = mWifiManager.createWifiLock("Test");
  }

  // 得到配置好的网络
  public List<WifiConfiguration> getConfiguration() {
    return mWifiConfiguration;
  }

  // 指定配置好的网络进行连接
  public void connectConfiguration(int index) {
    // 索引大于配置好的网络索引返回
    if (index > mWifiConfiguration.size()) {
      return;
    }
    // 连接配置好的指定ID的网络
    mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
            true);
  }

  public void startScan() {
    mWifiManager.startScan();
    mWifiList = mWifiManager.getScanResults();
    mWifiConfiguration = mWifiManager.getConfiguredNetworks();
  }

  // 得到网络列表
  public List<ScanResult> getWifiList() {
    return mWifiList;
  }

  // 查看扫描结果
  public StringBuilder lookUpScan() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < mWifiList.size(); i++) {
      stringBuilder
              .append("Index_" + new Integer(i + 1).toString() + ":");
      // 将ScanResult信息转换成一个字符串包
      // 其中把包括：BSSID、SSID、capabilities、frequency、level
      stringBuilder.append((mWifiList.get(i)).toString());
      stringBuilder.append("/n");
    }
    return stringBuilder;
  }

  // 得到MAC地址
  public String getMacAddress() {

    mWifiInfo  = mWifiManager.getConnectionInfo();
    return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
  }

  // 得到接入点的BSSID
  public String getBSSID() {

    mWifiInfo  = mWifiManager.getConnectionInfo();
    return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
  }

  // 得到接入点的BSSID
  public String getSSID() {
    mWifiInfo  = mWifiManager.getConnectionInfo();
    return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
  }

  // 得到IP地址
  public int getIPAddress() {
    mWifiInfo  = mWifiManager.getConnectionInfo();
    dbg.out("mWifiInfo=" + mWifiInfo.toString());
    return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
  }

  public String getIPAddressStr() {
    //WifiManager wifiManager = (WifiManager)mContext.getSystemService(WIFI_SERVICE);
    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
    int ip = wifiInfo.getIpAddress();

    String ipString = String.format(
            "%d.%d.%d.%d",
            (ip & 0xff),
            (ip >> 8 & 0xff),
            (ip >> 16 & 0xff),
            (ip >> 24 & 0xff));

    String ipString2 = Formatter.formatIpAddress(ip);

    return ipString2;
  }
  // 得到连接的ID
  public int getNetworkId() {
    mWifiInfo  = mWifiManager.getConnectionInfo();
    return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
  }

  // 得到WifiInfo的所有信息包
  public String getWifiInfo() {
    mWifiInfo  = mWifiManager.getConnectionInfo();
    return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
  }
  public boolean IsWifiOpened()
  {
    return this.mWifiManager.isWifiEnabled();
  }

  public int ConnNetworkBySSID(String paramString)
  {
    dbg.out("begin");
    Iterator<WifiConfiguration> paramWifiConfiguration;
    paramWifiConfiguration = this.mWifiManager.getConfiguredNetworks().iterator();
    WifiConfiguration localWifiConfiguration;
    do  {
      if (!paramWifiConfiguration.hasNext()) {
        dbg.out("can't find " + paramString);
        return 2;
      }
      localWifiConfiguration = (WifiConfiguration)paramWifiConfiguration.next();
    } while ((localWifiConfiguration.SSID == null) || (!localWifiConfiguration.SSID.equals("\"" + paramString + "\"")));
    this.mWifiManager.enableNetwork(localWifiConfiguration.networkId, true);
    if (this.mWifiManager.reconnect()) {
      //mWifiInfo = localWifiConfiguration;
      mWifiInfo = mWifiManager.getConnectionInfo();
      return 0;
    } else {
      return 1;
    }
  }

  public int ConnNetwork(WifiConfiguration paramWifiConfiguration1, String paramString)
  {
    Iterator<WifiConfiguration> paramWifiConfiguration;
    paramWifiConfiguration = this.mWifiManager.getConfiguredNetworks().iterator();
    WifiConfiguration localWifiConfiguration;
    do
    {
      if (!paramWifiConfiguration.hasNext()) {
        return 0;
      }
      localWifiConfiguration = (WifiConfiguration)paramWifiConfiguration.next();
    } while ((localWifiConfiguration.SSID == null) || (!localWifiConfiguration.SSID.equals("\"" + paramString + "\"")));
    this.mWifiManager.enableNetwork(localWifiConfiguration.networkId, true);
    if (this.mWifiManager.reconnect()) {}
    for (int i = 1;; i = 2) {
      return i;
    }
  }

  public void ConnNetwork(String paramString1, String paramString2, int paramInt)
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString1.equals("")))
    {
      Log.e("WifiAdmin", "addNetwork() ## nullpointer error!");
      return;
    }
    if ((paramInt != 17) && (paramInt != 18) && (paramInt != 19)) {
      Log.e("WifiAdmin", "addNetwork() ## unknown type = " + paramInt);
    }
    ConnNetwork(createWifiInfo(paramString1, paramString2, paramInt), paramString1);
  }

}