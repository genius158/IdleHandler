# IdleHandler(有些方法必须放在主线程，但又比较耗时，这个时候IdleHandler就派得上用场了)
implementation 'com.yan.idlehandler:idlehandler:1.0.1'
### rxjava
```
.compose(IdleHandlerUtils.wrapObserver())
.compose(IdleHandlerUtils.wrapSingle())
.compose(IdleHandlerUtils.wrapFlawable())
.compose(IdleHandlerUtils.wrapMaybe())
```
### kt continuation
```
GlobalScope.launch {
    ...
    resumeIdle()
    ...
}
```