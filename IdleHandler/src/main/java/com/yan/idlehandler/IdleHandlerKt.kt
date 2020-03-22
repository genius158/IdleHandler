package com.yan.idlehandler

import android.os.Looper
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun resumeIdle(): Boolean {
    return suspendCancellableCoroutine { continuation ->
        var handler: IdleHandler? = null
        if (Thread.currentThread() != Looper.getMainLooper().thread)
            Dispatchers.Main.dispatch(continuation.context,
                Runnable { handler = getHandle(continuation) })
        else
            handler = getHandle(continuation)

        continuation.invokeOnCancellation { handler?.clear() }
    }
}

private fun getHandle(continuation: CancellableContinuation<Boolean>): IdleHandler {
    return IdleHandlerUtils.handle(Runnable {
        if (!continuation.isCancelled) continuation.resume(true)
    })
}

