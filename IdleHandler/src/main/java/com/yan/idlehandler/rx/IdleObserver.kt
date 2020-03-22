package com.yan.idlehandler.rx

import android.os.Looper
import com.yan.idlehandler.IdleHandler
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableHelper
import java.util.concurrent.atomic.AtomicReference

internal class IdleObserver<T>(
    private val downstream: Observer<in T>
) : Disposable,
    Observer<T>, Runnable {
    private val upstream = AtomicReference<Disposable>()

    override fun onSubscribe(d: Disposable) {
        DisposableHelper.setOnce(upstream, d)
        downstream.onSubscribe(this)
    }

    private var handler: IdleHandler? = null
    private var data: T? = null
    override fun onNext(data: T) {
        this.data = data
        if (Thread.currentThread() !== Looper.getMainLooper().thread) {
            AndroidSchedulers.mainThread().scheduleDirect {
                if (!isDisposed) handler = IdleHandler.handle(this)
            }
        } else {
            handler = IdleHandler.handle(this)
        }
    }

    override fun onError(e: Throwable) {
        handler?.clear()
        downstream.onError(e)
    }

    override fun onComplete() {
        handler?.clear()
        downstream.onComplete()
    }

    override fun dispose() {
        handler?.clear()
        DisposableHelper.dispose(upstream)
    }

    override fun isDisposed(): Boolean {
        return upstream.get() === DisposableHelper.DISPOSED
    }

    override fun run() {
        downstream.onNext(data!!)
    }

}