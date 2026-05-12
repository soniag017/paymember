package com.paymember.ui.components

import androidx.compose.ui.graphics.Color

object Services {
    val fallback = ServiceBrand("fallback", Color(0xFF1F4634), Color.White, "PM")

    private val brands = mapOf(
        "netflix" to ServiceBrand("netflix", Color(0xFF0F0F10), Color(0xFFE50914), "N"),
        "spotify" to ServiceBrand("spotify", Color(0xFF0E2818), Color(0xFF1ED760), "S"),
        "disney" to ServiceBrand("disney", Color(0xFF0B1B3E), Color.White, "D+"),
        "chatgpt" to ServiceBrand("chatgpt", Color(0xFF0E2A22), Color(0xFF10A37F), "AI"),
        "icloud" to ServiceBrand("icloud", Color(0xFFE8F2FB), Color(0xFF1B7BE8), "iC"),
        "microsoft-365" to ServiceBrand("office", Color(0xFF22152A), Color(0xFFE8916B), "365"),
        "max" to ServiceBrand("max", Color(0xFF0F1A4A), Color(0xFF7AA0FF), "M"),
        "prime-video" to ServiceBrand("prime", Color(0xFF0E1A28), Color(0xFF7AC4F0), "P"),
        "amazon-prime" to ServiceBrand("amazon-prime", Color(0xFF0E1A28), Color(0xFF7AC4F0), "P"),
        "apple-tv" to ServiceBrand("appletv", Color(0xFF111111), Color.White, "tv+"),
        "youtube-premium" to ServiceBrand("youtube", Color(0xFF1A0F10), Color(0xFFFF3D3D), "YT"),
        "youtube-music" to ServiceBrand("youtube-music", Color(0xFF1A0F10), Color(0xFFFF3D3D), "YM"),
        "google-one" to ServiceBrand("google-one", Color(0xFFE8F2FB), Color(0xFF4285F4), "G1"),
        "apple-music" to ServiceBrand("apple-music", Color(0xFF2A1016), Color(0xFFFA243C), "AM"),
        "amazon-music" to ServiceBrand("amazon-music", Color(0xFF0E1A28), Color(0xFF7AC4F0), "AM"),
        "filmin" to ServiceBrand("filmin", Color(0xFF191817), Color(0xFFE8D08C), "F"),
        "skyshowtime" to ServiceBrand("skyshowtime", Color(0xFF21163D), Color(0xFFB5AEE6), "S"),
        "movistar-plus" to ServiceBrand("movistar-plus", Color(0xFF0A2D3A), Color(0xFFA6C6E8), "M+"),
        "dazn" to ServiceBrand("dazn", Color(0xFF101010), Color.White, "DZ"),
        "crunchyroll" to ServiceBrand("crunchyroll", Color(0xFF2A180E), Color(0xFFF47521), "CR"),
        "deezer" to ServiceBrand("deezer", Color(0xFF24112C), Color(0xFFB5AEE6), "DZ"),
        "tidal" to ServiceBrand("tidal", Color(0xFF101010), Color.White, "T"),
        "audible" to ServiceBrand("audible", Color(0xFF2A1C08), Color(0xFFE8D08C), "A"),
        "uber-one" to ServiceBrand("uber-one", Color(0xFF101010), Color.White, "U"),
        "glovo-prime" to ServiceBrand("glovo-prime", Color(0xFF28220A), Color(0xFFE8D08C), "G"),
        "just-eat-plus" to ServiceBrand("just-eat-plus", Color(0xFF2D1708), Color(0xFFE8916B), "JE"),
        "carrefour-plus" to ServiceBrand("carrefour-plus", Color(0xFF0B1B3E), Color(0xFFA6C6E8), "C+"),
        "dropbox" to ServiceBrand("dropbox", Color(0xFF0E1A3A), Color(0xFFA6C6E8), "DB"),
        "canva" to ServiceBrand("canva", Color(0xFF0A2A2A), Color(0xFFA6C6E8), "C"),
        "adobe-cc" to ServiceBrand("adobe-cc", Color(0xFF2A1010), Color(0xFFE8916B), "A"),
        "playstation-plus" to ServiceBrand("playstation-plus", Color(0xFF0B1B3E), Color(0xFFA6C6E8), "PS"),
        "xbox-game-pass" to ServiceBrand("xbox-game-pass", Color(0xFF0E2818), Color(0xFF9CB87A), "X"),
        "nintendo-online" to ServiceBrand("nintendo-online", Color(0xFF2A1010), Color(0xFFE8916B), "N"),
        "geforce-now" to ServiceBrand("geforce-now", Color(0xFF16260C), Color(0xFF9CB87A), "GF"),
        "ea-play" to ServiceBrand("ea-play", Color(0xFF2A1010), Color(0xFFE8916B), "EA"),
        "claude" to ServiceBrand("claude", Color(0xFF2A1710), Color(0xFFE8916B), "CL"),
        "gemini" to ServiceBrand("gemini", Color(0xFF0B1B3E), Color(0xFFA6C6E8), "G"),
        "perplexity" to ServiceBrand("perplexity", Color(0xFF0A2A2A), Color(0xFFA6C6E8), "P"),
        "notion-ai" to ServiceBrand("notion-ai", Color(0xFF101010), Color.White, "N")
    )

    fun brandFor(id: String): ServiceBrand = brands[id] ?: fallback.copy(mark = id.take(2).uppercase())
}
