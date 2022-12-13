package chen.you.test

import android.util.Log
import androidx.lifecycle.viewModelScope
import chen.you.flows.BaseViewModel
import chen.you.flows.TestApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 *  author: you : 2021/12/7
 */
class MainViewModel : BaseViewModel() {

    private var httpJob0: Job? = null

    private var httpJob1: Job? = null

    fun testHttpRequest0() {
        httpJob0?.cancel()
        httpJob0 = viewModelScope.launch {
            getService(TestApi::class).getUserBean(TestApi.URL)
                .onStart { Log.i("youxiaochen", "testHttpRequest0 loading start...") }
                .catch { Log.i("youxiaochen", "testHttpRequest0 loading error ...$it") }
                .onCompletion { Log.i("youxiaochen", "testHttpRequest0 loading complete...$it") }
                .collect { Log.i("youxiaochen", "testHttpRequest0 result = $it") }
        }
    }

    fun testHttpRequest1() {
        httpJob1?.cancel()
        httpJob1 = viewModelScope.launch {
            getService(TestApi::class).getUserBean(TestApi.URL2)
                .onStart { Log.i("youxiaochen", "testHttpRequest1 loading start...") }
                .catch { Log.i("youxiaochen", "testHttpRequest1 loading error ...$it") } //erro
                .onCompletion { Log.i("youxiaochen", "testHttpRequest1 loading complete...$it") } //cancel时 it不为空, error时it为空并触发catch...
                .collect { Log.i("youxiaochen", "testHttpRequest1 result = $it") }
        }
    }
}