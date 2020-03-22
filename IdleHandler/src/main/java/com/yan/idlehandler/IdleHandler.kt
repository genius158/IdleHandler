package com.yan.idlehandler

import android.os.Looper
import android.os.MessageQueue

class IdleHandler : MessageQueue.IdleHandler {
    private var task: Runnable? = null
    private var messageQueue: MessageQueue? = null
    private fun addTask(runnable: Runnable) {
        task = runnable
        messageQueue = Looper.myQueue()
        messageQueue?.addIdleHandler(this)
    }

    fun clear() {
        task = null
        messageQueue?.removeIdleHandler(this)
    }

    override fun queueIdle(): Boolean {
        task?.run()
        clear()
        return false
    }

    companion object {
        fun handle(runnable: Runnable): IdleHandler {
            val handlerHelper = IdleHandler()
            handlerHelper.addTask(runnable)
            return handlerHelper
        }
    }
}