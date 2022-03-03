package com.viomi.serialport;


/**
 * 串口通信调用本地方法
 *
 * @author zhouzy
 */
public class jniSERIAL {
    /**
     * 最大写入字节数
     */
    public static final int MAX_WRITE_LENGTH = 800;


    /**
     * 读数据缓存，serial_read() 接口返回的数据保存在这里，调用 serial_read 之后从这里读数据
     * 注意：一个 int 保存一个 byte 数据，也就是说 int[32] 实际保存的是 32 个 byte，而不是 32 * 4 = 128 bytes
     */
    public int[] rd_data = new int[259];

    /**
     * 写数据缓存, 调用 serial_write() 之前先把数据写入这里
     * 注意：一个 int 保存一个 byte 数据，也就是说 int[32] 实际保存的是 32 个 byte，而不是 32 * 4 = 128 bytes
     */
    public int[] wr_data = new int[259];

    /**
     * 调试开关，调试设备没有串口 so 的时候这个开关打开
     */
    public static boolean no_serial_lib = false;

    /**
     * 初始化串口设备
     *
     * @param param:    jniSERIAL 对象实例
     * @param pathstr:  串口路径,如"/dev/ttyMT2""
     * @param baudRate: 波特率
     * @param length:   字长 bit 数，8 位数据就是 8
     * @param parity_c: 奇偶校验位，'e' = even, 'o' = old，'n' = none
     * @param stopBit:  是否有停止位，1 = 有，0 = 没有
     */
    public native int init(Object param, String pathstr, int baudRate, int length, char parity_c, int stopBit);

    /**
     * 读取数据
     *
     * @param param: jniSERIAL 对象实例
     * @return 实际读取字节数
     */
    public native int serial_read(Object param);

    /**
     * 写入数据
     *
     * @param param:  jniSERIAL 对象实例
     * @param length: 写入数据长度
     * @return 实际写入长度
     */
    public native int serial_write(Object param, int length);

    /***
     * 获取 jni 软件版本
     */
    public native int get_version();

    /**
     * 打开 jni log
     */
    public native int log_enable(boolean enable);

    /**
     * 关闭串口
     */
    public native int exit();

    static {
        try {
            System.loadLibrary("viomi_serial");
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            no_serial_lib = true;
        }
    }
}
