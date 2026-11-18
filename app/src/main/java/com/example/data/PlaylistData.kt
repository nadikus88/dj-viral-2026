package com.example.data

import com.example.model.Song

object PlaylistData {
    val songs: List<Song> = listOf(
        Song(
            id = "dj_01",
            trackNumber = 1,
            title = "DJ Komang Rimex - Full Bass Viral TikTok",
            artist = "DJ Opus Remix",
            album = "DJ Viral TikTok 2026",
            duration = "03:45",
            durationMs = 225_000L,
            coverUrl = "https://picsum.photos/seed/djviral01/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        Song(
            id = "dj_02",
            trackNumber = 2,
            title = "DJ Cikini Ke Gondangdia - Jedag Jedug",
            artist = "DJ Nofin Asia",
            album = "Jedag Jedug Collection",
            duration = "04:12",
            durationMs = 252_000L,
            coverUrl = "https://picsum.photos/seed/djviral02/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        Song(
            id = "dj_03",
            trackNumber = 3,
            title = "DJ Aiya Susanti - Slow Bass Remix",
            artist = "DJ Desa Official",
            album = "Slow Bass Viral",
            duration = "03:30",
            durationMs = 210_000L,
            coverUrl = "https://picsum.photos/seed/djviral03/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        ),
        Song(
            id = "dj_04",
            trackNumber = 4,
            title = "DJ Mangku Purel - Koplo Breakbeat",
            artist = "DJ Komang",
            album = "Breakbeat Nusantara",
            duration = "04:05",
            durationMs = 245_000L,
            coverUrl = "https://picsum.photos/seed/djviral04/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
        ),
        Song(
            id = "dj_05",
            trackNumber = 5,
            title = "DJ Rungkad - Full Bass TikTok Viral",
            artist = "DJ Bonbon",
            album = "DJ Viral TikTok 2026",
            duration = "03:52",
            durationMs = 232_000L,
            coverUrl = "https://picsum.photos/seed/djviral05/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
        ),
        Song(
            id = "dj_06",
            trackNumber = 6,
            title = "DJ Mapopo Syalala - Jedag Jedug Remix",
            artist = "DJ TikTok Remix",
            album = "Jedag Jedug Collection",
            duration = "03:28",
            durationMs = 208_000L,
            coverUrl = "https://picsum.photos/seed/djviral06/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"
        ),
        Song(
            id = "dj_07",
            trackNumber = 7,
            title = "DJ Nemen - Slowed + Reverb Bass",
            artist = "DJ Jawa Viral",
            album = "Slowed & Reverb Hits",
            duration = "04:18",
            durationMs = 258_000L,
            coverUrl = "https://picsum.photos/seed/djviral07/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3"
        ),
        Song(
            id = "dj_08",
            trackNumber = 8,
            title = "DJ Ikan Dalam Kolam - Breakbeat Viral",
            artist = "DJ Mbon Mbon",
            album = "Breakbeat Nusantara",
            duration = "03:50",
            durationMs = 230_000L,
            coverUrl = "https://picsum.photos/seed/djviral08/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
        ),
        Song(
            id = "dj_09",
            trackNumber = 9,
            title = "DJ Malam Pagi - Remix Full Bass",
            artist = "DJ Remix Indo",
            album = "DJ Viral TikTok 2026",
            duration = "03:40",
            durationMs = 220_000L,
            coverUrl = "https://picsum.photos/seed/djviral09/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3"
        ),
        Song(
            id = "dj_10",
            trackNumber = 10,
            title = "DJ Gara Gara Sebotol Minuman - Viral",
            artist = "DJ Pantura Bass",
            album = "Pantura Club Mix",
            duration = "04:00",
            durationMs = 240_000L,
            coverUrl = "https://picsum.photos/seed/djviral10/500/500",
            // GANTI DENGAN LINK DIRECT MP3 LAGU DJ ANDA
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3"
        )
    )

    fun findById(id: String): Song? = songs.firstOrNull { it.id == id }
}
