package com.jayner.githubrepos.test

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Rule created to substitute app schedulers with the Trampoline scheduler, to allow tests to execute the tasks on the
 * current thread in a FIFO manner, which allows for testing values in an expected order.
 */
class RxSchedulerRule: TestRule {

    override fun apply(base: Statement, description: Description?): Statement {
        return object: Statement() {
            override fun evaluate() {
                RxAndroidPlugins.reset()
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { SCHEDULER_INSTANCE }

                RxJavaPlugins.reset()
                RxJavaPlugins.setIoSchedulerHandler { SCHEDULER_INSTANCE }
                RxJavaPlugins.setNewThreadSchedulerHandler { SCHEDULER_INSTANCE }
                RxJavaPlugins.setComputationSchedulerHandler { SCHEDULER_INSTANCE }

                base.evaluate()
            }
        }
    }

    companion object {
        private val SCHEDULER_INSTANCE = Schedulers.trampoline()
    }
}