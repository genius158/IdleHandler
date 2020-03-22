package com.yan.idlehandlertest

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.TextView
import com.yan.idlehandler.IdleHandlerUtils.wrapFlawable
import com.yan.idlehandler.IdleHandlerUtils.wrapObserver
import com.yan.idlehandler.IdleHandlerUtils.wrapSingle
import com.yan.idlehandler.resumeIdle
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

class TestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this))
        Single.just(true).observeOn(Schedulers.io())
            .compose(wrapSingle<Any>())
            .subscribe(object : SingleObserver<Any> {
                override fun onSubscribe(d: Disposable) {}
                override fun onSuccess(o: Any) {
                    Log.e("Single", "Single " + Thread.currentThread())
                }

                override fun onError(e: Throwable) {}
            })
        val handler = Handler()
        for (i in 0..999) {
            handler.sendMessage(Message())
        }
        Observable.interval(2000, TimeUnit.MILLISECONDS)
            .compose(wrapObserver())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(aLong: Long) {
                    Log.e(
                        "Observable",
                        "Observable " + Thread.currentThread()
                    )
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
        Flowable.interval(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .compose(wrapFlawable())
            .subscribe(object : FlowableSubscriber<Long> {
                override fun onSubscribe(s: Subscription) {
                    s.request(Int.MAX_VALUE.toLong())
                }

                override fun onNext(aLong: Long) {
                    Log.e("Flowable", "Flowable  " + Thread.currentThread())
                }

                override fun onError(t: Throwable) {}
                override fun onComplete() {}
            })

        GlobalScope.launch {
            Log.e("resumeIdle","111111111")
            resumeIdle()
            Log.e("resumeIdle","222222222")
        }

    }
}