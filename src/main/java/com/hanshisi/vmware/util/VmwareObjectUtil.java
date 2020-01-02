package com.hanshisi.vmware.util;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxhan on 2018/7/3.
 * @author rxhan
 */
public class VmwareObjectUtil implements AutoCloseable{

    public static final String VIRTUAL_MACHINE = "VirtualMachine";

    public static final String FOLDER = "Folder";

    public static final String RESOURCE_POOL = "ResourcePool";

    public static final String CLUSTER_COMPUTE_RESOURCE = "ClusterComputeResource";

    public static final String DISTRIBUTED_VIRTUAL_PORTGROUP = "DistributedVirtualPortgroup";

    private Logger logger = LoggerFactory.getLogger(VmwareObjectUtil.class);

    private ServiceInstance serviceInstance;

    public VmwareObjectUtil(){
    }

    public VmwareObjectUtil(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }


    /**
     * 根据resource pool name 获取  ResourcePool 对象
     */
    public ResourcePool getResourcePool(String name) {
        ResourcePool resourcePool = getManagedEntity(RESOURCE_POOL,name);
        return resourcePool;
    }


    /**
     * 根据clusterName 获取 resource pool 对象
     */
    public ResourcePool getResourcePoolByClusterName(String clusterName) {
        ClusterComputeResource clusterComputeResource = getManagedEntity(CLUSTER_COMPUTE_RESOURCE,clusterName);
        ResourcePool resourcePool = null;
        if (clusterComputeResource!=null){
            resourcePool = clusterComputeResource.getResourcePool();
        }
        return resourcePool;
    }


    /**
     * 获取环境中的管理对象列表
     * 包括 Datacenter  ClusterComputeResource  HostSystem VirtualMachine 等
     */
    public <T> List<T> getManagedObjectList(String type){
        List<T> managedObjectList  = new ArrayList<T>();
        InventoryNavigator inventoryNavigator = getInventoryNavigator();
        try {
            ManagedEntity[] managedEntities = inventoryNavigator.searchManagedEntities(type);
            if(managedEntities!=null && managedEntities.length>0){
                for (ManagedEntity managedEntity:managedEntities){
                    managedObjectList.add((T)managedEntity);
                }
            }else {
                logger.info("管理对象 "+type+" 列表为空");
            }
            return managedObjectList;
        } catch (RemoteException e) {
            logger.info(this.getClass().toString()+" getManagedObjectList error");
            logger.info("管理对象 "+type+" 异常 请检查VMware环境中该类型对象是否存在");
            e.printStackTrace();
            return managedObjectList;
        }
    }

    /**
     * 获取管理对象实体类
     */
    public <T> T  getManagedEntity(String type,String name){
        InventoryNavigator inventoryNavigator = getInventoryNavigator();
        ManagedEntity managedEntity = null;
        try {
            managedEntity = inventoryNavigator.searchManagedEntity(type, name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return (T)managedEntity;
    }

    public InventoryNavigator getInventoryNavigator(){
        Folder rootFolder = serviceInstance.getRootFolder();
        InventoryNavigator inventoryNavigator = new InventoryNavigator(rootFolder);
        return inventoryNavigator;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * 根据虚机的uuid 查询虚机
     */

    public VirtualMachine getVirtualMachineByuuid(String uuid){
        VirtualMachine virtualMachine;
        ManagedObjectReference managedObjectReference=serviceInstance.getServiceContent().getSearchIndex();
        SearchIndex searchIndex=new SearchIndex(serviceInstance.getServerConnection(),managedObjectReference);
        ManagedEntity entity;
        try {
            entity = searchIndex.findByUuid(null,uuid,true);
            virtualMachine=(VirtualMachine)entity;
        } catch (RemoteException e) {
            e.printStackTrace();
            virtualMachine = null;
        }
        return virtualMachine;
    }

    @Override
    public void close() throws Exception {
        serviceInstance.getServerConnection().logout();
    }
}
