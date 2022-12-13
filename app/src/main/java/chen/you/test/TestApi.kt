package chen.you.flows

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Url

/**
 *  author: you : 2022/12/7
 */
interface TestApi {

    companion object {

        //可以正常获取的
        const val URL = "http://jsonplaceholder.typicode.com/todos/1"

        //被限制的URL可以模拟请求超时
        const val URL2 = "https://www.google.com/"
    }

    @GET
    fun getUserBean(@Url url: String): Flow<UserBean>
}