import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

suspend fun saveNews(channel: Channel<News>) {
    val file = File("./news.txt")
    file.bufferedWriter().use { writer ->
        for (newsItem in channel) {
            writer.write("${newsItem.id}: ${newsItem.title}\n${newsItem.description}\n")
        }
    }
}
