package com.lusfold.yam.repository;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.lusfold.yam.YamApplication;
import com.lusfold.yam.repository.bean.Device;
import com.lusfold.yam.utils.DeviceUtils;
import com.lusfold.yam.utils.SharePreferenceUtils;
import com.lusfold.yam.utils.StringUtils;

import java.util.List;

/**
 * Created by lusfold on 4/29/16.
 */
public class DeviceService {
    private static DeviceService instance;

    private DeviceService() {
    }

    /**
     * @return
     */
    public static DeviceService getInstance() {
        if (instance == null) {
            synchronized (DeviceService.class) {
                if (instance == null) {
                    instance = new DeviceService();
                }
            }
        }
        return instance;
    }

    /**
     * @return
     */
    public Device getDevice() {
        String deviceStr = SharePreferenceUtils.getString(YamApplication.getInstance(), SharePreferenceUtils.KEY_DEVICE);
        Device device = null;
        if (!StringUtils.isEmpty(deviceStr)) {
            device = JSON.parseObject(deviceStr, Device.class);
        }
        if (device == null) {
            device = new Device();
            String osType = DeviceUtils.getOS();
            String udid = DeviceUtils.getUDID();
            device.setOs(osType);
            device.setUdid(udid);
            saveDevice(device);
        }
        return device;
    }

    /**
     * @param device
     * @return
     */
    public void uploadDevice(final Device device, final SaveCallback callback) {
        isDeviceExists(device, new FindCallback<Device>() {
            @Override
            public void done(List<Device> list, AVException e) {
                if (e!=null){
                    callback.done(e);
                }else{
                    if (list.size() == 0){
                        device.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e!=null){
                                    callback.done(e);
                                }else{
                                    saveDevice(device);
                                    callback.done(null);
                                }
                            }
                        });
                    }else{
                        saveDevice(list.get(0));
                        callback.done(null);
                    }
                }
            }
        });
    }

    /**
     * @param device
     * @return
     */
    public void isDeviceExists(final Device device, final FindCallback<Device> callback) {
        AVQuery<Device> query = new AVQuery<>("Device");
        query.whereEqualTo(Device.FIELD_UDID, device.getUdid());
        query.findInBackground(callback);
    }

    /**
     * @param device
     */
    public void saveDevice(Device device) {
        SharePreferenceUtils.putString(YamApplication.getInstance(), SharePreferenceUtils.KEY_DEVICE, JSON.toJSONString(device));
    }

    /**
     * @param device
     * @return
     */
    public boolean isDeviceLegal(Device device) {
        return !StringUtils.isEmpty(device.getObjectId());
    }
}
