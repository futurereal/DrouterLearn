package com.viomi.viot.utils.encrypt

import android.content.Context
import android.util.Base64
import com.viomi.viot.utils.FileUtil.getPrivateKeyFile
import com.viomi.viot.utils.FileUtil.getPublicKeyFile
import com.viomi.viot.utils.FileUtil.saveKeyFile
import com.viomi.viot.utils.LogUtil.e
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * RSA 加解密工具
 * Created by William on 2020/5/14.
 */
object RSAUtil {
    private val TAG = RSAUtil::class.java.simpleName
    private const val MAX_ENCRYPT_BLOCK = 117 // RSA 最大加密明文大小
    private const val MAX_DECRYPT_BLOCK = 128 // RSA 最大解密密文大小

    /**
     * RSA 公钥加密
     *
     * @param content 加密字符串
     * @param key     密钥
     * @return 密文
     */
    fun rsaEncode(content: String, key: String?): String {
        // Base64 编码的公钥
        val decoded = Base64.decode(key, Base64.NO_WRAP)
        val pubKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(decoded)) as? RSAPublicKey
        // RSA 加密
        val cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)

        val contents = content.toByteArray(StandardCharsets.UTF_8)

        val inputLen = contents.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var cache: ByteArray
        var i = 0
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            cache = if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cipher.doFinal(contents, offSet, MAX_ENCRYPT_BLOCK)
            } else {
                cipher.doFinal(contents, offSet, inputLen - offSet)
            }
            out.write(cache, 0, cache.size)
            i++
            offSet = i * MAX_ENCRYPT_BLOCK
        }
        val encryptedData = out.toByteArray()
        out.close()

        return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
    }

    fun rsaDecode(content: String?, key: String?): String {
        val decoded = Base64.decode(key, Base64.NO_WRAP)
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(decoded)) as? RSAPrivateKey
        val cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val contents = Base64.decode(content, Base64.NO_WRAP)
        val inputLen = contents.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var cache: ByteArray
        var i = 0
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            cache = if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cipher.doFinal(contents, offSet, MAX_DECRYPT_BLOCK)
            } else {
                cipher.doFinal(contents, offSet, inputLen - offSet)
            }
            out.write(cache, 0, cache.size)
            i++
            offSet = i * MAX_DECRYPT_BLOCK
        }
        val decryptedData = out.toByteArray()
        out.close()

        return String(decryptedData, StandardCharsets.UTF_8)
    }

    /**
     * 生成设备私钥和公钥
     */
    fun createRSAKeyPair(context: Context) {
        val keyPair = generateRSAKeyPair()
        keyPair?.let {
            val privateKey = keyPair.private
            val publicKey = keyPair.public
            try {
                saveKeyFile(context, "PrivateKey", privateKey)
                saveKeyFile(context, "PublicKey", publicKey)
            } catch (e: IOException) {
                e(TAG, e.message ?: "")
                e.printStackTrace()
            }
        }
    }

    fun getRSAPublicKey(context: Context): String {
        val publicKey = getPublicKeyFile(context)
        publicKey?.let {
            val publicKeyByte = publicKey.encoded
            return Base64.encodeToString(publicKeyByte, Base64.NO_WRAP)
        }
        return ""
    }

    fun getRSAPrivateKey(context: Context): String? {
        val privateKey = getPrivateKeyFile(context)
        privateKey?.let {
            val privateKeyByte = privateKey.encoded
            return Base64.encodeToString(privateKeyByte, Base64.NO_WRAP)
        }
        return null
    }

    private fun generateRSAKeyPair(): KeyPair? {
        return try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(1024)
            keyPairGenerator.genKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
}