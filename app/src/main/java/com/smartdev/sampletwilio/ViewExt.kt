package com.smartdev.sampletwilio

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showOrHide(show: Boolean) = if (show) show() else hide()

fun View.toggleView() {
    if (this.visibility == View.VISIBLE) this.visibility = View.GONE else this.visibility =
        View.VISIBLE

}
fun Activity.hideKeyboard(editText: EditText){
    val `in`: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    `in`.hideSoftInputFromWindow(
        editText.getApplicationWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}
fun Context?.toast(msg: String): Toast {
    return Toast.makeText(this, msg, Toast.LENGTH_SHORT).apply { show() }
}
