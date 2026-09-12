package com.osama.weather.data.repository

import com.osama.weather.data.remote.GeocodingApiService
import com.osama.weather.data.remote.dto.GeocodingResultDto
import com.osama.weather.domain.model.GeoLocation

class GeocodingRepository(
    private val api: GeocodingApiService = GeocodingApiService()
) {
    suspend fun search(query: String): Result<List<GeoLocation>> =
        api.search(query).map { dto ->
            dto.results.orEmpty()
                .map { it.toDomain() }
                // Larger, better-known places first — the "smart" ranking the
                // brief asks for (typing "بغد" should surface Baghdad, a
                // capital of ~9M people, above any small town sharing a prefix).
                .sortedByDescending { it.population ?: 0L }
        }

    private fun GeocodingResultDto.toDomain(): GeoLocation = GeoLocation(
        id = id,
        name = name,
        admin1 = admin1,
        admin2 = admin2,
        country = country,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        population = population
    )
}
