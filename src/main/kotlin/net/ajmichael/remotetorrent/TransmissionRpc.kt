package net.ajmichael.remotetorrent;

data class TransmissionRpc(val method: String, val arguments: Map<String, String>)
