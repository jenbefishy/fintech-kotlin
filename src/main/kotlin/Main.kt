package org.example
import News
import NewsResponse
import getNews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import saveNews

const val PATH = "./src/main/resources/news.csv"

fun main() = runBlocking {
    val logger = LoggerFactory.getLogger("MainLogger")
    val channel = Channel<News>();
    val workerContext = newFixedThreadPoolContext(4, "workerPool")

    try {
        val workers = List(8) { // Кол-во Worker'ов
            launch(workerContext) {
                val newsList = getNews(100)
                newsList.forEach { channel.send(it) }
            }
        }

        val processor = launch(Dispatchers.IO) {
            saveNews(channel)
        }

        workers.forEach { it.join() }
        channel.close()
        processor.join()
    } catch (e: Exception) {
        logger.error("Error occurred in main function: ${e.message}", e)
    }
}

