package chen.you.calladapter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *  author: you : 2021/10/17
 */
class FlowCallAdapterFactory private constructor(
    private val dispatcher: CoroutineDispatcher?,
    private val isAsync: Boolean
) : CallAdapter.Factory() {

    companion object {

        @JvmStatic
        fun createAsync() = FlowCallAdapterFactory(null, true)

        @JvmStatic
        fun createSynchronous(dispatcher: CoroutineDispatcher? = null) = FlowCallAdapterFactory(dispatcher, false)
    }

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        if (rawType != Flow::class.java) return null
        if (returnType !is ParameterizedType) {
            throw IllegalStateException("Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>")
        }
        val observableType = getParameterUpperBound(0, returnType)
        //Log.d("FlowCallAdapterFactory", "rawType = $rawType, returnType = $returnType, observableType = $observableType")
        return if (isAsync) AsyncFlowCallAdapter<Any>(observableType) else FlowCallAdapter<Any>(observableType, dispatcher)
    }

    class FlowCallAdapter<T>(
        private val responseType: Type,
        private val dispatcher: CoroutineDispatcher?
    ) : CallAdapter<T, Flow<T>> {

        override fun responseType(): Type = responseType

        override fun adapt(call: Call<T>): Flow<T> {
            val adaptFlow = flow {
                suspendCancellableCoroutine<T> { continuation ->
                    continuation.invokeOnCancellation {
                        call.cancel()
                    }
                    try {
                        val response = call.execute()
                        var body: T? = null
                        if (response.isSuccessful && response.body().let { body = it; it != null }) {
                            continuation.resume(body!!)
                        } else {
                            continuation.resumeWithException(HttpException(response))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }.also { emit(it) }
            }
            return dispatcher?.let { adaptFlow.flowOn(it) } ?: adaptFlow
        }
    }

    class AsyncFlowCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<T>> {

        override fun responseType(): Type = responseType

        override fun adapt(call: Call<T>): Flow<T> = flow {
            suspendCancellableCoroutine<T> { continuation ->
                continuation.invokeOnCancellation {
                    call.cancel()
                }
                call.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        var body: T? = null
                        if (response.isSuccessful && response.body().let { body = it; it != null }) {
                            continuation.resume(body!!)
                        } else {
                            continuation.resumeWithException(HttpException(response))
                        }
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
            }.also { emit(it) }
        }
    }

}