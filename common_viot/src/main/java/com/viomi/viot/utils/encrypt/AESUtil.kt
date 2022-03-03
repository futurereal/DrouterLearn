package com.viomi.viot.utils.encrypt

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES 加解密工具
 * Created by William on 2020/5/14.
 */
object AESUtil {

    fun aesEncode(content: String, key: String?): String {
        try {
            return replaceBlank(encrypt(content, key))
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun encrypt(content: String, key: String?): String {
        val contentBytes = content.toByteArray(StandardCharsets.UTF_8)
        val keyBytes = key?.toByteArray(StandardCharsets.UTF_8)
        val encryptedBytes = aesEncryptBytes(contentBytes, keyBytes)
        return String(Base64.encode(encryptedBytes, Base64.NO_WRAP), StandardCharsets.UTF_8)
    }

    fun decrypt(content: String?, key: String?): String {
        val encryptedBytes = Base64.decode(content, Base64.NO_WRAP)
        val keyBytes = key?.toByteArray(StandardCharsets.UTF_8)
        val decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    private fun aesEncryptBytes(contentBytes: ByteArray, keyBytes: ByteArray?): ByteArray {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE)
    }

    private fun aesDecryptBytes(contentBytes: ByteArray, keyBytes: ByteArray?): ByteArray {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE)
    }

    private fun cipherOperation(
        contentBytes: ByteArray,
        keyBytes: ByteArray?,
        mode: Int
    ): ByteArray {
        val secretKey = SecretKeySpec(keyBytes, "AES")

        val initParam = "AES-VIOMI-IOT-V1".toByteArray(StandardCharsets.UTF_8)
        val ivParameterSpec = IvParameterSpec(initParam)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(mode, secretKey, ivParameterSpec)

        return cipher.doFinal(contentBytes)
    }

    private fun replaceBlank(str: String?): String {
        var dest = ""
        str?.let {
            val p = Pattern.compile("\\s*|\t|\r|\n")
            val m = p.matcher(str)
            dest = m.replaceAll("")
        }
        return dest
    }
}