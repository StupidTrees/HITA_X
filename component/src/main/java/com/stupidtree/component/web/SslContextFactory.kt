package com.stupidtree.component.web

import android.content.Context
import com.stupidtree.component.R
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class SslContextFactory {
    var sslContext: SSLContext? = null
    val trustManager: TrustManagerFactory = TrustManagerFactory.getInstance(
        CLIENT_TRUST_MANAGER
    )

    fun getSslSocket(context: Context): SSLContext? {
        try {
//取得SSL的SSLContext实例
            sslContext = SSLContext.getInstance(CLIENT_AGREEMENT)
            //取得TrustManagerFactory的X509密钥管理器实例

            //取得BKS密库实例
            val tks: KeyStore = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE)
            val iS: InputStream = context.resources.openRawResource(R.raw.hita)
            iS.use {
                tks.load(it, CLIENT_TRUST_PASSWORD.toCharArray())
            }
            //初始化密钥管理器
            trustManager.init(tks)
            //初始化SSLContext
            sslContext?.init(null, trustManager.trustManagers, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sslContext
    }

    companion object {
        private const val CLIENT_TRUST_PASSWORD = "www,2012.com" //信任证书密码
        private const val CLIENT_AGREEMENT = "TLS" //使用协议
        private const val CLIENT_TRUST_MANAGER = "X509"
        private const val CLIENT_TRUST_KEYSTORE = "BKS"
    }
}
