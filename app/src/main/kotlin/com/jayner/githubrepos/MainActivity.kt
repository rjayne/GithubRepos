package com.jayner.githubrepos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jayner.githubrepos.trending.TrendingReposFragment_
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_main)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content_frame, TrendingReposFragment_())
                .commit()
        }
    }

}
