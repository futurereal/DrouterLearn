package com.viomi.ovensocommon.rxbus;

public class ViomiRxBusEvent {

    private final int msgId;
    private final Object msgObgect;

    public ViomiRxBusEvent(int msgId, Object msgObgect) {
        this.msgId = msgId;
        this.msgObgect = msgObgect;
    }

    public int getMsgId() {
        return msgId;
    }

    public Object getMsgObject() {
        return msgObgect;
    }

    @Override
    public String toString() {
        return "ViomiRxBusEvent{" +
                "msgId=" + msgId +
                ", msgObgect=" + msgObgect +
                '}';
    }
}
