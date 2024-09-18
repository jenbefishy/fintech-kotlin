import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class NewsServiceTest {
    @Test
    fun `test getNews returns expected news list`() =
        runTest {
            val mockHttpClient =
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                    engine {
                        addHandler { request ->
                            val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(
                                content = """{
                            "count": 49679,
                            "next": "https://testnews.com/?page=3",
                            "previous": "https://testnews.com/?page=1",
                            "results": [
                                {
                                    "id": 1,
                                    "publication_date": 1704112111,
                                    "title": "Test News",
                                    "place": null,
                                    "description": "Test Description",
                                    "site_url": "http://testnews.com",
                                    "favorites_count": 10,
                                    "comments_count": 5
                                }
                            ]
                        }""",
                                headers = responseHeaders,
                            )
                        }
                    }
                }

            val news = getNews(count = 1, client = mockHttpClient)

            assertEquals(1, news.size)
            assertEquals("Test News", news[0].title)
            assertEquals("Test Description", news[0].description)
        }

    @Test
    fun `test getMostRatedNews returns top rated news within period`() {
        val newsList =
            listOf(
                News(
                    1,
                    "Title1",
                    null,
                    "Description1",
                    "http://testnews.com/1",
                    10,
                    5,
                    1704112111,
                ),
                News(
                    2,
                    "Title2",
                    null,
                    "Description2",
                    "http://testnews.com/2",
                    20,
                    10,
                    1704113111,
                ),
                News(
                    3,
                    "Title3",
                    null,
                    "Description3",
                    "http://testnews.com/3",
                    30,
                    15,
                    1706790511,
                ),
            )

        val period = LocalDate.of(2024, 1, 1)..LocalDate.of(2024, 1, 31)
        val topNews = newsList.getMostRatedNews(2, period)

        assertEquals(2, topNews.size)
        assertEquals("Title2", topNews[0].title)
        assertEquals("Title1", topNews[1].title)
    }
}
