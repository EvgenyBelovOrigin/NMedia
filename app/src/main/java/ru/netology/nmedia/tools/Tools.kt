package ru.netology.nmedia.tools

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class Tools {
    fun getShortCountTypeValue(count: Long): String {
        when {
            count >= 1_100_000 -> return "${(count / 100_000).toDouble() / 10}M"
            count >= 10_000 -> return "${count / 1_000}K"
            count >= 1_100 -> return "${(count / 100).toDouble() / 10}K"
            count >= 1000 -> return "1K"
        }
        return "$count"
    }
}

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}