import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

fun saveNews(
    path: String,
    news: Collection<News>,
) {
    val logger = LoggerFactory.getLogger("NewsLogger")
    val file = File(path)

    try {
        if (file.exists()) {
            if (file.isDirectory) {
                logger.error("The path points to a directory, not a file: $path")
                throw IllegalArgumentException("Path points to a directory, not a file")
            } else {
                logger.warn("File already exists and will be overwritten: $path")
            }
        }

        csvWriter().open(file) {
            writeRow("id", "title", "place", "description", "site_url", "favorites_count", "comments_count", "publication_date", "rating")
            news.forEach { item ->
                writeRow(
                    item.id,
                    item.title,
                    item.place,
                    item.description,
                    item.siteUrl,
                    item.favoritesCount,
                    item.commentsCount,
                    item.publicationDate,
                    item.rating,
                )
            }
        }

        logger.info("News successfully saved into: $path")
    } catch (e: IOException) {
        logger.error("IO error occurred while saving news to file: $path", e)
    } catch (e: IllegalArgumentException) {
        logger.error("Invalid argument error: ${e.message}", e)
    } catch (e: Exception) {
        logger.error("Unexpected error occurred while saving news: $path", e)
    }
}
