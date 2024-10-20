import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import kotlin.test.assertEquals

class SaveNewsTests {
    private val logger: Logger = LoggerFactory.getLogger("NewsLogger")
    private val testPath = "src/test/resources/news.csv"

    @BeforeEach
    fun setup() {
        val file = File(testPath)
        if (file.exists()) {
            file.delete()
        }
    }

    @Test
    fun `test saveNews writes data to CSV file`() =
        runBlocking {
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
                        1704112111,
                    ),
                )

            saveNews(testPath, newsList)

            val csvFile = File(testPath)
            val csvContents = csvReader().readAllWithHeader(csvFile)

            assertEquals(2, csvContents.size)
            assertEquals("Title1", csvContents[0]["title"])
            assertEquals("Title2", csvContents[1]["title"])
        }

    @Test
    fun `test saveNews logs warning when file exists`() =
        runBlocking {
            val newsList = emptyList<News>()
            val csvFile = File(testPath)
            csvFile.createNewFile()

            mockkStatic(LoggerFactory::class)
            val loggerMock = mockk<Logger>(relaxed = true)
            every { LoggerFactory.getLogger("NewsLogger") } returns loggerMock

            saveNews(testPath, newsList)

            verify { loggerMock.warn("File already exists and will be overwritten: $testPath") }

            unmockkStatic(LoggerFactory::class)
        }

    @Test
    fun `test saveNews logs and handles IO exception`() =
        runBlocking {
            val newsList = emptyList<News>()
            val invalidPath = "/invalid/path/news.csv"

            mockkStatic(LoggerFactory::class)
            val loggerMock = mockk<Logger>(relaxed = true)
            every { LoggerFactory.getLogger("NewsLogger") } returns loggerMock

            saveNews(invalidPath, newsList)

            verify { loggerMock.error(any<String>(), any<IOException>()) }

            unmockkStatic(LoggerFactory::class)
        }
}
