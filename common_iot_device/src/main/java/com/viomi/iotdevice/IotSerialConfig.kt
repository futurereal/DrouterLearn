package com.viomi.iotdevice

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/10/22
 *     desc   : Iot 串口相关配置
 *     version: 1.0
 * </pre>
 */
class IotSerialConfig(builder: Builder) {
    var iotType: IotType
    var mac: String?
    var did: String?
    var mcuSerialPath: String
    var mcuSerialBaudRate: Int
    var wifiSerialPath: String?
    var wifiSerialBaudRate: Int
    var isDebugEnv: Boolean
    var readPeriod: Long
    var errorCount: Int

    class Builder {
        var iotType = IotType.PROTOCOL_MIOT
            private set
        var mac: String? = null
            private set
        var did: String? = null
            private set
        var mcuSerialPath: String = ""
            private set
        var mcuSerialBaudRate = 0
            private set
        var wifiSerialPath: String? = null
            private set
        var wifiSerialBaudRate = 0
            private set
        var isDebugEnv = false
            private set
        var readPeriod: Long = 0
            private set
        var errorCount: Int = 20
            private set

        fun setIotType(iotType: IotType): Builder {
            this.iotType = iotType
            return this
        }

        fun setMac(mac: String?): Builder {
            this.mac = mac
            return this
        }

        fun setMcuSerialPath(mcuSerialPath: String): Builder {
            this.mcuSerialPath = mcuSerialPath
            return this
        }

        fun setMcuSerialBaudRate(mcuSerialBaudRate: Int): Builder {
            this.mcuSerialBaudRate = mcuSerialBaudRate
            return this
        }

        fun setWifiSerialPath(wifiSerialPath: String?): Builder {
            this.wifiSerialPath = wifiSerialPath
            return this
        }

        fun setWifiSerialBaudRate(wifiSerialBaudRate: Int): Builder {
            this.wifiSerialBaudRate = wifiSerialBaudRate
            return this
        }

        fun setDebugEnv(isDebugEnv: Boolean): Builder {
            this.isDebugEnv = isDebugEnv
            return this
        }

        fun setDid(did: String?): Builder {
            this.did = did
            return this
        }

        fun setReadPeriod(readPeriod: Long): Builder {
            this.readPeriod = readPeriod
            return this
        }

        fun setErrorCount(errorCount: Int): Builder {
            this.errorCount = errorCount
            return this
        }

        fun build(): IotSerialConfig {
            return IotSerialConfig(this)
        }
    }

    enum class IotType(var value: Int) {
        PROTOCOL_MIOT(1), PROTOCOL_MIOT_SPEC(2), PROTOCOL_VIOT(3);

        fun value(): Int {
            return value
        }
    }

    init {
        iotType = builder.iotType
        mac = builder.mac
        did = builder.did
        mcuSerialPath = builder.mcuSerialPath
        mcuSerialBaudRate = builder.mcuSerialBaudRate
        wifiSerialPath = builder.wifiSerialPath
        wifiSerialBaudRate = builder.wifiSerialBaudRate
        isDebugEnv = builder.isDebugEnv
        readPeriod = builder.readPeriod
        errorCount = builder.errorCount
    }
}
