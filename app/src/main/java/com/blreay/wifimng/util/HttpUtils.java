package com.blreay.wifimng.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import org.apache.http.HttpVersion; 
import org.apache.http.client.HttpClient; 
import org.apache.http.conn.ClientConnectionManager; 
import org.apache.http.conn.scheme.PlainSocketFactory; 
import org.apache.http.conn.scheme.Scheme; 
import org.apache.http.conn.scheme.SchemeRegistry; 
import org.apache.http.conn.ssl.SSLSocketFactory; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager; 
import org.apache.http.params.BasicHttpParams; 
import org.apache.http.params.HttpProtocolParams; 
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context; 
   

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * Created by zhaozhan on 2017/4/14.
 */

public class HttpUtils {

    /**
     * 获取网络图片
     *
     * @param urlString
     *            如：http://f.hiphotos.baidu.com/image/w%3D2048/sign=3
     *            b06d28fc91349547e1eef6462769358
     *            /d000baa1cd11728b22c9e62ccafcc3cec2fd2cd3.jpg
     * @return
     * @date 2014.05.10
     */
    public static Bitmap getNetWorkBitmap(String urlString) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(urlString);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream();
            // 将InputStream转换成Bitmap
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return bitmap;
    }
/*
    public static String getNetWorkUrl(String urlString) {
        URL imgUrl = null;
        String result = null;
        HttpClient httpClient= new DefaultHttpClient();
        //final WebClient webClient = new WebClient();
        try{
            // 创建HttpGet对象。
            HttpGet get = new HttpGet("http://www.baidu.com");
            // 发送GET请求
            HttpResponse httpResponse = httpClient.execute(get);
            // 如果服务器成功地返回响应
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 获取服务器响应字符串
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        }catch(Exception e){
            e.printStackTrace();
            result = "error" + e.toString();
        }finally{
            httpClient.getConnectionManager().shutdown();
        }

        return result;
    } */
   
    public static HttpClient getHttpsClient() { 
      BasicHttpParams params = new BasicHttpParams(); 
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
      HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET); 
      HttpProtocolParams.setUseExpectContinue(params, true); 
       
      SchemeRegistry schReg = new SchemeRegistry(); 
      schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
      schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443)); 
       
      ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg); 
       
      return new DefaultHttpClient(connMgr, params); 
    } 
     
    public static HttpClient getCustomClient() { 
      BasicHttpParams params = new BasicHttpParams(); 
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
      HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET); 
      HttpProtocolParams.setUseExpectContinue(params, true); 
       
      SchemeRegistry schReg = new SchemeRegistry(); 
      schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
      schReg.register(new Scheme("https", MySSLSocketFactory.getSocketFactory(), 443)); 
       
      ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg); 
       
      return new DefaultHttpClient(connMgr, params); 
    } 
     
    public static HttpClient getSpecialKeyStoreClient(Context context) { 
      BasicHttpParams params = new BasicHttpParams(); 
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
      HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET); 
      HttpProtocolParams.setUseExpectContinue(params, true); 
       
      SchemeRegistry schReg = new SchemeRegistry(); 
      schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
      //schReg.register(new Scheme("https", CustomerSocketFactory.getSocketFactory(context), 443));
       
      ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg); 
       
      return new DefaultHttpClient(connMgr, params); 
    }


}

