package com.akcreation.gitsilent.utils

import kotlinx.serialization.json.Json

object JsonUtil {
    val j = Json { ignoreUnknownKeys = true; encodeDefaults = false }
}
