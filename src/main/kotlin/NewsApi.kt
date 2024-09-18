import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

suspend fun getNews(
    count: Int = 100,
    orderBy: String = "-publication_date",
    location: String = "spb",
    client: HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        },
): List<News> {
    val logger = LoggerFactory.getLogger("NewsLogger")
    val pageSize = 50
    val totalPages = (count + pageSize - 1) / pageSize
    val allNews = mutableListOf<News>()

    try {
        for (page in 1..totalPages) {
            try {
                val response: HttpResponse =
                    client.get("https://kudago.com/public-api/v1.4/news/") {
                        parameter(
                            "fields",
                            "id,title,place,description,site_url,favorites_count,comments_count,publication_date",
                        )
                        parameter("expand", "place")
                        parameter("text_format", "text")
                        parameter("page_size", pageSize)
                        parameter("order_by", orderBy)
                        parameter("location", location)
                        parameter("page", page)
                    }

                val responseBody: String = response.bodyAsText()
                logger.info("Received response page: $page")
                val newsResponse: NewsResponse = Json.decodeFromString(responseBody)
                allNews.addAll(newsResponse.results)
            } catch (e: Exception) {
                logger.error("Failed to fetch news for page $page", e)
            }
        }
    } catch (e: Exception) {
        logger.error("Error occurred while fetching news", e)
    } finally {
        client.close()
    }
    return allNews.take(count)
}

fun List<News>.getMostRatedNews(
    count: Int,
    period: ClosedRange<LocalDate>,
): List<News> =
    this
        .filter { news ->
            val publicationDate =
                Instant
                    .ofEpochSecond(news.publicationDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            publicationDate in period
        }.sortedByDescending { it.rating }
        .take(count)
