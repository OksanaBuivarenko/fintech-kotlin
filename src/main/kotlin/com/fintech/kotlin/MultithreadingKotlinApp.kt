package com.fintech.kotlin

import com.fintech.kotlin.data.News
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.time.LocalDate
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.channels.Channel

fun main() = runBlocking {

    val period = LocalDate.parse("2024-09-14")..LocalDate.parse("2024-09-15")
    val app = KotlinAppHw9()

    val durationTimeWith15Threads: Long = measureTimeMillis {
        app.run(period, 10, 15, "src\\main\\resources\\news.csv");
    }

    val durationTimeFromHw4: Long = measureTimeMillis {
        val rateList: List<News> = getNewsListForRange(period).getMostRatedNews(20, period)
        if (rateList.isNotEmpty()) {
            saveCSV("src\\main\\resources\\news.csv", rateList)
        }
    }
    println("Duration time without workers - $durationTimeFromHw4")

    println("Duration time with 9 threads- $durationTimeWith15Threads")
}


@SpringBootApplication
class KotlinAppHw9 {

    val log = KotlinLogging.logger {}

    val channel = Channel<List<News>>(Channel.UNLIMITED)

    suspend fun run(period: ClosedRange<LocalDate>, workersCount: Int, threadsCount: Int, filePath: String) = coroutineScope {
        val workers = List(workersCount) { workerId ->
            launch(newFixedThreadPoolContext(threadsCount, "workersThreadPool")) {
                worker(workerId, period, workersCount)
            }
        }

        val processorJob = launch { processor(filePath) }

        workers.joinAll()
        channel.close()
        processorJob.join()
    }

    private suspend fun processor(filePath: String) {
        for (news in channel) {
            saveCSV(filePath, news)
        }
    }

    suspend fun worker(id: Int, period: ClosedRange<LocalDate>, workersCount: Int) {
        var currentPage = id

        var isInRange = true
        while (isInRange) {
            log.debug { "Worker $id get News from page $currentPage" }
            val newsList: List<News> = getNews(page = currentPage)
            if (newsList.isEmpty()) {
                isInRange = false
            } else {
                val rangeNewsList: MutableList<News> = ArrayList()
                for (news in newsList) {
                    if (period.start.isAfter(news.getLocalDate())) {
                        log.debug { "Обработана новость, с датой, выходящей за начало периода - ${news.getLocalDate()}" }
                        isInRange = false
                        break
                    } else {
                        if(news.getLocalDate() in period) {
                            rangeNewsList.add(news)
                            log.debug { "Обработана новость, с датой - ${news.getLocalDate()}" }
                        }
                    }
                }
                channel.send(rangeNewsList)
                currentPage += workersCount
            }
        }
    }
}