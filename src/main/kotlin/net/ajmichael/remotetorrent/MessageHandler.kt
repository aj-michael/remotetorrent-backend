package net.ajmichael.remotetorrent

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Manager
import com.pusher.client.channel.SubscriptionEventListener
import org.apache.log4j.Logger

class MessageHandler(val downloadDir: String) : SubscriptionEventListener {
    val log = Logger.getLogger(this.javaClass)
    val partialsInProgress: MutableMap<String, MutableList<PartialMessage>> = hashMapOf()
    val mapper = jacksonObjectMapper()

    override fun onEvent(channel: String?, event: String?, data: String?) {
        val partial: PartialMessage = mapper.readValue(data!!)
        log.info("Received pusher event for " + partial.id + " piece " + partial.piece + " of " + partial.pieces)

        if (!partialsInProgress.containsKey(partial.id)) {
            partialsInProgress.put(partial.id, arrayListOf())
        }
        val partials = partialsInProgress.get(partial.id)!!
        partials.add(partial)
        if (partials.size == partial.pieces) {
            val rpc = TransmissionRpc("torrent-add", mapOf("metainfo" to combine(partials), "download-dir" to downloadDir))
            log.info("Sending request now for " + partial.id)
            val body = mapper.writeValueAsBytes(rpc)
            Fuel.post("http://127.0.0.1:9091/transmission/rpc").body(body).response { request, response, result ->
                if (response.httpStatusCode == 409) {
                    val session = response.httpResponseHeaders.get("X-Transmission-Session-Id")!!.first()
                    Manager.instance.baseHeaders = mapOf("X-Transmission-Session-Id" to session)
                    log.info("Received 409, trying again with session id")
                    Fuel.post("/transmission/rpc").body(body).response { request, response, result ->
                        log.info(response)
                    }
                }
            }
            partialsInProgress.remove(partial.id)
        }
    }

    fun combine(partials: MutableList<PartialMessage>): String =
            partials.sortedBy { it.piece }.map { it.data }.reduce { t, n -> t + n }
}