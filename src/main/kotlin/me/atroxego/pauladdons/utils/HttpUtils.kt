package me.atroxego.pauladdons.utils

import io.netty.handler.codec.http.HttpMethod
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.*
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.StandardHttpRequestRetryHandler
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * @author chenerzhu
 * @create 2018-08-11 11:25
 */
object HttpUtils {
    private const val DEFAULT_CHARSET = "UTF-8"
    private var reqConf: RequestConfig? = null
    private var standardHandler: StandardHttpRequestRetryHandler? = null

    init {
        reqConf = RequestConfig.custom()
            .setSocketTimeout(10000)
            .setConnectTimeout(10000)
            .setConnectionRequestTimeout(5000)
            .setRedirectsEnabled(false)
            .setMaxRedirects(0)
            .build()
        standardHandler = StandardHttpRequestRetryHandler(3, true)
    }

    private fun send(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        formParamMap: Map<String?, String?>?,
        contentCharset: String = DEFAULT_CHARSET,
        method: HttpMethod?,
    ): String? {
        var httpClient: CloseableHttpClient? = null
        try {
            val builder = HttpClientBuilder.create().setRetryHandler(standardHandler)
            if (url.lowercase().startsWith("https"))
                initSSL(builder)
            httpClient = builder.build()

            var httpResponse: HttpResponse? = null
            when (method) {
                HttpMethod.GET -> {
                    val httpGet = HttpGet(url)
                    httpGet.config = reqConf
                    addHeader(httpGet, headerMap)
                    httpResponse = httpClient.execute(httpGet)
                }

                HttpMethod.POST -> {
                    val httpPost = HttpPost(url)
                    httpPost.config = reqConf
                    addHeader(httpPost, headerMap)
                    if (formParamMap.isNullOrEmpty()) {
                        httpPost.entity = StringEntity(content, contentCharset)
                    } else {
                        val ls: MutableList<NameValuePair> = ArrayList()
                        for ((key, value) in formParamMap) run {
                            ls.add(BasicNameValuePair(key, value))
                        }
                        httpPost.entity = UrlEncodedFormEntity(ls, "UTF-8")
                    }
                    httpResponse = httpClient.execute(httpPost)
                }

                HttpMethod.DELETE -> {
                    val httpDelete = HttpDelete(url)
                    httpDelete.config = reqConf
                    addHeader(httpDelete, headerMap)
                    httpResponse = httpClient.execute(httpDelete)
                }

                HttpMethod.PUT -> {
                    val httpPut = HttpPut(url)
                    httpPut.config = reqConf
                    addHeader(httpPut, headerMap)
                    httpPut.entity = StringEntity(content, contentCharset)
                    httpResponse = httpClient.execute(httpPut)
                }

                HttpMethod.PATCH -> {
                    val httpPatch = HttpPatch(url)
                    httpPatch.config = reqConf
                    addHeader(httpPatch, headerMap)
                    httpPatch.entity = StringEntity(content, contentCharset)
                    httpResponse = httpClient.execute(httpPatch)
                }
            }
            if (httpResponse != null) {
                if (httpResponse.statusLine.statusCode == 200) {
                    return EntityUtils.toString(httpResponse.entity)
                } else logger.error("Failed request with status code ${httpResponse.statusLine?.statusCode}")
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                httpClient?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun addHeader(httpRequest: HttpRequestBase, headerMap: Map<String, String>?): HttpRequestBase {
        if (!headerMap.isNullOrEmpty()) {
            val keys = headerMap.keys
            val iterator = keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                httpRequest.addHeader(key, headerMap[key])
            }
        }
        return httpRequest
    }

    private fun initSSL(builder: HttpClientBuilder) {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }

                override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                }

                override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                }
            }
        )
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, SecureRandom())
        val scsf = SSLConnectionSocketFactory(sc)
        val r = RegistryBuilder.create<ConnectionSocketFactory>().register("https", scsf).build()
        val ccm = BasicHttpClientConnectionManager(r)

        builder.setSslcontext(sc).setSSLSocketFactory(scsf).setConnectionManager(ccm)
    }

    @JvmOverloads
    fun sendGet(
        url: String,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
    ): String? = send(url, "", headerMap, null, contentCharset, HttpMethod.GET)

    fun sendPostForm(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        formParamMap: Map<String?, String?>?,
    ): String? = send(url, content, headerMap, formParamMap, DEFAULT_CHARSET, HttpMethod.POST)

    @JvmOverloads
    fun sendPost(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
    ): String? = send(url, content, headerMap, null, contentCharset, HttpMethod.POST)

    fun sendPostForm(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        formParamMap: Map<String?, String?>?,
        contentCharset: String = DEFAULT_CHARSET,
    ): String? = send(url, content, headerMap, formParamMap, contentCharset, HttpMethod.POST)

    @JvmOverloads
    fun sendDelete(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
    ): String? = send(url, content, headerMap, null, contentCharset, HttpMethod.DELETE)

    @JvmOverloads
    fun sendPut(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
    ): String? = send(url, content, headerMap, null, contentCharset, HttpMethod.PUT)

    @JvmOverloads
    fun sendPatch(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
    ): String? = send(url, content, headerMap, null, contentCharset, HttpMethod.PATCH)
}