package com.viomi.iotdevice.common.protocol;

import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

import io.reactivex.rxjava3.core.Observable;


public abstract  class Xmodem {

    final String TAG = "Xmodem";
    // 开始符
    private final byte SOH = 0x01;
    // 结束符
    private final byte EOT = 0x04;
    // 应答符
    private final byte ACK = 0x06;

    // 重传
    private final byte NAK = 0x15;
    // 无条件结束
    private final byte CAN = 0x18;
    //字符C
    private final byte CRC = 'C';//大写的C在ASCII码值67,0x43

    // 以128字节块的形式传输数据
    private final int SECTOR_SIZE = 128;
    // 最大错误（无应答）包数
    private final int MAX_TRY = 10;

    private boolean mIsCRC;//是否CRC校验，false代表是检验和，非CRC


    public abstract   byte[]    receiveComData();//读取串口数据
    public abstract   boolean   sendComData(byte[] data);//发送串口数据

    /***
     * xmodem收发流程
     * @param data
     * 传输数据包为128B，加上Header,Packet Number,CherckSum共计133字节。
     * Xmodem传输数据握手时，第一个NAK或C用于确定校验方式，第二个NAK或C开始传输数据。因此超时时间不宜过长
     * 传输最后一个包时，如果有效数据不足128B，则补齐为1A FF FF FF…若128B,则补加一个数据包，数据为1A FF FF
     */
    public Observable<Integer> process(byte[] data){

       return Observable.create(e -> {

           if (data == null || data.length <= 128) {//数据空获取固件包太小
               Log.e(TAG, "data error");
               e.onError(new Throwable("data error"));
               return;
           }
           int progress;//发送进度

           //接收协商数据，NAK为校验和方式，'C'为CRC校验方式
           int timeCount = 0;
           while (true) {
               SystemClock.sleep(1000);
               byte[] mode = receiveComData();
               if (mode != null && mode.length > 0) {
                   if (mode[0] == NAK) {
                       mIsCRC = false;
                       break;
                   } else if (mode[0] == CRC) {
                       mIsCRC = true;
                       break;
                   }else if (mode[0] == CAN) {
                       Log.e(TAG, "mcu cancel");
                       e.onError(new Throwable("mcu cancel"));
                       return;
                   }
               }
               timeCount++;
               if (timeCount > 10) {
                   Log.e(TAG, "verify timeout");
                   e.onError(new Throwable("verify timeout"));
                   return;
               }
           }
           progress = 10;
           e.onNext(progress);

           //发送数据
           int dataLeng = data.length;
           int dataIndex = 0;
           byte[] block = new byte[SECTOR_SIZE];
           int packnumber = 0;
           int progressLeng = dataLeng / 80;//数据发送占总进度80%，1/80数据块的长度
           do {
               //组合数据包
               int remainLeng = dataLeng - dataIndex;
               if (remainLeng >= SECTOR_SIZE) {   //未到数组尾，剩余长于128，直接复制
                   System.arraycopy(data, dataIndex, block, 0, SECTOR_SIZE);
               } else {
                   if (remainLeng == 0) {           //刚好到数组尾，新加一个数据包
                       Arrays.fill(block, (byte) 0xFF);
                       block[0] = (byte) 0x1A;
                   } else {                      //未到数组尾，剩余短于128，复制后补齐
                       System.arraycopy(data, dataIndex, block, 0, remainLeng);
                       block[remainLeng] = (byte) 0x1A;
                       for (int i = remainLeng; i < SECTOR_SIZE; i++) {
                           block[i] = (byte) 0xFF;
                       }
                   }
               }
               dataIndex += SECTOR_SIZE;

               //打包数据
               packnumber++;
               if (packnumber > 255) {
                   packnumber = 0;//超过255后从0开始
               }
               byte[] sendDataArray;
               if (mIsCRC) {
                   ByteBuffer packBuffer = ByteBuffer.allocate(133);
                   packBuffer.put(SOH);
                   byte numberByte = (byte) (packnumber & 0xFF);
                   packBuffer.put(numberByte);
                   packBuffer.put((byte) ~numberByte);
                   packBuffer.put(block);
                   packBuffer.put((byte) 0);
                   packBuffer.put((byte) 0);
                   sendDataArray = packBuffer.array();
                   int crc = CRC16.calcCrc16(block);
                   sendDataArray[sendDataArray.length - 2] = (byte) ((crc & 0xff00) >> 8);
                   sendDataArray[sendDataArray.length - 1] = (byte) (crc & 0x00ff);
               } else {
                   ByteBuffer packBuffer = ByteBuffer.allocate(132);
                   packBuffer.put(SOH);
                   byte numberByte = (byte) (packnumber & 0xFF);
                   packBuffer.put(numberByte);
                   packBuffer.put((byte) ~numberByte);
                   packBuffer.put(block);
                   packBuffer.put((byte) 0);
                   sendDataArray = packBuffer.array();
                   byte checkSum = getCheckSum(sendDataArray, 0, sendDataArray.length);
                   sendDataArray[sendDataArray.length - 1] = checkSum;
               }


               //数据发送,失败重发,最多10次
               int tryCount = 0;
               boolean sendSuccess = false;
               while (true) {
                   int perTimeCount = 0;
                   sendComData(sendDataArray);
                   while (true) {
                       SystemClock.sleep(100);
                       byte[] ask = Xmodem.this.receiveComData();
                       if (ask != null && ask.length >= 1) {
                           if (ask[0] == NAK) {
                               break;
                           } else if (ask[0] == ACK) {
                               sendSuccess = true;
                               break;
                           }else if (ask[0] == CAN) {
                               Log.e(TAG, "mcu cancel");
                               e.onError(new Throwable("mcu cancel"));
                               return;
                           }
                       }
                       perTimeCount++;
                       if (perTimeCount > 10) {   //超过1s收不到回复，报异常
                           Log.e(TAG, "send data timeout");
                           e.onError(new Throwable("send data timeout"));
                           return;
                       }
                   }
                   if (sendSuccess) {
                       break;
                   }
                   tryCount++;
                   if (tryCount > MAX_TRY) {
                       Log.e(TAG, "send data timeout");
                       e.onError(new Throwable("send data timeout"));
                       return;
                   }
               }

               if (progress != dataIndex / progressLeng) {//只有变动1%以上才回调
                   progress = dataIndex / progressLeng;
                   e.onNext(progress+10);
               }
           } while (dataIndex <= dataLeng);

           //发送结束符
           try {
               if (!Xmodem.this.sendEot()) {
                   Log.e(TAG, "send eot timeout");
                   e.onError(new Throwable("send eot timeout"));
                   return;
               } else {
                   e.onNext(100);
               }
           }catch (CancelException exception){
               Log.e(TAG, "mcu cancel");
               e.onError(new Throwable("mcu cancel"));
               return;
           }
           e.onComplete();
       });

    }


    /***
     * 发送数据结束符
     * @return false为超时没收到
     */
    private boolean sendEot()throws CancelException{
        int timeCount=0;
        byte[] eot=new byte[1];
        eot[0]=EOT;
        sendComData(eot);
        while (true){
            SystemClock.sleep(100);
            byte[] ask=receiveComData();
            if(ask!=null&&ask.length>=1){
                if(ask[0]==ACK){
                    break;
                }else if(ask[0]==CAN){
                    throw new CancelException( "mcu cancel" ) ;
                }else if(ask[0] == NAK){
                    sendComData(eot);
                }
            }
            timeCount++;
            if(timeCount>10){   //超过1s收不到回复，报异常
                return false;
            }
        }
        return true;
    }

    /***
     * 获取前多少位校验和
     * @param data
     * @return
     */
    private static byte getCheckSum(byte[] data,int start,int length){
        if(data==null||data.length==0||data.length<length+start){
            return 0;
        }
        int sum=0;
        for(int i=0;i<length;i++){
            sum+=data[start+i]&0xFF;
        }
        return (byte) (0xff&sum);
    }


    private class CancelException extends Exception{

        public  CancelException(){
            super();
        }

        public  CancelException(String msg){
            super(msg);
        }
    }
}
