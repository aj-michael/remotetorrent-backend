package net.ajmichael.remotetorrent

import com.github.kittinunf.fuel.core.Manager
import com.pusher.client.PusherOptions
import com.pusher.client.Pusher
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.util.HttpAuthorizer
import org.apache.log4j.BasicConfigurator

val pusherApiKey = System.getenv().get("RT_PUSHER_APP_KEY")
val pusherChannel = "private-torrentfiles"
val pusherEvent = "client-newtorrent"

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    Manager.instance.basePath = "http://127.0.0.1:9091"
    val authorizer = HttpAuthorizer("http://experiments.ajmichael.net/remotetorrent/auth")
    val options = PusherOptions().setAuthorizer(authorizer);
    val pusher = Pusher(pusherApiKey, options)
    pusher.connect()
    val channel = pusher.subscribePrivate(pusherChannel)
    channel.bind(pusherEvent, MessageHandler(args[0]))
    while(true) {
        Thread.sleep(10000);
    }
}
