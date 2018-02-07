
package com.rengh.library.util.common;

import android.app.Notification;
import android.app.Service;

/**
 * Service工具类。
 * 
 * @since 2017.01.26
 * @author RenGuanghui
 * @see 1、将service设置为前台状态，避免service空闲或系统内存不足时被杀掉。
 * @see 2、将service的前台状态取消，service空闲或系统内存不足时可被杀掉。
 */
public class ServiceUtils {

    /**
     * 设置前台service状态。无应用名称、图标时设置会失败。
     * 
     * @param service 需要操作的service对象，不许为null，为null时返回false。
     * @param id 前台service占用notification的id，需传入非0值。
     * @return boolean service为null时返回false，其他返回true。
     */
    public static boolean startForeground(Service service, int id) {
        return setForeground(service, true, id, false);
    }

    /**
     * 取消service前台状态。
     * 
     * @param service 需要操作的service对象，不许为null，为null时返回false。
     * @param removeNotification 是否移除通知。true或false，只有参数enable为false时才起作用。
     * @return boolean service为null时返回false，其他返回true。
     */
    public static boolean stopForeground(Service service, boolean removeNotification) {
        return setForeground(service, false, 0, removeNotification);
    }

    /**
     * 启用或取消service前台状态的具体实现。
     * 
     * @param service 需要操作的service对象，不许为null，为null时返回false。
     * @param enable 
     *            启用或取消前台状态，true或false，为true时removeNotification参数不起作用，为false时id参数不起作用
     * @param id 前台service占用notification的id，需传入非0值。
     * @param removeNotification 是否移除通知。true或false，只有参数enable为false时才起作用。
     * @return boolean service为null时返回false，其他返回true。
     */
    private static boolean setForeground(Service service, boolean enable, int id,
            boolean removeNotification) {
        if (null == service) { // service不许为null
            return false;
            // throw new IllegalArgumentException();
        }

        if (enable && 0 >= id) { // 启用前台service状态时，id必须大于0
            return false;
            // throw new IllegalArgumentException();
        }

        if (enable) { // 启用前台service状态
            Notification notification = new Notification();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
            service.startForeground(id, notification);
        } else { // 取消前台service状态
            service.stopForeground(removeNotification);
        }

        return true;
    }
}
