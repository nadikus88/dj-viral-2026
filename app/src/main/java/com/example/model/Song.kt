package com.example.model

data class Song(
    val id: String,
    val trackNumber: Int,
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val durationMs: Long,
    val coverUrl: String,
    val audioUrl: String
)
