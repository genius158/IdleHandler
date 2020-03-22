package com.yan.idlehandler

import android.os.Looper
import androidx.annotation.MainThread
import com.yan.idlehandler.rx.IdleFlowableObserver
import com.yan.idlehandler.rx.IdleMaybeObserver
import com.yan.idlehandler.rx.IdleObserver
import com.yan.idlehandler.rx.IdleSingleObserver
import io.reactivex.*
import org.reactivestreams.Subscriber

object IdleHandlerUtils {
    @MainThread
    @Throws(Exception::class)
    @JvmStatic
    fun handle(runnable: Runnable?): IdleHandler {
        if (Looper.getMainLooper().thread !== Thread.currentThread()) {
            throw Exception("must call on main thread")
        }
        return IdleHandler.handle(runnable!!)
    }

    @JvmStatic
    fun <T> wrapObserver(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            object : Observable<T>() {
                override fun subscribeActual(observer: Observer<in T>) {
                    upstream.subscribe(IdleObserver(observer))
                }
            }
        }
    }

    @JvmStatic
    fun <T> wrapSingle(): SingleTransformer<T, T> {
        return SingleTransformer { upstream: Single<T> ->
            object : Single<T>() {
                override fun subscribeActual(observer: SingleObserver<in T>) {
                    upstream.subscribe(IdleSingleObserver(observer))
                }
            }
        }
    }

    @JvmStatic
    fun <T> wrapMaybe(): MaybeTransformer<T, T> {
        return MaybeTransformer { upstream: Maybe<T> ->
            object : Maybe<T>() {
                override fun subscribeActual(observer: MaybeObserver<in T>) {
                    upstream.subscribe(IdleMaybeObserver(observer))
                }
            }
        }
    }

    @JvmStatic
    fun <T> wrapFlawable(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> ->
            object : Flowable<T>() {
                override fun subscribeActual(observer: Subscriber<in T>) {
                    upstream.subscribe(IdleFlowableObserver(observer))
                }
            }
        }
    }
}