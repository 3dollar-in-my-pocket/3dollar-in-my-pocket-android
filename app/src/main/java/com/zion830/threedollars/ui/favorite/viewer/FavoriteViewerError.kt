package com.zion830.threedollars.ui.favorite.viewer

sealed interface FavoriteViewerError {
    data class ApiError(val message: String) : FavoriteViewerError
    data object Unknown : FavoriteViewerError
}
