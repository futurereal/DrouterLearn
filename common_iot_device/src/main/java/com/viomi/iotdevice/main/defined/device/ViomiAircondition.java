/* This file is auto-generated.*/

package com.viomi.iotdevice.main.defined.device;

import com.viomi.iotdevice.main.defined.service.AirconditionService;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.operable.DeviceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.urn.DeviceType;

public class ViomiAircondition extends DeviceOperable {

    private static final DeviceType DEVICE_TYPE = new DeviceType("Viomi", "ViomiAircondition", "1");

    private AirconditionService _AirconditionService = new AirconditionService(false);

    public ViomiAircondition(MiotDeviceConfig config) {
        super(DEVICE_TYPE);
        super.setDiscoveryTypes(config.discoveryTypes());
        super.setFriendlyName(config.friendlyName());
        super.setDeviceId(config.deviceId());
        super.setMacAddress(config.macAddress());
        super.setManufacturer(config.manufacturer());
        super.setModelName(config.modelName());
        super.setMiotToken(config.miotToken());
        super.setMiotInfo(config.miotInfo());
        super.addService(_AirconditionService);
        super.initializeInstanceID();
    }

    public AirconditionService AirconditionService() {
        return _AirconditionService;
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