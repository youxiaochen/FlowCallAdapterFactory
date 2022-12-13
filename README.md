# FlowCallAdapterFactory
### 协程Flow之Retrofit2 CallAdapterFactory

##使用
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }  
}

dependencies {
	implementation 'com.github.youxiaochen:FlowCallAdapterFactory:1.0.0'
}
```

#####使用

```
Retrofit.Builder()
    .client(builder.build())
    ...
    .addCallAdapterFactory(FlowCallAdapterFactory.createSynchronous(Dispatchers.IO))//一般使用同步在IO中执行
    .baseUrl("https://www.jianshu.com/u/b1cff340957c/")
    .build()

httpJob?.cancel()
httpJob = viewModelScope.launch {
    testApi.getUserBean(TestApi.URL2)
    .onStart { Log.i("youxiaochen", "testHttpRequest1 loading start...") }
    .catch { Log.i("youxiaochen", "testHttpRequest1 loading error ...$it") } //erro
    .onCompletion { Log.i("youxiaochen", "testHttpRequest1 loading complete...$it") } //cancel时 it不为空, error时it为空并触发catch...
    .collect { Log.i("youxiaochen", "testHttpRequest1 result = $it") }
}

```

