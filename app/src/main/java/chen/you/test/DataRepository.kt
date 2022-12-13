package chen.you.flows

import chen.you.calladapter.FlowCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 *  author: you : 2021/12/7
 *
 */
object DataRepository : Repository {

    /**
     * 默认超时时间
     */
    private const val DEF_MILL_TIMEOUT = 10L

    private val retrofit: Retrofit

    init {
        val builder = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .connectTimeout(DEF_MILL_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEF_MILL_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEF_MILL_TIMEOUT, TimeUnit.SECONDS)

        retrofit = Retrofit.Builder()
            .client(builder.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.createSynchronous(Dispatchers.IO))//一般使用同步在IO中执行
            .baseUrl("https://www.jianshu.com/u/b1cff340957c/")
            .build()
    }

    override fun <T> getService(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}