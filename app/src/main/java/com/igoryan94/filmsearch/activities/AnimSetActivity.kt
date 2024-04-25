package com.igoryan94.filmsearch.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityAnimSetBinding

class AnimSetActivity : AppCompatActivity() {
    lateinit var b: ActivityAnimSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityAnimSetBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sunRiseAnim = ObjectAnimator.ofFloat(b.imageSun, View.TRANSLATION_Y, -700f)
        sunRiseAnim.duration = 1500

        val sunAppearAnim = ObjectAnimator.ofFloat(b.imageSun, View.ALPHA, 1f)
        sunAppearAnim.duration = 300

        val nightToDayAnim = ObjectAnimator.ofFloat(b.imageHorizon, View.ALPHA, 1f)
        nightToDayAnim.duration = 2000

        val firstCloudAnim = ObjectAnimator.ofFloat(b.imageCloud1, View.TRANSLATION_X, 0F)
        firstCloudAnim.duration = 1000

        val firstCloudAppearAnim = ObjectAnimator.ofFloat(b.imageCloud1, View.ALPHA, 1f)
        firstCloudAppearAnim.duration = 300

        val secondCloudAnim = ObjectAnimator.ofFloat(b.imageCloud2, View.TRANSLATION_X, 0F)
        secondCloudAnim.duration = 1000

        val secondCloudAppearAnim = ObjectAnimator.ofFloat(b.imageCloud2, View.ALPHA, 1f)
        secondCloudAppearAnim.duration = 300

        val cloud1OffScreenAnim = ObjectAnimator.ofFloat(b.imageCloud1, View.TRANSLATION_X, -500f)
        cloud1OffScreenAnim.duration = 1000

        val cloud2OffScreenAnim = ObjectAnimator.ofFloat(b.imageCloud2, View.TRANSLATION_X, 500f)
        cloud2OffScreenAnim.duration = 1000

        val animatorSun = AnimatorSet()
        animatorSun.playTogether(sunAppearAnim, sunRiseAnim, nightToDayAnim)
        animatorSun.play(firstCloudAppearAnim).after(sunRiseAnim)
        animatorSun.play(firstCloudAnim).after(firstCloudAppearAnim)
        animatorSun.play(secondCloudAppearAnim).after(firstCloudAnim).after(500)
        animatorSun.play(secondCloudAnim).after(secondCloudAppearAnim)

        animatorSun.play(cloud1OffScreenAnim).before(firstCloudAnim)
        animatorSun.play(cloud2OffScreenAnim).before(secondCloudAnim)

        animatorSun.start()
    }
}