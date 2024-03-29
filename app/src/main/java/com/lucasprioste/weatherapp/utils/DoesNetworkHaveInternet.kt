package com.lucasprioste.weatherapp.utils

import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

object DoesNetworkHaveInternet {
    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try{
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress("www.google.com", 80), 1500)
            socket.close()
            true
        }catch (e: IOException){
            false
        }
    }
}