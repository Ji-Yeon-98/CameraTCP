package com.example.camertcp

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class OQC(
    private var ipAddress: String,
    private var portNumber: Int,
    private var code: Int,
    private var bodySize: Int,
    private var _liveResult : MutableLiveData<String>
) {
    private val serverAddress = InetSocketAddress(ipAddress, portNumber)
    private val socketChannel: SocketChannel = SocketChannel.open()

    suspend fun getData(){
        socketChannel.connect(serverAddress)

        val buffer = writeHeaderBuffer()
        buffer.flip()
        socketChannel.write(buffer)

        val receiveBuffer = ByteBuffer.allocate(OQCInfo.HEADER_DEFAULT_SIZE)
        val bytesRead = socketChannel.read(receiveBuffer)

        if (bytesRead != -1) {
            readHeader(receiveBuffer)
        }

        socketChannel.close()
    }

    suspend fun getDataWithGetBodyList(getBodyList : MutableList<MutableList<String>>){
        socketChannel.connect(serverAddress)

        val buffer = writeHeaderBuffer()
        buffer.flip()
        socketChannel.write(buffer)

        val totalBodySize = getBodyList.sumOf { it[1].toInt() }
        val receiveBuffer = ByteBuffer.allocate(OQCInfo.HEADER_DEFAULT_SIZE + totalBodySize)
        val bytesRead = socketChannel.read(receiveBuffer)

        if (bytesRead != -1) {
            readHeader(receiveBuffer)
            readBody(receiveBuffer, getBodyList)
        }

        socketChannel.close()
    }

    suspend fun getDataWithSendAndGetBodyList(sendBodyList : MutableList<MutableList<String>>, getBodyList : MutableList<MutableList<String>>){
        socketChannel.connect(serverAddress)

        val buffer = writeHeaderBuffer()

        for(sendList in sendBodyList){
            if(sendList[2] == OQCInfo.TYPE_CHAR){
                val paddedString = sendList[3].padEnd(sendList[1].toInt(), ' ')
                buffer.put(paddedString.toByteArray())
            }else if(sendList[2] == OQCInfo.TYPE_INT){
                buffer.putInt(sendList[3].toInt())
            }
        }

        buffer.flip()
        socketChannel.write(buffer)

        val totalBodySize = getBodyList.sumOf { it[1].toInt() }
        val receiveBuffer = ByteBuffer.allocate(OQCInfo.HEADER_DEFAULT_SIZE + totalBodySize)
        val bytesRead = socketChannel.read(receiveBuffer)

        if (bytesRead != -1) {
            readHeader(receiveBuffer)
            readBody(receiveBuffer, getBodyList)
        }

        socketChannel.close()
    }

    suspend fun getAPInfo(){
        val socket = Socket()
        socket.connect(InetSocketAddress(ipAddress, portNumber))

        val outputStream = DataOutputStream(socket.getOutputStream())
        outputStream.write(OQCInfo.HEADER_NAME_VALUE.toByteArray())
        outputStream.write(OQCInfo.HEADER_VERSION_VALUE.toByteArray())
        outputStream.writeInt(code)
        outputStream.writeInt(bodySize)
        outputStream.flush()

        val inputStream = DataInputStream(socket.getInputStream())

        val name = ByteArray(OQCInfo.HEADER_NAME_SIZE)
        inputStream.readFully(name)

        val version = ByteArray(OQCInfo.HEADER_VERSION_SIZE)
        inputStream.readFully(version)

        val code = inputStream.readInt()
        val bodySize = inputStream.readInt()

        val count = inputStream.readInt()
        writeText("\n${OQCInfo.HEADER_NAME} : ${String(name)}," +
                " ${OQCInfo.HEADER_VERSION} : ${String(version)}," +
                " ${OQCInfo.HEADER_CODE} : $code," +
                " ${OQCInfo.HEADER_BODY_SIZE} : $bodySize," +
                " $count")

        for (i in 0 until count) {

            val ssid = ByteArray(64)
            inputStream.readFully(ssid)

            val channel = inputStream.readUnsignedByte()
            val encryption = inputStream.readUnsignedByte()
            val rssi: Short = inputStream.readShort()

            writeText("${byteArrayToString(ssid)}, $channel, $encryption, $rssi")
        }

        outputStream.close()
        inputStream.close()
        socket.close()
    }

    private fun writeHeaderBuffer(): ByteBuffer {
        val buffer = ByteBuffer.allocate(OQCInfo.HEADER_DEFAULT_SIZE + bodySize)
        buffer.put(OQCInfo.HEADER_NAME_VALUE.toByteArray())
        buffer.put(OQCInfo.HEADER_VERSION_VALUE.toByteArray())
        buffer.putInt(code)
        buffer.putInt(bodySize)

        return buffer
    }

    private suspend fun readHeader(receiveBuffer: ByteBuffer){
        receiveBuffer.flip()

        val name = ByteArray(OQCInfo.HEADER_NAME_SIZE)
        receiveBuffer.get(name)

        val version = ByteArray(OQCInfo.HEADER_VERSION_SIZE)
        receiveBuffer.get(version)

        val code = receiveBuffer.int
        val bodySize = receiveBuffer.int

        writeText("\n${OQCInfo.HEADER_NAME} : ${String(name)}," +
                " ${OQCInfo.HEADER_VERSION} : ${String(version)}," +
                " ${OQCInfo.HEADER_CODE} : $code," +
                " ${OQCInfo.HEADER_BODY_SIZE} : $bodySize")
    }

    private suspend fun readBody(receiveBuffer: ByteBuffer, getBodyList : MutableList<MutableList<String>>){
        for(getList in getBodyList){
            if(getList[2] == OQCInfo.TYPE_CHAR){
                val byteArray = ByteArray(getList[1].toInt())
                receiveBuffer.get(byteArray)

                writeText("${getList[0]} : ${byteArrayToString(byteArray)}")
            }else if(getList[2] == OQCInfo.TYPE_INT){
                val value = receiveBuffer.int

                writeText("${getList[0]} : $value")
            }
        }
    }

    private fun byteArrayToString(byteArray: ByteArray): String {
        return String(byteArray, Charsets.UTF_8).trimEnd { it == '\u0000' }
    }

    private suspend fun writeText(text:String) = withContext(Dispatchers.Main){
        _liveResult.postValue(text + "\n")
    }
}

