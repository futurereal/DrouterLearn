package com.viomi.viot.utils

import android.content.Context
import android.os.Environment
import android.os.StatFs
import java.io.*
import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey

/**
 * 文件管理工具类
 * Created by William on 2020/5/14.
 */
object FileUtil {

    fun saveKeyFile(context: Context, fileName: String, key: Key) {
        val fileOutputStream: FileOutputStream
        var objectOutputStream: ObjectOutputStream? = null
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(key)
            objectOutputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            objectOutputStream?.close()
        }
    }

    fun getPrivateKeyFile(context: Context): PrivateKey? {
        val fileInputStream: FileInputStream
        val objectInputStream: ObjectInputStream
        val privateKey: PrivateKey
        try {
            fileInputStream = context.openFileInput("PrivateKey")
            objectInputStream = ObjectInputStream(fileInputStream)
            privateKey = objectInputStream.readObject() as PrivateKey
            return privateKey
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun getPublicKeyFile(context: Context): PublicKey? {
        val fileInputStream: FileInputStream
        val objectInputStream: ObjectInputStream
        val publicKey: PublicKey
        try {
            fileInputStream = context.openFileInput("PublicKey")
            objectInputStream = ObjectInputStream(fileInputStream)
            publicKey = objectInputStream.readObject() as PublicKey
            return publicKey
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun saveFileObject(context: Context, fileName: String?, `object`: Any?) {
        var fileOutputStream: FileOutputStream? = null
        var objectOutputStream: ObjectOutputStream? = null
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(`object`)
            // 将 fos 的数据保存到内核缓冲区, 不能确保数据保存到物理存储设备上, 如突然断电可能导致文件未保存
            fileOutputStream.flush()
            // 将数据同步到物理存储设备
            fileOutputStream.fd.sync()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                objectOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getFileObject(context: Context, fileName: String): Any? {
        var fileInputStream: FileInputStream? = null
        var objectInputStream: ObjectInputStream? = null
        if (context.getFileStreamPath(fileName).exists()) {
            try {
                fileInputStream = context.openFileInput(fileName)
                objectInputStream = ObjectInputStream(fileInputStream)
                return objectInputStream.readObject()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    fileInputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    objectInputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    fun getSDCardFreeSize(): Long {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
            val freeBlocks = statFs.freeBlocksLong
            val blockSize = statFs.blockSizeLong
            return freeBlocks * blockSize
        }
        return 0
    }
}