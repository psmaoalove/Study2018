
package com.rengh.library.util.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * WifiManager封装。
 * 该类的大部分方法为异步操作，如：打开WIFI、关闭WIFI、添加、移除、断开连接等。
 * 这些方法返回true只代表系统底层方法执行成功，并不代表一经执行完毕。
 * Created by rengh on 2017/3/25.
 */
public class WifiUtils {
    private final String TAG = "WifiUtils";

    /**
     * WIFI_STATE_DISABLING WIFI网卡正在关闭 0 WIFI_STATE_DISABLED WIFI网卡不可用 1
     * WIFI_STATE_ENABLING WIFI网卡正在打开 2 WIFI_STATE_ENABLED WIFI网卡可用 3
     * WIFI_STATE_UNKNOWN WIFI网卡状态不可知 4
     */

    /**
     * WIFI热点加密类型枚举
     */
    public enum WifiCipherType {
        WIFI_CIPHER_WEP, WIFI_CIPHER_WPA_EAP, WIFI_CIPHER_WPA_PSK, WIFI_CIPHER_WPA2_PSK,
        WIFI_CIPHER_NOPASS
    }

    private Context mContext;
    private static WifiUtils sInstance;

    private WifiManager mWifiManager;
    private List<WifiConfiguration> mWifiConfList;

    private WifiUtils(Context context) {
        mContext = context.getApplicationContext();
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 获取WifiUtils的单例
     *
     * @param context 应用上下文
     * @return WifiUtils单例对象
     */
    public static WifiUtils getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new WifiUtils(context);
        }
        return sInstance;
    }

    /**
     * 判断WIFI是否已连接
     *
     * @return 当前连接是WIFI类型时返回true
     */
    public boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Wifi是否已启用
     *
     * @return 如果WIFI是启用状态，返回true
     */
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 设置WIFI状态
     *
     * @param enabled true启用或false关闭
     * @return 设置成功或目标状态和当前状态一致时返回true
     */
    public boolean setWifiEnabled(boolean enabled) {
        if (enabled && !isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(enabled);
        } else if (!enabled && isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(enabled);
        }
        // 目标状态和当前状态一致，返回true
        return true;
    }

    /**
     * 开始扫描附近WIFI热点
     *
     * @return 执行成功返回true
     */
    public boolean startScan() {
        return mWifiManager.startScan();
    }

    public boolean pingSupplicant() {
        return mWifiManager.pingSupplicant();
    }

    public boolean reassociate() {
        return mWifiManager.reassociate();
    }

    /**
     * 获取WIFI状态
     *
     * @return 参考WifiManager
     */
    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 获取Wifi的DHCP信息
     *
     * @return DhcpInfo对象
     */
    public DhcpInfo getDhcpInfo() {
        return mWifiManager.getDhcpInfo();
    }

    /**
     * 获取当前连接信息
     *
     * @return ConnectionInfo对象
     */
    public WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }

    /**
     * 获取当前连接的SSID
     *
     * @return 当前连接的SSID
     */
    public String getConnectedSSID() {
        WifiInfo wifiInfo = getConnectionInfo();
        if (null != wifiInfo) {
            String ssid = wifiInfo.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            return ssid;
        }
        return null;
    }

    /**
     * 获取当前连接的netId
     *
     * @return 当前连接的netId
     */
    public int getConnectedNetId() {
        WifiInfo wifiInfo = getConnectionInfo();
        if (null != wifiInfo) {
            return wifiInfo.getNetworkId();
        }
        return -1;
    }

    /**
     * 获取扫描结果
     *
     * @return ScanResult的列表
     */
    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }

    /**
     * 计算wifi的信号想读
     *
     * @param rssi      热点信号强度
     * @param numLevels 最大等级
     * @return 0-numLevels
     */
    public static int calculateSignalLevel(int rssi, int numLevels) {
        return WifiManager.calculateSignalLevel(rssi, numLevels);
    }

    /**
     * 获取当前设备已保存的配置信息
     *
     * @return 本地WifiConfiguration的列表
     */
    public List<WifiConfiguration> getConfiguredNetworks() {
        mWifiConfList = mWifiManager.getConfiguredNetworks();
        return mWifiConfList;
    }

    /**
     * 获取指定配置信息的加密类型
     *
     * @param config 指定配置信息
     * @return WifiCipherType
     */
    public WifiCipherType getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WifiCipherType.WIFI_CIPHER_WPA_PSK;
        } else if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return WifiCipherType.WIFI_CIPHER_WPA_EAP;
        }
        return (config.wepKeys[0] != null) ? WifiCipherType.WIFI_CIPHER_WEP
                : WifiCipherType.WIFI_CIPHER_NOPASS;
    }

    /**
     * 获取指定配置信息的加密类型
     *
     * @param scanResult 指定配置信息
     * @return WifiCipherType
     */
    public WifiCipherType getSecurity(ScanResult scanResult) {
        WifiCipherType type;
        if (scanResult.capabilities.contains("WPA2-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
        } else if (scanResult.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
        } else if (scanResult.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
        } else if (scanResult.capabilities.contains("WEP")) {
            // WEP加密
            type = WifiCipherType.WIFI_CIPHER_WEP;
        } else {
            // 无密码
            type = WifiCipherType.WIFI_CIPHER_NOPASS;
        }
        return type;
    }

    /**
     * 判断扫描到的热点是否需要密码
     *
     * @param scanResult 热点信息
     * @return 需要密码时返回true
     */
    public boolean isNeedPasswod(ScanResult scanResult) {
        if (WifiCipherType.WIFI_CIPHER_NOPASS == getSecurity(scanResult)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断WIFI是否已打开
     *
     * @return 已打开时返回true
     */
    public boolean isWifiOpened() {
        boolean isOpen = true;
        int wifiState = getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_DISABLED
                || wifiState == WifiManager.WIFI_STATE_DISABLING
                || wifiState == WifiManager.WIFI_STATE_UNKNOWN
                || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            isOpen = false;
        }
        return isOpen;
    }

    /**
     * 判断指定WIFI在本地是否有配置
     *
     * @param SSID WIFI热点名称
     * @return 本地存在配置信息，返回WifiConfiguration配置信息
     */
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (null != existingConfigs) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                String t = "\"";
                if (SSID.startsWith("\"") && SSID.endsWith("\"")) {
                    t = "";
                }
                if (existingConfig.SSID.toString().equals(t + SSID + t)) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    /**
     * 使用密码连接指定热点，无密码时自动连接
     *
     * @param scanResult 指定热点
     * @param password   密码
     * @return 更新成功返回true，但不代表连接成功，需要判断网络变化或者循环 判断是否连接成功
     */
    public boolean connectWifi(ScanResult scanResult, String password) {
        boolean result = false;
        if (null == scanResult) {
            LogUtils.e(TAG, "scanResult is null");
            return result;
        }
        WifiConfiguration wifiConf = isExsits(scanResult.SSID);
        if (null != wifiConf && -1 != wifiConf.networkId && null == password) {
            LogUtils.i(TAG, "Current wifi \'" + scanResult.SSID + "\': " + wifiConf.networkId);
            // 只表示可以连接，但是密码是否正确只能监听网络变化来判断。所以不作为连接成功的标志。
            result = enableNetwork(wifiConf.networkId);
            LogUtils.i(TAG, "Connect current wifi \'" + scanResult.SSID + "\': " + result
                    + ", need listener network change");
        } else if (!isNeedPasswod(scanResult)) {
            wifiConf = getWifiConfiguration(scanResult, null);
            result = connectWifi(wifiConf);
            LogUtils.i(TAG,
                    "Wifi \'" + scanResult.SSID + "\' not need password, connect: " + result);
        } else if (!TextUtils.isEmpty(password)) {
            LogUtils.i(TAG, "Wifi \'" + scanResult.SSID + "\': not exsits and need password");
            wifiConf = getWifiConfiguration(scanResult, password);
            result = connectWifi(wifiConf);
            LogUtils.i(TAG, "Connect wifi by password \'" + scanResult.SSID + "\': " + result
                    + ", need listener network change");
        } else {
            LogUtils.i(TAG, "Connect wifi false, password is null");
        }
        return result;
    }

    /**
     * 连接指定配置信息，若有密码，密码应该被包含在参数里
     *
     * @param wifiConf 配置信息
     * @return 更新成功返回true，但不代表连接成功，需要判断网络变化或者循环 判断是否连接成功
     */
    public boolean connectWifi(WifiConfiguration wifiConf) {
        boolean result = false;
        if (null != wifiConf) {
            if (wifiConf.SSID.equals(getConnectedSSID())) {
                result = true;
            } else {
                int netId = wifiConf.networkId;
                if (-1 != netId) {
                    LogUtils.i(TAG, "update old wifi id: " + netId);
                    updateNetwork(wifiConf);
                } else {
                    netId = addNetwork(wifiConf);
                }
                LogUtils.i(TAG, "connect netId: " + netId);
                if (-1 != netId && enableNetwork(netId)) {
                    saveConfiguration();
                    result = true;
                    LogUtils.i(TAG, "Connect wifi \'" + wifiConf.SSID + "\': " + result
                            + ", need listener network change");
                }
            }
        } else {
            LogUtils.e(TAG, "wifiConf is null");
        }
        return result;
    }

    /**
     * 添加网络配置
     *
     * @param wifiConf 网络配置信息
     * @return 添加成功返回netId，本地已存在或者添加失败返回-1
     */
    public int addNetwork(WifiConfiguration wifiConf) {
        return mWifiManager.addNetwork(wifiConf);
    }

    /**
     * 启用指定netId的连接
     *
     * @param netId 指定WIFI连接的netId
     * @return 操作成功返回true，但不代表连接成功
     */
    public boolean enableNetwork(int netId) {
        return mWifiManager.enableNetwork(netId, true);
    }

    /**
     * 停用指定netId的连接
     *
     * @param netId 指定热点的netId
     * @return 操作成功返回true，异步方法，需要循环判断是否已停用
     */
    public boolean disableNetwork(int netId) {
        return mWifiManager.disableNetwork(netId);
    }

    /**
     * 忽略当前已连接的wifi热点
     *
     * @return 忽略成功返回true，当前未连接、停用失败、断开连接失败、移除失败、保存配置信息失败皆返回false
     */
    public boolean ignoreConnectedWifi() {
        boolean result = false;
        int netId = getConnectedNetId();
        if (-1 != netId && disableNetwork(netId) && disconnect() && removeNetwork(netId)
                && saveConfiguration()) {
            result = true;
        }
        return result;
    }

    /**
     * 重新连接WIFI
     *
     * @return 操作成功返回true
     */
    public boolean reconnect() {
        return mWifiManager.reconnect();
    }

    /**
     * 断开WIFI连接
     *
     * @return 操作成功返回true
     */
    public boolean disconnect() {
        return mWifiManager.disconnect();
    }

    /**
     * 移除指定网络
     *
     * @param netId 需移除网络的netId
     * @return 操作成功返回true
     */
    public boolean removeNetwork(int netId) {
        return mWifiManager.removeNetwork(netId);
    }

    /**
     * 更新网络配置
     *
     * @param wifiConf 配置信息
     * @return 操作成功返回true
     */
    public int updateNetwork(WifiConfiguration wifiConf) {
        return mWifiManager.updateNetwork(wifiConf);
    }

    /**
     * 保存配置信息
     *
     * @return 操作成功返回true
     */
    public boolean saveConfiguration() {
        return mWifiManager.saveConfiguration();
    }

    /**
     * 根据扫描到的热点和密码，获取配置信息，用于连接该热点
     *
     * @param scanResult 扫描到的热点
     * @param passwd     密码
     * @return WifiConfiguration对象
     */
    public WifiConfiguration getWifiConfiguration(ScanResult scanResult, String passwd) {
        WifiConfiguration config = this.isExsits(scanResult.SSID);
        if (config != null) {
            // 本机之前配置过此wifi热点，调整优先级后，直接返回
            return setMaxPriority(config);
        }

        int priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }
        config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = scanResult.SSID;
        config.status = WifiConfiguration.Status.ENABLED;
        config.priority = priority; // 2147483647;

        WifiCipherType type;
        if (scanResult.capabilities.contains("WPA2-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
        } else if (scanResult.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
        } else if (scanResult.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
        } else if (scanResult.capabilities.contains("WEP")) {
            // WEP加密
            type = WifiCipherType.WIFI_CIPHER_WEP;
        } else {
            // 无密码
            type = WifiCipherType.WIFI_CIPHER_NOPASS;
        }
        /* 各种加密方式判断 */
        if (type == WifiCipherType.WIFI_CIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.WIFI_CIPHER_WEP) {
            config.preSharedKey = "\"" + passwd + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_EAP) {
            config.preSharedKey = "\"" + passwd + "\"";
            config.hiddenSSID = true;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
                    | WifiConfiguration.Protocol.WPA);
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK) {
            config.preSharedKey = "\"" + passwd + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
                    | WifiConfiguration.Protocol.WPA);
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK) {
            config.preSharedKey = "\"" + passwd + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        } else {
            return null;
        }
        return config;
    }

    private int getMaxPriority() {
        int i = 0;
        List<WifiConfiguration> localList = getConfiguredNetworks();
        if (null == localList) {
            return i;
        }
        Iterator<WifiConfiguration> localIterator = localList.iterator();
        while (true) {
            if (!localIterator.hasNext()) {
                return i;
            }
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator.next();
            if (localWifiConfiguration.priority <= i) {
                continue;
            }
            i = localWifiConfiguration.priority;
        }
    }

    private void sortByPriority(List<WifiConfiguration> paramList) {
        Collections.sort(paramList, new WifiManagerCompare());
    }

    private int shiftPriorityAndSave() {
        List<WifiConfiguration> localList = getConfiguredNetworks();
        sortByPriority(localList);
        int i = localList.size();
        for (int j = 0; ; ++j) {
            if (j >= i) {
                saveConfiguration();
                return i;
            }
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localList.get(j);
            localWifiConfiguration.priority = j;
            updateNetwork(localWifiConfiguration);
        }
    }

    private WifiConfiguration setMaxPriority(WifiConfiguration config) {
        int priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }
        config.priority = priority;
        updateNetwork(config);
        // 本机之前配置过此wifi热点，直接返回
        return config;
    }

    class WifiManagerCompare implements Comparator<WifiConfiguration> {
        public int compare(WifiConfiguration paramWifiConfiguration1,
                           WifiConfiguration paramWifiConfiguration2) {
            return paramWifiConfiguration1.priority - paramWifiConfiguration2.priority;
        }
    }

}
