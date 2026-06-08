package com.metrolist.music.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VideoState {
    private val _videoUrls = MutableStateFlow<Map<String, String>>(emptyMap())
    val videoUrls: StateFlow<Map<String, String>> = _videoUrls.asStateFlow()

    private val _isVideoModeActive = MutableStateFlow(false)
    val isVideoModeActive: StateFlow<Boolean> = _isVideoModeActive.asStateFlow()

    fun updateVideoUrl(mediaId: String, url: String?) {
        if (url != null) {
            _videoUrls.value = _videoUrls.value + (mediaId to url)
        }
    }

    fun setVideoMode(active: Boolean) {
        _isVideoModeActive.value = active
    }
}
