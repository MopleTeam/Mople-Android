package com.moim.core.remote.datasource.opengraph

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.moim.core.common.di.IoDispatcher
import com.moim.core.common.model.OpenGraph
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class OpenGraphRemoteDataSourceImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : OpenGraphRemoteDataSource {
    private val client =
        HttpClient {
            followRedirects = true
            install(HttpTimeout) { requestTimeoutMillis = 15000 }
        }

    override suspend fun getOpenGraph(url: String?): OpenGraph? {
        return withContext(ioDispatcher) {
            try {
                if (url.isNullOrEmpty()) return@withContext null

                val validUrl =
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        "https://$url"
                    } else {
                        url
                    }

                var currentUrl = validUrl
                var redirectCount = 0
                val maxRedirects = 10

                // 리다이렉트 수동 추적
                while (redirectCount < maxRedirects) {
                    val response: HttpResponse =
                        client.get(currentUrl) {
                            header(
                                "User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                            )
                            header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                            header("Referer", "https://www.google.com")
                        }

                    // 리다이렉트 확인 (3xx 상태 코드)
                    if (response.status.value in 300..399) {
                        val location = response.headers["Location"]
                        Timber.e("location= $location")

                        if (location != null) {
                            // 절대 URL인지 확인
                            currentUrl =
                                if (location.startsWith("http")) {
                                    location
                                } else {
                                    // 상대 URL 처리 개선
                                    val currentUri = java.net.URI(currentUrl)
                                    currentUri.resolve(location).toString()
                                }
                            redirectCount++
                            continue
                        } else {
                            break
                        }
                    }

                    val document = Ksoup.parseGetRequest(url = currentUrl)
                    val title =
                        document.selectFirst("meta[property=og:title]")?.attr("content")
                            ?: document.selectFirst("meta[name=og:title]")?.attr("content")
                            ?: document.selectFirst("meta[property=twitter:title]")?.attr("content")
                            ?: document.selectFirst("meta[name=twitter:title]")?.attr("content")
                            ?: document.title()

                    val description =
                        document.selectFirst("meta[property=og:description]")?.attr("content")
                            ?: document.selectFirst("meta[name=og:description]")?.attr("content")
                            ?: document.selectFirst("meta[property=twitter:description]")?.attr("content")
                            ?: document.selectFirst("meta[name=twitter:description]")?.attr("content")
                            ?: document.selectFirst("meta[name=description]")?.attr("content")

                    val imageUrl =
                        document.selectFirst("meta[property=og:image]")?.attr("content")
                            ?: document.selectFirst("meta[name=og:image]")?.attr("content")
                            ?: document.selectFirst("meta[property=twitter:image]")?.attr("content")
                            ?: document.selectFirst("meta[name=twitter:image]")?.attr("content")
                            ?: document.selectFirst("link[rel=image_src]")?.attr("href")

                    val openGraph =
                        OpenGraph(
                            url = url,
                            title = title.takeIf { it.isNotBlank() } ?: url,
                            description = description.takeIf { it?.isNotBlank() == true } ?: "no content",
                            imageUrl = imageUrl.takeIf { it?.isNotBlank() == true },
                        )

                    return@withContext if (openGraph.isEmpty) {
                        null
                    } else {
                        openGraph
                    }
                }
                null
            } catch (e: Exception) {
                null
            }
        }
    }
}
