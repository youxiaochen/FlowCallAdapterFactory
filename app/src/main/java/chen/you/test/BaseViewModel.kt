package chen.you.flows

import androidx.collection.ArrayMap
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

/**
 *  author: you : 2021/12/7
 *  可从MVVM封装此类Repository, BaseModel, BaseModel
 */
open class BaseViewModel(private val repository: DataRepository = DataRepository) : ViewModel(), DefaultLifecycleObserver {

    private val services = ArrayMap<Class<*>, Any>()

    protected fun <T> getService(serviceClass: Class<T>): T {
        val service = services[serviceClass]
        if (service != null) return service as T
        return repository.getService(serviceClass).also { services[serviceClass] = it }
    }

    protected fun <T : Any> getService(serviceClass: KClass<T>): T = getService(serviceClass.java)

    override fun onCleared() {
        services.clear()
    }
}