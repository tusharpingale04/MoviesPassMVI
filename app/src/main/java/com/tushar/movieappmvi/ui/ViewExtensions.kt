package com.tushar.movieappmvi.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.tushar.movieappmvi.R

fun Context.displayToast(@StringRes message:Int){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun Context.displayToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun Context.displaySuccessDialog(message: String?){
    MaterialDialog(this)
        .show{
            title(R.string.text_success)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
}

fun Context.displayErrorDialog(errorMessage: String?){
    MaterialDialog(this)
        .show{
            title(R.string.text_error)
            message(text = errorMessage)
            positiveButton(R.string.text_ok)
        }
}

fun View.slideDown() {
    this.visibility = View.VISIBLE
    val layoutParams = this.layoutParams
    layoutParams.height = 1
    this.layoutParams = layoutParams
    this.measure(
        View.MeasureSpec.makeMeasureSpec(
            Resources.getSystem().displayMetrics.widthPixels,
            View.MeasureSpec.EXACTLY
        ),
        View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
    )
    val height = this.measuredHeight
    val valueAnimator = ObjectAnimator.ofInt(1, height)
    valueAnimator.addUpdateListener { animation ->
        val value = animation.animatedValue as Int
        if (height > value) {
            val lp = this.layoutParams
            lp.height = value
            this.layoutParams = lp
        } else {
            val lp = this.layoutParams
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            this.layoutParams = lp
        }
    }
    valueAnimator.start()
}


fun View.slideUp() {
    this.post {
        val height = this.height
        val valueAnimator = ObjectAnimator.ofInt(height, 0)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            if (value > 0) {
                val layoutParams = this.layoutParams
                layoutParams.height = value
                this.layoutParams = layoutParams
            } else {
                this.visibility = View.GONE
            }
        }
        valueAnimator.start()
    }
}
















