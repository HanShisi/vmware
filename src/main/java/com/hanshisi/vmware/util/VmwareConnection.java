package com.hanshisi.vmware.util;

import com.vmware.vim25.mo.ServiceInstance;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Created by rxhan on 2020/1/2.
 * 获取VMware连接的工具类
 * @author rxhan
 */
public class VmwareConnection {

    /**
     * 建立同vmware环境的连接并返回一个ServiceInstance的实例
     * @param ipAddress vmware的连接地址
     * @param userName 连接vmware的用户名
     * @param password 连接vmware的密码
     * @return ServiceInstance实例
     */
    public static ServiceInstance createConnect(String ipAddress, String userName, String password){
        ServiceInstance  serviceInstance = null;
        try {
            serviceInstance = new ServiceInstance(new URL("https://" + ipAddress + "/sdk"), userName, password, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return serviceInstance;
    }

    /**
     *断开VMware连接
     */
    public static void disConnect(ServiceInstance  connect){
        connect.getServerConnection().logout();
    }

}
