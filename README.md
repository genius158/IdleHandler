# IdleHandler
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