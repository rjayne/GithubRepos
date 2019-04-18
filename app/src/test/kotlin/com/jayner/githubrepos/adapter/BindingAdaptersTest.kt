package com.jayner.githubrepos.adapter

import android.view.View
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class BindingAdaptersTest {

    @Mock
    lateinit var mockView: View

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testGone_true() {
        bindIsGone(mockView, true)

        Mockito.verify(mockView, Mockito.only()).setVisibility(View.GONE)
        Mockito.verify(mockView, Mockito.never()).setVisibility(View.VISIBLE)
    }

    @Test
    fun testGone_false() {
        bindIsGone(mockView, false)

        Mockito.verify(mockView, Mockito.only()).setVisibility(View.VISIBLE)
        Mockito.verify(mockView, Mockito.never()).setVisibility(View.GONE)
    }
}