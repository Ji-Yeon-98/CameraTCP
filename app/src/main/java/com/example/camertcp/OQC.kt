package com.example.camertcp

import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class OQC(
    private var ipAddress: String,
    private var portNumber: Int
) {
    private lateinit var socketChannel : SocketChannel

    fun connect() : String{
        try{
            val serverAddress = InetSocketAddress(ipAddress, portNumber)
            socketChannel = SocketChannel.open()
            socketChannel.connect(serverAddress)
        }catch (e : Exception){
            return when(e){
                is ConnectException -> {
                    OQCInfo.CONNECT_EXCEPTION
                }

                is UnknownHostException -> {
                    OQCInfo.UNKNOWN_HOST_EXCEPTION
                }

                is SocketException -> {
                    OQCInfo.SOCKET_EXCEPTION
                }

                else -> {
                    OQCInfo.EXCEPTION
                }
            }
        }

        return OQCInfo.MESSAGE_SUCCESS
    }

    fun disconnect() : String{
        socketChannel.close()

        return OQCInfo.MESSAGE_FAIL
    }

    fun getData(code : Int, bodySize: Int, sendBodyList : MutableList<MutableList<String>>): ByteBuffer {

        val buffer = ByteBuffer.allocate(OQCInfo.HEADER_DEFAULT_SIZE + bodySize)
        buffer.put(OQCInfo.HEADER_NAME_VALUE.toByteArray())
        buffer.put(OQCInfo.HEADER_VERSION_VALUE.toByteArray())
        buffer.putInt(code)
        buffer.putInt(bodySize)

        if(sendBodyList.isNotEmpty()){
            for(sendList in sendBodyList){
                if(sendList[2] == OQCInfo.TYPE_CHAR){
                    val paddedString = sendList[3].padEnd(sendList[1].toInt(), ' ')
                    buffer.put(paddedString.toByteArray())
                }else if(sendList[2] == OQCInfo.TYPE_INT){
                    buffer.putInt(sendList[3].toInt())
                }
            }
        }

        buffer.flip()
        socketChannel.write(buffer)

        val receiveBuffer = ByteBuffer.allocate(OQCInfo.HEADER_DEFAULT_SIZE)
        val bytesRead = socketChannel.read(receiveBuffer)

        if (bytesRead != -1) {
            val responseBodySize = readHeader(code, receiveBuffer)
            val receiveBodyBuffer = ByteBuffer.allocate(responseBodySize)

            var totalBytesRead = 0
            while (totalBytesRead < responseBodySize) {
                val bodyBytesRead = socketChannel.read(receiveBodyBuffer)
                if (bodyBytesRead == -1) {
                    break
                }
                totalBytesRead += bodyBytesRead
            }

            return receiveBodyBuffer
        }else {
            return ByteBuffer.allocate(0)
        }
    }

    private fun readHeader(code: Int, receiveBuffer: ByteBuffer): Int {
        receiveBuffer.flip()

        val name = ByteArray(OQCInfo.HEADER_NAME_SIZE)
        receiveBuffer.get(name)

        val version = ByteArray(OQCInfo.HEADER_VERSION_SIZE)
        receiveBuffer.get(version)

        val responseCode = receiveBuffer.int

        if (code + 1000 == responseCode || code + 100 == responseCode) {
            return receiveBuffer.int
        } else {
            throw IllegalArgumentException(OQCInfo.EXCEPTION)
        }
    }

    fun isSocketConnected(): Boolean {
        return socketChannel.isConnected
    }
}