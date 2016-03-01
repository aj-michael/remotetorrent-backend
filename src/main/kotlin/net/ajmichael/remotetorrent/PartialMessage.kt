package net.ajmichael.remotetorrent

import java.net.URLEncoder

data class PartialMessage(val id: String, val piece: Int, val pieces: Int, val data: String)