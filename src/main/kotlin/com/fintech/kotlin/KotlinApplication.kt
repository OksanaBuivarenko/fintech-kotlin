package com.fintech.kotlin

import com.fintech.kotlin.data.News
import com.fintech.kotlin.data.Response
import com.fintech.kotlin.dsl.prettyPrint
import com.fintech.kotlin.dsl.prettyPrintHTML
import kotlinx.serialization.json.*
import org.springframework.boot.autoconfigure.SpringBootApplication

import java.time.LocalDate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import java.io.*

import kotlin.collections.ArrayList

@SpringBootApplication
class KotlinApplication

private val logger = KotlinLogging.logger {}

suspend fun main(args: Array<String>) {
    val list: List<News> = getNews(3, 2)
    if (list.isNotEmpty()) {
        logger.info { "Cписок новостей успешно получен" }
        println(prettyPrint(list))
    }

    val period = LocalDate.parse("2024-09-14")..LocalDate.parse("2024-09-15")
    val rateList: List<News> = getNewsListForRange(period).getMostRatedNews(20, period)

    if (rateList.isNotEmpty()) {
        logger.info { "Cписок новостей успешно получен" }
        saveCSV("src\\main\\resources\\news.csv", rateList)
        saveHTML("src\\main\\resources\\news.html", rateList)
    }
}

suspend fun getNews(count: Int = 100, page: Int = 1): List<News> {
    return try {
        HttpClient {
            install(Logging)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }.use {
            it.get(
                "https://kudago.com/public-api/v1.2/events/?expand=place&page=$page&" +
                        "page_size=$count&fields=id,title,publication_date,place,description,site_url,favorites_count," +
                        "comments_count&location=spb&text_format=text&order_by=-publication_date"
            ).body<Response>().results
        }
    } catch (e: Exception) {
        logger.error { "Не удалось получить новости с сайта" }
        emptyList()
    }
}

suspend fun getNewsListForRange(period: ClosedRange<LocalDate>): List<News> {
    val rangeNewsList: MutableList<News> = ArrayList<News>()
    var pageNumber = 1
    var isInRange = true
    while (isInRange) {
        val newsList: List<News> = getNews(page = pageNumber)
        if (newsList.isEmpty()) {
            isInRange = false
        } else {
            for (news in newsList) {
                if (period.start.isAfter(news.getLocalDate())) {
                    logger.debug { "Обработана новость, с датой, выходящей за начало периода - ${news.getLocalDate()}" }
                    isInRange = false
                    break
                } else {
                    rangeNewsList.add(news)
                    logger.debug { "Обработана новость, с датой - ${news.getLocalDate()}" }
                }
            }
            pageNumber++
        }
    }
    return rangeNewsList
}

//фильтр должен полностью быть в методе выше, чтобы не сохранять лишние данные,
//но оставила здесь, чтобы соответствовало параметрам функции из задания
fun List<News>.getMostRatedNews(count: Int, period: ClosedRange<LocalDate>): List<News> {
    return this.filter { it.getLocalDate() in period }.sortedBy { it.rating }.takeLast(count)
}

fun isValidFile(file: File): Boolean {
    val parentDir = File(file.absoluteFile.parentFile.absolutePath)
    if (!parentDir.exists() && !parentDir.isDirectory) {
        logger.warn { "Указанный путь к файлу ${file.path} не валиден" }
        return false
    } else if (file.exists()) {
        logger.warn { "Файл ${file.name} уже существует" }
        return false
    } else {
        return true
    }
}

fun saveCSV(path: String, news: Collection<News>) {
    val file = File(path)
    if (isValidFile(file)) {
        val fileWriter = FileWriter(file)
        try {
            CSVFormat.DEFAULT.print(fileWriter).apply {
                printRecord(
                    "Id", "Title", "Place", "Description", "SiteUrl", "FavoritesCount", "CommentsCount",
                    "Rating", "PublicationDate"
                )
                news.forEach { (id, title, place, description, siteUrl, favoritesCount, commentsCount, publicationDate)
                    ->
                    printRecord(
                        id, title, place?.id.toString(), description, siteUrl, favoritesCount, commentsCount,
                        publicationDate
                    )
                }
            }
            logger.info { "Cписок новостей успешно записан в файл ${file.name}" }
        } catch (e: Exception) {
            logger.error { "Не удалось записать новости в файл ${file.name}" }
        } finally {
            fileWriter.close()
        }
    }
}

fun saveHTML(path: String, news: Collection<News>) {
    val file = File(path)
    if (isValidFile(file)) {
        val fileWriter = FileOutputStream(file)
        try {
            val html = prettyPrintHTML(news).toString()
            val data = html.toByteArray()
            fileWriter.write(data)
            logger.info { "Cписок новостей успешно записан в файл ${file.name}" }
        } catch (e: Exception) {
            logger.error { "Не удалось записать новости в файл ${file.name}" }
        } finally {
            fileWriter.close()
        }
    }
}