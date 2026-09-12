package com.osama.weather.ui.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.osama.weather.WeatherApplication
import com.osama.weather.domain.model.GeoLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<GeoLocation> = emptyList(),
    val recentSearches: List<GeoLocation> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val app get() = getApplication<WeatherApplication>()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(recentSearches = app.preferencesManager.getRecentSearches()) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _uiState.update { it.copy(results = emptyList(), isSearching = false, hasSearched = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(320) // debounce — avoid firing a request on every keystroke
            _uiState.update { it.copy(isSearching = true) }
            app.geocodingRepository.search(query)
                .onSuccess { results ->
                    _uiState.update { it.copy(results = results, isSearching = false, hasSearched = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(results = emptyList(), isSearching = false, hasSearched = true) }
                }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            app.preferencesManager.clearRecentSearches()
            _uiState.update { it.copy(recentSearches = emptyList()) }
        }
    }
}
