package com.jayner.githubrepos.data

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.jayner.githubrepos.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.threeten.bp.ZonedDateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import android.os.Build
import javax.net.ssl.SSLContext


/**
 * This Singleton class holds reference to any RestClients required by the application.
 *
 * OkHttp performs best when you create a single {@code OkHttpClient} instance and reuse it for
 * all of your HTTP calls. This is because each client holds its own connection pool and thread
 * pools. Reusing connections and threads reduces latency and saves memory. Conversely, creating a
 * client for each request wastes resources on idle pools.
 *
 * The single httpClient object is available for reuse by additional rest clients.
 */
@EBean(scope = EBean.Scope.Singleton)
class RestServiceAccessor() {

    @RootContext
    lateinit var context: Context

    lateinit var httpClient: OkHttpClient
    lateinit var gitHubRestClient: GitHubRestClient

    @AfterInject
    fun init() {
        httpClient = provideOkHttpClient(context)
        gitHubRestClient = provideGithubRestService()
    }

    /**
     * Added caching to the HttpClient and adjusted connect and read timeouts. Extra time for connection time allowed
     * for users with slow internet connections. The read timeout will depend on the server's response ability.
     */
    private fun provideOkHttpClient(context: Context) : OkHttpClient {
        val httpCacheDir = File(context.getCacheDir(), "HttpResponseCache")
        Log.d(TAG, "initOkHttpClient - cache dir: " + context.getCacheDir())

        var clientBuilder = OkHttpClient.Builder()
            .cache(Cache(httpCacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

        return enableTls12OnPreLollipop(clientBuilder).build()
    }

    private fun provideGithubRestService(): GitHubRestClient {
        val retrofit = provideRetrofit(BuildConfig.API_URL)
        return retrofit.create(GitHubRestClient::class.java)
    }

    private fun provideRetrofit(baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(ZonedDateTime::class.java, DateTimeTypeConverter())
            .create()

        return Retrofit.Builder()
            .callFactory(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // Retrofit interfaces are able to return RxJava 2.x types, e.g., Observable, Flowable or Single and so on
            .baseUrl(baseUrl)
            .build()
    }

    // To enable client to work on older Android OS, otherwise get SSL Handshake aborted
    fun enableTls12OnPreLollipop(client: OkHttpClient.Builder): OkHttpClient.Builder {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                val sc = SSLContext.getInstance("TLSv1.2")
                sc.init(null, null, null)
                client.sslSocketFactory(Tls12SocketFactory(sc.getSocketFactory()))

                val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2).build()

                val specs = ArrayList<ConnectionSpec>()
                specs.add(cs)
                specs.add(ConnectionSpec.COMPATIBLE_TLS)
                specs.add(ConnectionSpec.CLEARTEXT)

                client.connectionSpecs(specs)
            } catch (exc: Exception) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
            }
        }

        return client
    }

    companion object {
        private val TAG = "RestServiceAccessor"
        private val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = (10 * 1024 * 1024).toLong() // 10 MiB
        private val READ_TIMEOUT = 30L // 30 secs
        private val CONNECT_TIMEOUT = 60L // 1 min
    }
}