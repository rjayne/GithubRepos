package com.jayner.githubrepos.idlingresource

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

class EspressoTestingIdlingResource private constructor() {
    private val RESOURCE = "GLOBAL"

    var countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource?.increment()
    }

    fun decrement() {
        countingIdlingResource?.decrement()
    }

    fun getIdlingResource(): IdlingResource? {
        return countingIdlingResource
    }

    companion object {
        private var espressoTestingIdlingResource: EspressoTestingIdlingResource? = null
        fun getInstance(): EspressoTestingIdlingResource {
            if(espressoTestingIdlingResource == null) {
                espressoTestingIdlingResource = EspressoTestingIdlingResource()
            }

            return espressoTestingIdlingResource!!
        }
    }
}