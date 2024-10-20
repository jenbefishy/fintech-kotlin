package org.example
import News
import getMostRatedNews
import getNews
import kotlinx.coroutines.runBlocking
import newsDsl
import org.slf4j.LoggerFactory
import saveNews
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

const val PATH = "./src/main/resources/news.csv"

fun main() =
    runBlocking {
        val logger = LoggerFactory.getLogger("MainLogger")
        try {
            val newsList = getNews(200)
            logger.info("Fetched ${newsList.size} news items")

            val dslOutput = buildDslOutput(newsList)
            println(dslOutput)

            val period = LocalDate.of(2024, 9, 1)..LocalDate.of(2024, 9, 30)
            val ratedNews = newsList.getMostRatedNews(10, period)
            saveNews(PATH, ratedNews)
        } catch (e: Exception) {
            logger.error("Error occurred in main function: ${e.message}", e)
        }
    }

private fun buildDslOutput(newsList: List<News>): String =
    newsDsl {
        header(1) { "Top Rated News" }
        newsList.forEach { news ->
            header(2) { news.title }
            text {
                "Published on: ${Instant.ofEpochSecond(news.publicationDate).atZone(
                    ZoneId.systemDefault(),
                ).toLocalDate()}"
            }
            text { "Rating: ${bold(news.rating.toString())}" }
            text { news.description }
            text { link(news.siteUrl, "Read more") }
        }
    }
