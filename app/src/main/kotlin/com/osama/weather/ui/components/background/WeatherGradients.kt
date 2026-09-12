package com.osama.weather.ui.components.background

import androidx.compose.ui.graphics.Color
import com.osama.weather.domain.model.TimeOfDay
import com.osama.weather.domain.model.WeatherCondition

/**
 * Every one of the 13 [WeatherCondition] families gets its own hand-tuned
 * sky for each of the 4 times of day — 52 distinct backgrounds in total, so
 * that literally every weather_code the API can return renders with artwork
 * built specifically for it (never a generic fallback). Ordered top-to-bottom
 * for [androidx.compose.ui.graphics.Brush.verticalGradient].
 */
object WeatherGradients {

    fun colorsFor(condition: WeatherCondition, time: TimeOfDay): List<Color> = when (condition) {
        WeatherCondition.CLEAR -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF4FA6E8), Color(0xFF1669C7), Color(0xFF0A4BA0))
            TimeOfDay.DAWN -> listOf(Color(0xFF5B7FC4), Color(0xFFE49CA6), Color(0xFFFFC988))
            TimeOfDay.DUSK -> listOf(Color(0xFF3A4E92), Color(0xFFC96E93), Color(0xFFFF8F5C))
            TimeOfDay.NIGHT -> listOf(Color(0xFF0B1642), Color(0xFF060B24), Color(0xFF020509))
        }
        WeatherCondition.MOSTLY_CLEAR -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF5CB2EA), Color(0xFF2C7DD1), Color(0xFF1A5CA8))
            TimeOfDay.DAWN -> listOf(Color(0xFF6484C8), Color(0xFFE2A2AA), Color(0xFFFFCD91))
            TimeOfDay.DUSK -> listOf(Color(0xFF41539A), Color(0xFFCC7796), Color(0xFFFF9863))
            TimeOfDay.NIGHT -> listOf(Color(0xFF101D4C), Color(0xFF0A122E), Color(0xFF03060F))
        }
        WeatherCondition.PARTLY_CLOUDY -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF6FB3E0), Color(0xFF5588B8), Color(0xFF3D6690))
            TimeOfDay.DAWN -> listOf(Color(0xFF6D84B0), Color(0xFFD9A2AC), Color(0xFFF3C089))
            TimeOfDay.DUSK -> listOf(Color(0xFF4B5786), Color(0xFFB9789A), Color(0xFFE68F63))
            TimeOfDay.NIGHT -> listOf(Color(0xFF19244A), Color(0xFF121A34), Color(0xFF080C18))
        }
        WeatherCondition.OVERCAST -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF8FA0AE), Color(0xFF6B7D8C), Color(0xFF4E5F6E))
            TimeOfDay.DAWN -> listOf(Color(0xFF7A8598), Color(0xFFB48F97), Color(0xFFCBA382))
            TimeOfDay.DUSK -> listOf(Color(0xFF5B6478), Color(0xFF97717F), Color(0xFFB87E68))
            TimeOfDay.NIGHT -> listOf(Color(0xFF262E38), Color(0xFF181E26), Color(0xFF0A0D11))
        }
        WeatherCondition.FOG -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFFCDD5DA), Color(0xFFAEB9C0), Color(0xFF919FA8))
            TimeOfDay.DAWN -> listOf(Color(0xFFC3C4C9), Color(0xFFD3B4AF), Color(0xFFE0BC9E))
            TimeOfDay.DUSK -> listOf(Color(0xFFA7A9B4), Color(0xFFB69199), Color(0xFFC08F76))
            TimeOfDay.NIGHT -> listOf(Color(0xFF3C4148), Color(0xFF262A2F), Color(0xFF14171A))
        }
        WeatherCondition.DRIZZLE -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF7E97A9), Color(0xFF5F7789), Color(0xFF445A6C))
            TimeOfDay.DAWN -> listOf(Color(0xFF71809C), Color(0xFFAB8B97), Color(0xFFC0A186))
            TimeOfDay.DUSK -> listOf(Color(0xFF4E5C79), Color(0xFF8C6D82), Color(0xFFA57964))
            TimeOfDay.NIGHT -> listOf(Color(0xFF1D2733), Color(0xFF141B24), Color(0xFF090D12))
        }
        WeatherCondition.RAIN -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF5C7186), Color(0xFF465A6C), Color(0xFF2F3E4C))
            TimeOfDay.DAWN -> listOf(Color(0xFF5A6786), Color(0xFF8E7186), Color(0xFF9C7F6C))
            TimeOfDay.DUSK -> listOf(Color(0xFF3F4A68), Color(0xFF6E5771), Color(0xFF7C5F52))
            TimeOfDay.NIGHT -> listOf(Color(0xFF141B26), Color(0xFF0D131B), Color(0xFF05080C))
        }
        WeatherCondition.FREEZING_RAIN -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF9BC0D4), Color(0xFF6E93A8), Color(0xFF4C6C80))
            TimeOfDay.DAWN -> listOf(Color(0xFF8AA6C6), Color(0xFFBB9CA6), Color(0xFFCFAE92))
            TimeOfDay.DUSK -> listOf(Color(0xFF5E7591), Color(0xFF977C93), Color(0xFFA98770))
            TimeOfDay.NIGHT -> listOf(Color(0xFF17232E), Color(0xFF101B24), Color(0xFF060B10))
        }
        WeatherCondition.SNOW -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFFE3EDF3), Color(0xFFC2D3DE), Color(0xFF9FB6C6))
            TimeOfDay.DAWN -> listOf(Color(0xFFC9D6E6), Color(0xFFE3C3C6), Color(0xFFEFD0A8))
            TimeOfDay.DUSK -> listOf(Color(0xFFA9B7D2), Color(0xFFC8A0B4), Color(0xFFD8A98D))
            TimeOfDay.NIGHT -> listOf(Color(0xFF1B2540), Color(0xFF121A34), Color(0xFF090D1C))
        }
        WeatherCondition.RAIN_SHOWERS -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF6BAAE0), Color(0xFF5786B6), Color(0xFF3E6088))
            TimeOfDay.DAWN -> listOf(Color(0xFF6C8AC0), Color(0xFFCB9DAA), Color(0xFFF0BE8B))
            TimeOfDay.DUSK -> listOf(Color(0xFF465493), Color(0xFFAA7495), Color(0xFFE08E64))
            TimeOfDay.NIGHT -> listOf(Color(0xFF15224A), Color(0xFF0F1836), Color(0xFF060A18))
        }
        WeatherCondition.SNOW_SHOWERS -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFFC9DFEE), Color(0xFF9FBBD0), Color(0xFF7C9AB4))
            TimeOfDay.DAWN -> listOf(Color(0xFFBACAE6), Color(0xFFD9BAC6), Color(0xFFE9C89C))
            TimeOfDay.DUSK -> listOf(Color(0xFF8E9DC8), Color(0xFFBB93AC), Color(0xFFCE9C7C))
            TimeOfDay.NIGHT -> listOf(Color(0xFF192142), Color(0xFF0F1530), Color(0xFF060812))
        }
        WeatherCondition.THUNDERSTORM -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF4B5461), Color(0xFF373E49), Color(0xFF1F2530))
            TimeOfDay.DAWN -> listOf(Color(0xFF474F6E), Color(0xFF6B5470), Color(0xFF7C5E56))
            TimeOfDay.DUSK -> listOf(Color(0xFF383F60), Color(0xFF56415F), Color(0xFF5C4640))
            TimeOfDay.NIGHT -> listOf(Color(0xFF14161E), Color(0xFF0C0D13), Color(0xFF030405))
        }
        WeatherCondition.THUNDERSTORM_HAIL -> when (time) {
            TimeOfDay.DAY -> listOf(Color(0xFF454654), Color(0xFF33323F), Color(0xFF19181F))
            TimeOfDay.DAWN -> listOf(Color(0xFF42456C), Color(0xFF624C6E), Color(0xFF6E5250))
            TimeOfDay.DUSK -> listOf(Color(0xFF34365C), Color(0xFF4E3C5A), Color(0xFF523D38))
            TimeOfDay.NIGHT -> listOf(Color(0xFF100F17), Color(0xFF0A090E), Color(0xFF020203))
        }
    }
}
