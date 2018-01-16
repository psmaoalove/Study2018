
package com.rengh.study.util.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

/**
 * Created by rengh on 17-5-29.
 */
public class HttpHelper {
    private final String TAG = "HttpHelper";

    public static final int TYPE_SIGN_NOT = 0;
    public static final int TYPE_SIGN_SCLOUD_1 = 1;
    public static final int TYPE_SIGN_SCLOUD_2 = 2;

    private int mTimeOut = 1000 * 5;

    private int mSignType = TYPE_SIGN_NOT;
    private String mKeySseretKey;
    private String mKeyAccess;

    public HttpHelper() {
    }

    public HttpHelper setTimeOut(int mills) {
        mTimeOut = mills;
        return this;
    }

    public HttpHelper setSignType(int type) {
        mSignType = type;
        return this;
    }

    public HttpHelper setKeySecret(String key) {
        mKeySseretKey = key;
        return this;
    }

    public HttpHelper setKeyAccess(String key) {
        mKeyAccess = key;
        return this;
    }

    public HttpResponse get(String url) {
        HttpResponse httpResponse = null;
        try {
            httpResponse = getImpl(url);
        } catch (CertPathValidatorException e) {
            LogUtils.e(TAG, "CertPathValidatorException: ", e);
            if (url.startsWith("https://")) {
                url = url.replace("https://", "http://");
                httpResponse = get(url);
            }
        }
        return httpResponse;
    }

    public HttpResponse getBitmap(Context context, String url) {
        try {
            return getBitmapImpl(context, url);
        } catch (CertPathValidatorException e) {
            LogUtils.e(TAG, "CertPathValidatorException: ", e);
            if (url.startsWith("https://")) {
                url = url.replace("https://", "http://");
                return getBitmap(context, url);
            }
        }
        return null;
    }

    public HttpDownload getUrlFileInfo(String urlPath) {
        HttpDownload httpDownload = new HttpDownload();
        httpDownload.url = urlPath;
        if (TextUtils.isEmpty(urlPath)) {
            return httpDownload;
        }
        RandomAccessFile fos = null;
        InputStream is = null;
        HttpURLConnection conn;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15 * 1000);
            conn.setReadTimeout(15 * 1000);

            String contentType = conn.getHeaderField("Content-Type");

            httpDownload.size = conn.getContentLength();

            conn.getResponseCode();
            String realUrl = conn.getURL().toString();
            httpDownload.realUrl = realUrl;
            if (realUrl != null && !"".equals(realUrl) && !urlPath.equals(realUrl)) {
                CdnTsVideoInfo arkVideoInfo = CdnTsVideoInfo.getFileInfo(realUrl);
                httpDownload.netMd5 = arkVideoInfo.getMd5();
            }
        } catch (IllegalArgumentException e) {
            LogUtils.i(TAG, "===== IllegalArgumentException:", e);
        } catch (MalformedURLException e) {
            LogUtils.i(TAG, "===== MalformedURLException:", e);
        } catch (IOException e) {
            LogUtils.i(TAG, "===== IOException:", e);
        } catch (ClassCastException e) {
            LogUtils.i(TAG, "===== ClassCastException:", e);
        } catch (Exception e) {
            LogUtils.i(TAG, "===== Exception:", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }

        return httpDownload;
    }

    public HttpDownload download(String downloadUrl, String path, int downloadSpeed) {
        LogUtils.i(TAG, "path=" + path + " url=" + downloadUrl);
        HttpDownload httpDownload = new HttpDownload();
        httpDownload.url = downloadUrl;
        httpDownload.path = path;
        if (TextUtils.isEmpty(downloadUrl) || TextUtils.isEmpty(path)) {
            return httpDownload;
        }

        RandomAccessFile fos = null;
        InputStream is = null;
        HttpURLConnection conn;

        try {
            URL url = new URL(downloadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(mTimeOut);
            conn.setReadTimeout(mTimeOut);

            String contentType = conn.getHeaderField("Content-Type");
            LogUtils.i(TAG, "===== contentType:" + contentType);

            long fileSize = conn.getContentLength();
            LogUtils.i(TAG, "===== download file's size:" + fileSize / 1024 + " KB");
            httpDownload.size = fileSize;
            if (httpDownload.size == 0) {
                return httpDownload;
            }

            httpDownload.responsCode = conn.getResponseCode();
            String realUrl = conn.getURL().toString();
            httpDownload.realUrl = realUrl;
            LogUtils.i(TAG, "===== realUrl:" + realUrl);
            if (realUrl != null && !"".equals(realUrl) && !downloadUrl.equals(realUrl)) {
                CdnTsVideoInfo arkVideoInfo = CdnTsVideoInfo.getFileInfo(realUrl);
                httpDownload.netMd5 = arkVideoInfo.getMd5();
                URL downloadURL = new URL(realUrl);
                conn = (HttpURLConnection) downloadURL.openConnection();
            }

            File file = new File(path);
            if (file.exists()) {
                if (!file.delete()) {
                    LogUtils.e(TAG, "delete old file error: " + file.getPath());
                    return httpDownload;
                }
            }
            String dir = file.getParent();
            LogUtils.i(TAG, "dir: " + dir);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            if (file.createNewFile()) {
                LogUtils.i(TAG, "create new file: " + file.getPath());
            } else {
                LogUtils.e(TAG, "create new file error: " + file.getPath());
                return httpDownload;
            }

            is = conn.getInputStream();
            fos = new RandomAccessFile(file, "rw");
            byte[] buf = new byte[1024 * 10];
            long totalSize = 0;
            int num = 0;
            long cursum = 0;
            int speed = 1000 * 40;
            speed = downloadSpeed;
            while ((num = is.read(buf)) != -1) {
                fos.write(buf, 0, num);
                totalSize += num;
                cursum += num;
                // long bootLength = SystemClock.elapsedRealtime();
                // boolean isBootedFiveMinutes = bootLength > 1000 * 60 * 5;
                // if (speed < downloadSpeed && isBootedFiveMinutes) {
                // speed = downloadSpeed;
                // LogUtils.i(TAG, "===== Change speed: " + speed / 1024 + "KB");
                // }
                if (cursum >= speed) {
                    LogUtils.i(TAG, "download size: " + totalSize / 1024 + "KB");
                    ThreadUtils.sleep(1000);
                    cursum = 0;
                }
            }
            if (fos.getFD().valid()) {
                fos.getFD().sync();
            }
            httpDownload.localMd5 = MD5Utils.getFileMD5(path);
            httpDownload.success = true;
        } catch (IllegalArgumentException e) {
            LogUtils.i(TAG, "===== IllegalArgumentException:", e);
        } catch (MalformedURLException e) {
            LogUtils.i(TAG, "===== MalformedURLException:", e);
        } catch (IOException e) {
            LogUtils.i(TAG, "===== IOException:", e);
        } catch (ClassCastException e) {
            LogUtils.i(TAG, "===== ClassCastException:", e);
        } catch (Exception e) {
            LogUtils.i(TAG, "===== Exception:", e);
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
            }
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }

        return httpDownload;
    }

    private HttpResponse getImpl(String mUrl) throws CertPathValidatorException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader read = null;
        try {
            // 不使用证书
            URL url = new URL(mUrl);
            trustAllHosts();
            if ("https".equals(url.getProtocol().toLowerCase(Locale.getDefault()))) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
                urlConnection = httpsURLConnection;
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                // 解决异常：IOException: unexpected end of stream on Connection...Caused by: java.io.EOFException: \n not found: size=0 content=...
                urlConnection.setRequestProperty("Connection", "close");
            }
            String path = url.getPath();
            String param = url.getQuery();
            String realParams = getRealParams(param);
            if (TYPE_SIGN_SCLOUD_2 == mSignType && !TextUtils.isEmpty(mKeySseretKey) && !TextUtils.isEmpty(mKeyAccess)) {
                // 云平台签名规范2：添加date到头信息
                SimpleDateFormat fm = new SimpleDateFormat("EEE,d MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
                String date = fm.format(new Date()) + " GMT";
                //后面在lightHttp中会做签名工作，这里不做处理
                //String sign = SignUrlUtils.sign(mKeySseretKey, "GET", path, null, date, realParams);
                urlConnection.addRequestProperty("Date", date);
                urlConnection.addRequestProperty("Authorization", "LETV " + mKeyAccess);
            }
            urlConnection.setConnectTimeout(mTimeOut);
            urlConnection.setReadTimeout(mTimeOut);
            urlConnection.connect();
            httpResponse.responsCode = urlConnection.getResponseCode();
            in = urlConnection.getInputStream();
            inReader = new InputStreamReader(in);
            read = new BufferedReader(inReader);
            StringBuilder builder = new StringBuilder();
            String tmp = null;
            while (null != (tmp = read.readLine())) {
                builder.append(tmp);
            }
            httpResponse.response = builder.toString();
            httpResponse.success = true;
            builder = null;
        } catch (IllegalMonitorStateException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "IllegalMonitorStateException: " + httpResponse.errMsg);
        } catch (IllegalArgumentException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "IllegalArgumentException: " + httpResponse.errMsg);
        } catch (RuntimeException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "RuntimeException: " + httpResponse.errMsg);
        } catch (UnknownHostException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "UnknownHostException: " + httpResponse.errMsg);
        } catch (ConnectException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "ConnectException: " + httpResponse.errMsg);
        } catch (SocketTimeoutException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "SocketTimeoutException: " + httpResponse.errMsg);
        } catch (MalformedURLException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "MalformedURLException: " + httpResponse.errMsg);
        } catch (IOException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "IOException.", e);
        } catch (Exception e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "Exception.", e);
        } finally {
            if (null != read) {
                try {
                    read.close();
                } catch (IOException e) {
                }
                read = null;
            }
            if (null != inReader) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inReader = null;
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (null != urlConnection) {
                urlConnection.disconnect();
                urlConnection = null;
            }
        }

        if (null != httpResponse.response && httpResponse.response.contains("&")) {
            httpResponse.response = httpResponse.response.replace("&#039;", "'");
            httpResponse.response = httpResponse.response.replace("&quot;", "\"");
            httpResponse.response = httpResponse.response.replace("&lt;", "<");
            httpResponse.response = httpResponse.response.replace("&gt;", ">");
            httpResponse.response = httpResponse.response.replace("&amp;", "&");
        }

        return httpResponse;
    }

    private HttpResponse getBitmapImpl(Context context, String mUrl) throws CertPathValidatorException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        InputStreamReader inReader = null;
        httpResponse.bitmap = null;
        try {
            // 不使用证书
            URL url = new URL(mUrl);
            trustAllHosts();
            if ("https".equals(url.getProtocol().toLowerCase(Locale.getDefault()))) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
                urlConnection = httpsURLConnection;
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            urlConnection.setConnectTimeout(mTimeOut);
            urlConnection.setReadTimeout(mTimeOut);
            urlConnection.connect();
            httpResponse.responsCode = urlConnection.getResponseCode();
            in = urlConnection.getInputStream();
            httpResponse.bitmap = BitmapUtils.readBitMap(context, in);
            httpResponse.success = true;
        } catch (UnknownHostException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "UnknownHostException: " + httpResponse.errMsg);
        } catch (ConnectException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "ConnectException: " + httpResponse.errMsg);
        } catch (SocketTimeoutException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "SocketTimeoutException: " + httpResponse.errMsg);
        } catch (MalformedURLException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "MalformedURLException: " + httpResponse.errMsg);
        } catch (IOException e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "IOException.", e);
        } catch (Exception e) {
            httpResponse.errMsg = e.getMessage();
            LogUtils.e(TAG, "Exception.", e);
        } finally {
            if (null != inReader) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != urlConnection) {
                urlConnection.disconnect();
            }
        }

        return httpResponse;
    }

    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    @SuppressLint("TrulyRandom")
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }

                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取需要计算签名的参数(去空)
     *
     * @return 去空后的参数拼接结果
     */
    private static String getRealParams(String params) {
        if (null == params) {
            return null;
        }
        String[] items = params.split("&");
        String newParams = "";
        if (null != items && items.length > 0) {
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                if (null == item || !item.endsWith("=")) {
                    if (i != 0) {
                        item = "&" + item;
                    }
                    newParams += item;
                }
            }
        }
        return newParams;
    }

    public class HttpDownload {
        private boolean success;
        private int responsCode;
        private long size;
        private String netMd5;
        private String localMd5;
        private String path;
        private String url;
        private String realUrl;

        public HttpDownload() {
        }

        public boolean isSuccess() {
            return success;
        }

        public int getResponsCode() {
            return responsCode;
        }

        public long getSize() {
            return size;
        }

        public String getNetMd5() {
            return netMd5;
        }

        public String getLocalMd5() {
            return localMd5;
        }

        public String getPath() {
            return path;
        }

        public String getUrl() {
            return url;
        }

        public String getRealUrl() {
            return realUrl;
        }

        @Override
        public String toString() {
            return "HttpDownload{" +
                    "success=" + success +
                    ", responsCode=" + responsCode +
                    ", size=" + size +
                    ", netMd5='" + netMd5 + '\'' +
                    ", localMd5='" + localMd5 + '\'' +
                    ", path='" + path + '\'' +
                    ", url='" + url + '\'' +
                    ", realUrl='" + realUrl + '\'' +
                    '}';
        }
    }

    public class HttpResponse {
        private boolean success;
        private int responsCode;
        private String response;
        private String errMsg;
        private Bitmap bitmap;

        public HttpResponse() {
        }

        public boolean isSuccess() {
            return success;
        }

        public int getResponsCode() {
            return responsCode;
        }

        public String getResponse() {
            return response;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        public String toString() {
            return "HttpResponse{" +
                    "success=" + success +
                    ", responsCode=" + responsCode +
                    ", response='" + response + '\'' +
                    ", errMsg='" + errMsg + '\'' +
                    ", bitmap=" + bitmap +
                    '}';
        }
    }

    private static class CdnTsVideoInfo {
        private String trdID;
        private String videocode;
        private String videoRate;
        private String audiocode;
        private String audioRate;
        private String duration;
        private String size;
        private String md5;
        private String timestamp;

        public CdnTsVideoInfo() {
        }

        public static CdnTsVideoInfo getFileInfo(String realUrl) {
            CdnTsVideoInfo fileInfo = new CdnTsVideoInfo();
            if (TextUtils.isEmpty(realUrl)) {
                return fileInfo;
            }

            if (!realUrl.contains("/")) {
                return fileInfo;
            }

            int start = realUrl.lastIndexOf("/") + 1;
            if (start <= 1) {
                return fileInfo;
            }

            int end = -1;
            if (realUrl.contains("?")) {
                end = realUrl.indexOf("?");
            } else {
                end = realUrl.length();
            }
            if (end <= 0) {
                return fileInfo;
            }

            LogUtils.i("BootAdFileInfo",
                    "start:" + start + ", end:" + end + ", length:" + realUrl.length());

            if (start >= end || start > realUrl.length() || end > realUrl.length()) {
                return fileInfo;
            }
            String name = realUrl.substring(start, end);
            LogUtils.i("BootAdFileInfo", "name:" + name);

            if (TextUtils.isEmpty(name)) {
                return fileInfo;
            }

            if (!name.contains("-")) {
                return fileInfo;
            }

            String[] arr = name.split("-");
            if (arr == null) {
                return fileInfo;
            }

            if (name.endsWith(".ts") && arr.length == 9) {
                fileInfo.setTrdID(arr[0]);
                fileInfo.setVideoCode(arr[1]);
                fileInfo.setVideoRate(arr[2]);
                fileInfo.setAudioCode(arr[3]);
                fileInfo.setAudioRate(arr[4]);
                fileInfo.setDuration(arr[5]);
                fileInfo.setSize(arr[6]);
                fileInfo.setMd5(arr[7]);
                fileInfo.setTimeStamp(arr[8]);
            } else if (name.endsWith(".mp4") && arr.length == 10) {
                fileInfo.setTrdID(arr[1]);
                fileInfo.setVideoCode(arr[2]);
                fileInfo.setVideoRate(arr[3]);
                fileInfo.setAudioCode(arr[4]);
                fileInfo.setAudioRate(arr[5]);
                fileInfo.setDuration(arr[6]);
                fileInfo.setSize(arr[7]);
                fileInfo.setMd5(arr[8]);
                fileInfo.setTimeStamp(arr[9]);
            }
            return fileInfo;
        }

        public void setTrdID(String trdID) {
            this.trdID = trdID;
        }

        public String getTrdID() {
            return this.trdID;
        }

        public void setVideoCode(String videocode) {
            this.videocode = videocode;
        }

        public String getVideoCode() {
            return this.videocode;
        }

        public void setVideoRate(String videoRate) {
            this.videoRate = videoRate;
        }

        public String getVideoRate() {
            return this.videoRate;
        }

        public void setAudioCode(String audiocode) {
            this.audiocode = audiocode;
        }

        public String getAudioCode() {
            return this.audiocode;
        }

        public void setAudioRate(String audioRate) {
            this.audioRate = audioRate;
        }

        public String getAudioRate() {
            return this.audioRate;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDuration() {
            return this.duration;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getSize() {
            return this.size;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getMd5() {
            return this.md5;
        }

        public void setTimeStamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getTimeStamp() {
            return this.timestamp;
        }
    }

}
