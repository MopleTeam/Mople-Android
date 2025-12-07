package com.moim.core.remote.datasource.opengraph

import com.moim.core.common.model.OpenGraph

interface OpenGraphRemoteDataSource {

    suspend fun getOpenGraph(url: String?): OpenGraph?
}