package com.osama.weather.data.repository

import com.osama.weather.data.remote.AirQualityApiService
import com.osama.weather.data.remote.dto.AirQualityHourlyDto
import com.osama.weather.domain.model.AirQualityBundle
import com.osama.weather.domain.model.HourlyAirQualityEntry
import com.osama.weather.util.DateTimeUtils

class AirQualityRepository(
    private val api: AirQualityApiService = AirQualityApiService()
) {
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityBundle> =
        api.fetchAirQuality(latitude, longitude).map { dto ->
            AirQualityBundle(
                latitude = dto.latitude,
                longitude = dto.longitude,
                timezone = dto.timezone.orEmpty(),
                utcOffsetSeconds = dto.utcOffsetSeconds,
                hourly = dto.hourly?.toDomainList(dto.utcOffsetSeconds) ?: emptyList()
            )
        }

    private fun AirQualityHourlyDto.toDomainList(utcOffsetSeconds: Int): List<HourlyAirQualityEntry> =
        time.indices.map { i ->
            val iso = time[i]
            HourlyAirQualityEntry(
                time = iso,
                epochSeconds = DateTimeUtils.parseEpochSeconds(iso, utcOffsetSeconds),
                pm10 = pm10.at(i),
                pm2_5 = pm2_5.at(i),
                carbonMonoxide = carbonMonoxide.at(i),
                carbonDioxide = carbonDioxide.at(i),
                nitrogenDioxide = nitrogenDioxide.at(i),
                sulphurDioxide = sulphurDioxide.at(i),
                ozone = ozone.at(i),
                aerosolOpticalDepth = aerosolOpticalDepth.at(i),
                dust = dust.at(i),
                uvIndex = uvIndex.at(i),
                uvIndexClearSky = uvIndexClearSky.at(i),
                methane = methane.at(i),
                europeanAqi = europeanAqi.at(i),
                europeanAqiPm2_5 = europeanAqiPm2_5.at(i),
                europeanAqiPm10 = europeanAqiPm10.at(i),
                europeanAqiNitrogenDioxide = europeanAqiNitrogenDioxide.at(i),
                europeanAqiOzone = europeanAqiOzone.at(i),
                europeanAqiSulphurDioxide = europeanAqiSulphurDioxide.at(i),
                usAqi = usAqi.at(i),
                usAqiPm2_5 = usAqiPm2_5.at(i),
                usAqiPm10 = usAqiPm10.at(i),
                usAqiNitrogenDioxide = usAqiNitrogenDioxide.at(i),
                usAqiOzone = usAqiOzone.at(i),
                usAqiSulphurDioxide = usAqiSulphurDioxide.at(i),
                usAqiCarbonMonoxide = usAqiCarbonMonoxide.at(i),
                formaldehyde = formaldehyde.at(i),
                glyoxal = glyoxal.at(i),
                peroxyacylNitrates = peroxyacylNitrates.at(i),
                seaSaltAerosol = seaSaltAerosol.at(i),
                nitrogenMonoxide = nitrogenMonoxide.at(i)
            )
        }

    private fun <T> List<T>.at(index: Int): T? = getOrNull(index)
}
