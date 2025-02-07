package ru.netology.nmedia.util

import android.os.Build
import androidx.annotation.RequiresApi
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.TimeSeparator
import ru.netology.nmedia.dto.TimeSeparatorValues
import java.time.Instant
import javax.inject.Inject

class TakePostDate @Inject constructor() {
    private val oneDayInSeconds = 10L

    @RequiresApi(Build.VERSION_CODES.O)
    val today = Instant.now().epochSecond


    @RequiresApi(Build.VERSION_CODES.O)
    fun takePostDate(previous: Post?, next: Post?): TimeSeparator? {
        return when {
            !postIsToday(previous) && postIsToday(next) -> {
                TimeSeparator(TimeSeparatorValues.TODAY)
            }

            !postIsYesterday(previous) && postIsYesterday(next) -> {
                TimeSeparator(TimeSeparatorValues.YESTERDAY)
            }

            !postIsAWeekAgo(previous) && postIsAWeekAgo(next) -> {
                TimeSeparator(TimeSeparatorValues.A_WEEK_AGO)
            }

            else -> {
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postIsToday(post: Post?): Boolean {
        return (post?.published ?: 0L) > today - oneDayInSeconds
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postIsYesterday(post: Post?): Boolean {
        return (post?.published ?: 0L) > today - oneDayInSeconds * 2 && (post?.published
            ?: 0L) < today - oneDayInSeconds
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postIsAWeekAgo(post: Post?): Boolean {
        return (post?.published ?: 0L) <= today - oneDayInSeconds * 2
    }
}