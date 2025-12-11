package projectichif.DriveNusa

import android.animation.*
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val gearStick = findViewById<ImageView>(R.id.gearStick)
        val logoText = findViewById<ImageView>(R.id.logoText)

        gearStick.visibility = View.VISIBLE
        logoText.alpha = 0f
        logoText.scaleX = 0.9f
        logoText.scaleY = 0.9f

        gearStick.post {

            val startY = gearStick.translationY
            val jumpHeight = 160f

            val fall = ObjectAnimator.ofFloat(gearStick, "translationY", startY - 400f, startY - 80f).apply {
                duration = 550
                interpolator = AccelerateDecelerateInterpolator()
            }

            val tiltRight = ObjectAnimator.ofFloat(gearStick, "rotation", 0f, 18f).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }

            val jumpUp = ObjectAnimator.ofFloat(gearStick, "translationY", startY - 80f, startY - jumpHeight).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }

            val settle = ObjectAnimator.ofFloat(gearStick, "translationY", startY - jumpHeight, startY).apply {
                duration = 1000
                interpolator = BounceInterpolator()
            }

            val tiltReset = ObjectAnimator.ofFloat(gearStick, "rotation", 18f, 0f).apply {
                duration = 400
                interpolator = AccelerateDecelerateInterpolator()
            }

            val tuasAnim = AnimatorSet()
            tuasAnim.playSequentially(fall, tiltRight, jumpUp, settle, tiltReset)
            tuasAnim.start()

            val fadeIn = ObjectAnimator.ofFloat(logoText, "alpha", 0f, 1f).apply {
                duration = 900
                startDelay = 200
            }
            val scaleUpX = ObjectAnimator.ofFloat(logoText, "scaleX", 0.9f, 1f).apply {
                duration = 900
                startDelay = 200
            }
            val scaleUpY = ObjectAnimator.ofFloat(logoText, "scaleY", 0.9f, 1f).apply {
                duration = 900
                startDelay = 200
            }

            tuasAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val logoAnim = AnimatorSet()
                    logoAnim.playTogether(fadeIn, scaleUpX, scaleUpY)
                    logoAnim.start()
                }
            })

            // 🚀 Setelah animasi selesai = CEK TOKEN
            Handler(Looper.getMainLooper()).postDelayed({

                lifecycleScope.launch {
                    val isValid = AuthRepository.checkToken(this@SplashActivity)
                    Log.d("SPLASH_CHECK", "isValid = $isValid")

                    if (isValid) {
                        goToHome()
                    } else {
                        UserLocal.clearToken(this@SplashActivity)
                        goToLogin()
                    }
                }

            }, 4000) // durasi splash
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
