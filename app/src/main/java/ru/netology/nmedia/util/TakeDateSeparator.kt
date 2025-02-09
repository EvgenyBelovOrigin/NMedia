package ru.netology.nmedia.util

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.TimeSeparator
import ru.netology.nmedia.dto.TimeSeparatorValues
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class TakeDateSeparator @Inject constructor() {
    private val today = LocalDate.now()
    private val yesterday = today.minusDays(1)
    private val aWeekAgo = today.minusDays(2)


    fun createSeparator(previous: Post?, next: Post?): TimeSeparator? =
        when {
            !previous.isToday() && next.isToday() -> {
                TimeSeparatorValues.TODAY
            }

            !previous.isYesterday() && next.isYesterday() -> {
                TimeSeparatorValues.YESTERDAY
            }

            !previous.isWeekAgo() && next.isWeekAgo() -> {
                TimeSeparatorValues.A_WEEK_AGO
            }

            else -> {
                null
            }
        }?.let(::TimeSeparator)

    private val Post.publishedDate: LocalDate
        get() = Instant.ofEpochSecond(published)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    private fun Post?.isToday(): Boolean = this?.publishedDate?.let { today == it } ?: false

    private fun Post?.isYesterday(): Boolean =
        this?.publishedDate?.let { today > it && today >= yesterday } ?: false


    private fun Post?.isWeekAgo(): Boolean =
        this?.publishedDate?.let { it < aWeekAgo } ?: false
}