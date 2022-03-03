package com.viomi.iotdevice.common.util

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/20
 *     desc   : 文件管理工具
 *     version: 1.0
 * </pre>
 */
internal object FileUtil {

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    fun readFileByBytes(filename: String): ByteArray {
        val file = File(filename)
        takeIf { !file.exists() }?.run { throw FileNotFoundException(filename) }
        val byteArrayOutputStream = ByteArrayOutputStream(file.length().toInt())
        var bufferedInputStream: BufferedInputStream? = null
        return try {
            bufferedInputStream = BufferedInputStream(FileInputStream(file))
            val bufSize = 1024
            val buffer = ByteArray(bufSize)
            var len: Int
            while (-1 != bufferedInputStream.read(buffer, 0, bufSize).also { len = it }) {
                byteArrayOutputStream.write(buffer, 0, len)
            }
            byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            try {
                bufferedInputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            byteArrayOutputStream.close()
        }
    }
}
