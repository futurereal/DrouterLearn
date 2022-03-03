/* This file is auto-generated.*/

package com.miotspecv2.defined.device;

import com.miotspecv2.defined.service.Custommode;
import com.miotspecv2.defined.service.Customoven;
import com.miotspecv2.defined.service.Customrecipes;
import com.miotspecv2.defined.service.Safetycheck;
import com.miotspecv2.defined.service.Oven;
import com.miotspecv2.defined.service.Deviceinformation;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.operable.DeviceOperable;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.urn.DeviceType;

public class OvenDevice extends DeviceOperable {

    private static final DeviceType DEVICE_TYPE = new DeviceType("miotspecv2", "oven", "1");

    private final Custommode _custommode = new Custommode(false);
    private final Customoven _customoven = new Customoven(false);
    private final Customrecipes _customrecipes = new Customrecipes(false);
    private final Safetycheck _safetycheck = new Safetycheck(false);
    private final Oven _oven = new Oven(false);
    private final Deviceinformation _deviceinformation = new Deviceinformation(false);

    public OvenDevice(MiotDeviceConfig config) {
        super(DEVICE_TYPE);
        super.setDiscoveryTypes(config.discoveryTypes());
        super.setFriendlyName(config.friendlyName());
        super.setDeviceId(config.deviceId());
        super.setMacAddress(config.macAddress());
        super.setManufacturer(config.manufacturer());
        super.setModelName(config.modelName());
        super.setMiotToken(config.miotToken());
        super.setMiotInfo(config.miotInfo());
        super.addService(_custommode);
        super.addService(_customoven);
        super.addService(_customrecipes);
        super.addService(_safetycheck);
        super.addService(_oven);
        super.addService(_deviceinformation);
        //super.initializeInstanceID();
    }

    public Deviceinformation deviceinformation() {
        return _deviceinformation;
    }

    public Oven oven() {
        return _oven;
    }

    public Customoven customoven() {
        return _customoven;
    }

    public Customrecipes customrecipes() {
        return _customrecipes;
    }

    public Custommode custommode() {
        return _custommode;
    }

    public Safetycheck safetycheck() {
        return _safetycheck;
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