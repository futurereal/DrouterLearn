/* This file is auto-generated.*/

package com.viomi.iotdevice.main.newdefined.device;


import com.viomi.iotdevice.main.newdefined.service.Deviceinformation;
import com.viomi.iotdevice.main.newdefined.service.Fridge;
import com.viomi.iotdevice.main.newdefined.service.Fridgeanotherchamber;
import com.viomi.iotdevice.main.newdefined.service.Fridgechamber;
import com.viomi.iotdevice.main.newdefined.service.Fridgefactory;
import com.viomi.iotdevice.main.newdefined.service.Fridgepanel;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.operable.DeviceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.urn.DeviceType;

public class FridgeDevice extends DeviceOperable {

    private static final DeviceType DEVICE_TYPE = new DeviceType("miotspecv2", "fridge", "1");

    private Fridgefactory _fridgefactory = new Fridgefactory(false);
    private Fridge _fridge = new Fridge(false);
    private Fridgechamber _fridgechamber = new Fridgechamber(false);
    private Fridgeanotherchamber _fridgechamberanother = new Fridgeanotherchamber(false);
    private Fridgepanel _fridgepanel = new Fridgepanel(false);
    private Deviceinformation _deviceinformation = new Deviceinformation(false);

    public FridgeDevice(MiotDeviceConfig config) {
        super(DEVICE_TYPE);
        super.setDiscoveryTypes(config.discoveryTypes());
        super.setFriendlyName(config.friendlyName());
        super.setDeviceId(config.deviceId());
        super.setMacAddress(config.macAddress());
        super.setManufacturer(config.manufacturer());
        super.setModelName(config.modelName());
        super.setMiotToken(config.miotToken());
        super.setMiotInfo(config.miotInfo());
        super.addService(_fridgefactory);
        super.addService(_fridge);
        super.addService(_fridgechamber);
        super.addService(_fridgechamberanother);
        super.addService(_fridgepanel);
        super.addService(_deviceinformation);
        //super.initializeInstanceID();
    }

    public Fridgefactory fridgefactory() {
        return _fridgefactory;
    }
    public Fridge fridge() {
        return _fridge;
    }
    public Fridgechamber fridgechamber() {
        return _fridgechamber;
    }
    public Fridgeanotherchamber fridgeanotherchamber(){
        return _fridgechamberanother;
    }
    public Fridgepanel fridgepanel() {
        return _fridgepanel;
    }
    public Deviceinformation deviceinformation() {
        return _deviceinformation;
    }

    public void start(CompletedListener listener) throws MiotException {
        MiotHostManager.getInstance().register(this, listener, this);
    }

    public void stop(CompletedListener listener) throws MiotException {
        MiotHostManager.getInstance().unregister(this, listener);
    }

    public void sendEvents() throws MiotException {
        MiotHostManager.getInstance().sendEvent(super.getChangedProperties(), new CompletedListener() {
            @Override
            public void onSucceed(String result) {

            }

            @Override
            public void onFailed(MiotError error) {

            }
        });
    }

    public void send(String method, String params) throws MiotException {
        MiotHostManager.getInstance().send(method, params, new CompletedListener() {
            @Override
            public void onSucceed(String result) {

            }

            @Override
            public void onFailed(MiotError error) {

            }
        });
    }
}