package com.yan.idlehandler.rx

import android.os.Looper
import com.yan.idlehandler.IdleHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.subscriptions.SubscriptionHelper
import io.reactivex.internal.util.EndConsumerHelper
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicReference

class IdleFlowableObserver<T> internal constructor(
    private val downstream: Subscriber<in T>
) : Subscriber<T>,
    Subscription, Disposable, Runnable {
    private val upstream = AtomicReference<Subscription>()

    override fun onSubscribe(s: Subscription) {
        EndConsumerHelper.setOnce(upstream, s, javaClass)
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

    override fun request(n: Long) {
        upstream.get().request(n)
    }

    override fun cancel() {
        handler?.clear()
        dispose()
    }

    override fun isDisposed(): Boolean {
        return upstream.get() === SubscriptionHelper.CANCELLED
    }

    override fun dispose() {
        handler?.clear()
        SubscriptionHelper.cancel(upstream)
    }

    override fun run() {
        downstream.onNext(data!!)
    }

}