package net.ajmichael.remotetorrent

import com.github.kittinunf.fuel.core.Manager
import com.pusher.client.Pusher
import org.apache.log4j.BasicConfigurator

val pusherApiKey = System.getenv().get("RT_PUSHER_APP_KEY")
val pusherChannel = "torrentfiles"
val pusherEvent = "newtorrent"

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    Manager.instance.basePath = "http://127.0.0.1:9091"
    val pusher = Pusher(pusherApiKey)
    pusher.connect()
    val channel = pusher.subscribe(pusherChannel)
    channel.bind(pusherEvent) { channel, event, data ->

    }
    channel.bind(pusherEvent, MessageHandler(args[0]))
    while(true) {}
}
