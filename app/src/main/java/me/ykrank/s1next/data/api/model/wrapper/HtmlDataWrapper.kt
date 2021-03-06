package me.ykrank.s1next.data.api.model.wrapper

import me.ykrank.s1next.App
import me.ykrank.s1next.data.event.NoticeRefreshEvent
import me.ykrank.s1next.util.L
import org.jsoup.nodes.Document
import java.util.regex.Pattern

/**
 * Parse html extra data
 * Created by ykrank on 2017/6/7.
 */
class HtmlDataWrapper {
    var notice: Int? = null

    companion object {
        fun fromHtml(document: Document): HtmlDataWrapper {
            val result = HtmlDataWrapper()
            try {
                val elements = document.select("#myprompt")
                if (!elements.isEmpty()) {
                    val noticeStr = elements[0].text()
                    val pattern = Pattern.compile("\\((\\d*)\\)")
                    val matcher = pattern.matcher(noticeStr)
                    if (matcher.find()) {
                        result.notice = matcher.group(1)?.toInt() ?: 0
                        notifyData(result)
                    }
                }
            } catch (e: Exception) {
                L.report(e)
            }
            return result
        }

        fun notifyData(data: HtmlDataWrapper) {
            if (data.notice != null) {
                App.getAppComponent().eventBus.post(NoticeRefreshEvent::class.java, NoticeRefreshEvent(null, data.notice!! > 0))
            }
        }
    }
}
