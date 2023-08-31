package ke.co.xently.shopping.features.security

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object DataEncryption {
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getKeyFromPassword(password: String, salt: String, algorithm: String = "AES"): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(
            factory.generateSecret(spec)
                .encoded, algorithm
        )
    }

    fun generateIv(base64EncodedIvParameterSpec: String): IvParameterSpec {
        return IvParameterSpec(Base64.getDecoder().decode(base64EncodedIvParameterSpec))
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(
        cipherText: String,
        key: SecretKey,
        iv: IvParameterSpec,
        algorithm: String = "AES/CBC/PKCS5Padding",
    ): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(
            Base64.getDecoder()
                .decode(cipherText)
        )
        return String(plainText)
    }
}