package com.osama.weather.data.repository

import com.osama.weather.data.remote.WeatherApiService
import com.osama.weather.data.remote.dto.DailyDto
import com.osama.weather.data.remote.dto.ForecastResponseDto
import com.osama.weather.data.remote.dto.HourlyDto
import com.osama.weather.data.remote.dto.Minutely15Dto
import com.osama.weather.domain.model.CurrentConditions
import com.osama.weather.domain.model.DailyEntry
import com.osama.weather.domain.model.HourlyEntry
import com.osama.weather.domain.model.MinutelyEntry
import com.osama.weather.domain.model.WeatherBundle
import com.osama.weather.util.DateTimeUtils

class WeatherRepository(
    private val api: WeatherApiService = WeatherApiService()
) {
    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherBundle> =
        api.fetchForecast(latitude, longitude).map { dto -> dto.toDomain() }

    private fun ForecastResponseDto.toDomain(): WeatherBundle {
        val offset = utcOffsetSeconds
        val currentDto = current

        val current = CurrentConditions(
            time = currentDto?.time.orEmpty(),
            temperature = currentDto?.temperature2m ?: 0.0,
            apparentTemperature = currentDto?.apparentTemperature ?: currentDto?.temperature2m ?: 0.0,
            isDay = (currentDto?.isDay ?: 1) == 1,
            relativeHumidity = currentDto?.relativeHumidity2m ?: 0,
            windSpeed = currentDto?.windSpeed10m ?: 0.0,
            windDirection = currentDto?.windDirection10m ?: 0,
            windGusts = currentDto?.windGusts10m ?: 0.0,
            precipitation = currentDto?.precipitation ?: 0.0,
            rain = currentDto?.rain ?: 0.0,
            showers = currentDto?.showers ?: 0.0,
            snowfall = currentDto?.snowfall ?: 0.0,
            weatherCode = currentDto?.weatherCode ?: 0,
            cloudCover = currentDto?.cloudCover ?: 0,
            pressureMsl = currentDto?.pressureMsl ?: 1013.0,
            surfacePressure = currentDto?.surfacePressure ?: 1013.0
        )

        return WeatherBundle(
            latitude = latitude,
            longitude = longitude,
            elevationMeters = elevation ?: 0.0,
            timezone = timezone.orEmpty(),
            utcOffsetSeconds = offset,
            current = current,
            hourly = hourly?.toDomainList(offset) ?: emptyList(),
            daily = daily?.toDomainList(offset) ?: emptyList(),
            minutely15 = minutely15?.toDomainList(offset) ?: emptyList()
        )
    }

    private fun HourlyDto.toDomainList(offset: Int): List<HourlyEntry> =
        time.indices.map { i ->
            val iso = time[i]
            HourlyEntry(
                time = iso,
                epochSeconds = DateTimeUtils.parseEpochSeconds(iso, offset),
                temperature = temperature2m.at(i) ?: 0.0,
                relativeHumidity = relativeHumidity2m.at(i) ?: 0,
                dewPoint = dewPoint2m.at(i) ?: 0.0,
                apparentTemperature = apparentTemperature.at(i) ?: 0.0,
                precipitationProbability = precipitationProbability.at(i),
                precipitation = precipitation.at(i) ?: 0.0,
                rain = rain.at(i) ?: 0.0,
                showers = showers.at(i) ?: 0.0,
                snowfall = snowfall.at(i) ?: 0.0,
                snowDepth = snowDepth.at(i) ?: 0.0,
                weatherCode = weatherCode.at(i) ?: 0,
                isDay = (isDay.at(i) ?: 1) == 1,
                cloudCover = cloudCover.at(i) ?: 0,
                cloudCoverLow = cloudCoverLow.at(i) ?: 0,
                cloudCoverMid = cloudCoverMid.at(i) ?: 0,
                cloudCoverHigh = cloudCoverHigh.at(i) ?: 0,
                visibility = visibility.at(i),
                pressureMsl = pressureMsl.at(i) ?: 1013.0,
                surfacePressure = surfacePressure.at(i) ?: 1013.0,
                windSpeed10m = windSpeed10m.at(i) ?: 0.0,
                windSpeed80m = windSpeed80m.at(i),
                windSpeed120m = windSpeed120m.at(i),
                windSpeed180m = windSpeed180m.at(i),
                windDirection10m = windDirection10m.at(i) ?: 0,
                windDirection80m = windDirection80m.at(i),
                windDirection120m = windDirection120m.at(i),
                windDirection180m = windDirection180m.at(i),
                windGusts10m = windGusts10m.at(i) ?: 0.0,
                temperature80m = temperature80m.at(i),
                temperature120m = temperature120m.at(i),
                temperature180m = temperature180m.at(i),
                uvIndex = uvIndex.at(i) ?: 0.0,
                uvIndexClearSky = uvIndexClearSky.at(i),
                sunshineDuration = sunshineDuration.at(i),
                vapourPressureDeficit = vapourPressureDeficit.at(i),
                et0FaoEvapotranspiration = et0FaoEvapotranspiration.at(i),
                evapotranspiration = evapotranspiration.at(i),
                wetBulbTemperature = wetBulbTemperature2m.at(i),
                totalColumnWaterVapour = totalColumnIntegratedWaterVapour.at(i),
                boundaryLayerHeight = boundaryLayerHeight.at(i),
                freezingLevelHeight = freezingLevelHeight.at(i),
                convectiveInhibition = convectiveInhibition.at(i),
                liftedIndex = liftedIndex.at(i),
                cape = cape.at(i),
                soilTemperature0cm = soilTemperature0cm.at(i),
                soilTemperature6cm = soilTemperature6cm.at(i),
                soilTemperature18cm = soilTemperature18cm.at(i),
                soilTemperature54cm = soilTemperature54cm.at(i),
                soilMoisture0to1cm = soilMoisture0to1cm.at(i),
                soilMoisture1to3cm = soilMoisture1to3cm.at(i),
                soilMoisture3to9cm = soilMoisture3to9cm.at(i),
                soilMoisture9to27cm = soilMoisture9to27cm.at(i),
                soilMoisture27to81cm = soilMoisture27to81cm.at(i)
            )
        }

    private fun DailyDto.toDomainList(offset: Int): List<DailyEntry> =
        time.indices.map { i ->
            DailyEntry(
                date = time[i],
                weatherCode = weatherCode.at(i) ?: 0,
                temperatureMax = temperature2mMax.at(i) ?: 0.0,
                temperatureMin = temperature2mMin.at(i) ?: 0.0,
                apparentTemperatureMax = apparentTemperatureMax.at(i) ?: 0.0,
                apparentTemperatureMin = apparentTemperatureMin.at(i) ?: 0.0,
                uvIndexMax = uvIndexMax.at(i) ?: 0.0,
                uvIndexClearSkyMax = uvIndexClearSkyMax.at(i),
                windSpeedMax = windSpeed10mMax.at(i) ?: 0.0,
                windGustsMax = windGusts10mMax.at(i) ?: 0.0,
                windDirectionDominant = windDirection10mDominant.at(i) ?: 0,
                shortwaveRadiationSum = shortwaveRadiationSum.at(i),
                et0FaoEvapotranspiration = et0FaoEvapotranspiration.at(i),
                moonPhase = moonPhase.at(i),
                moonrise = moonrise.at(i),
                moonset = moonset.at(i),
                sunshineDuration = sunshineDuration.at(i),
                daylightDuration = daylightDuration.at(i) ?: 0.0,
                sunrise = sunrise.at(i) ?: "",
                sunset = sunset.at(i) ?: "",
                precipitationProbabilityMax = precipitationProbabilityMax.at(i),
                precipitationHours = precipitationHours.at(i) ?: 0.0,
                precipitationSum = precipitationSum.at(i) ?: 0.0,
                snowfallSum = snowfallSum.at(i) ?: 0.0,
                showersSum = showersSum.at(i) ?: 0.0,
                rainSum = rainSum.at(i) ?: 0.0
            )
        }

    private fun Minutely15Dto.toDomainList(offset: Int): List<MinutelyEntry> =
        time.indices.map { i ->
            val iso = time[i]
            MinutelyEntry(
                time = iso,
                epochSeconds = DateTimeUtils.parseEpochSeconds(iso, offset),
                temperature = temperature2m.at(i),
                precipitation = precipitation.at(i) ?: 0.0,
                weatherCode = weatherCode.at(i),
                isDay = (isDay.at(i) ?: 1) == 1
            )
        }

    private fun <T> List<T>.at(index: Int): T? = getOrNull(index)
}
