package com.example.camertcp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class MainViewModel : ViewModel(){
    private val _liveResult = MutableLiveData<String>()
    val liveResult : LiveData<String>
        get() = _liveResult

    private val _liveToast = MutableLiveData<String>()
    val liveToast : LiveData<String>
        get() = _liveToast

    private lateinit var oqc : OQC

    fun connect() = viewModelScope.launch(Dispatchers.IO){
        if(::oqc.isInitialized && oqc.isSocketConnected()) {
            showToast(OQCInfo.MESSAGE_ALREADY_CONNECT)
            return@launch
        }

        oqc = OQC(OQCInfo.IP_ADDRESS, OQCInfo.PORT_NUMBER,)
        val response = oqc.connect()
        writeText(response)
    }

    fun requestAPInfo() = viewModelScope.launch(Dispatchers.IO){
        if(::oqc.isInitialized && oqc.isSocketConnected()) {
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
            return@launch
        }

        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_COUNT, "4", OQCInfo.TYPE_INT))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_AP_SSID, "64", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_CHANNEL_NUMBER, "1", OQCInfo.TYPE_UINT8))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_ENCRYPTION_METHOD, "1", OQCInfo.TYPE_UINT8))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RSSI, "2", OQCInfo.TYPE_INT16))

        val response = oqc.getData(OQCInfo.CODE_REQUEST_AP_INFO,
            OQCInfo.BODY_SIZE_DEFAULT, mutableListOf())
        readBody(response, getBodyList)
    }

    fun changeSN(serialNumber: String) = viewModelScope.launch(Dispatchers.IO){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }

        val sendBodyList : MutableList<MutableList<String>> = mutableListOf()
        sendBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_SN, "40", OQCInfo.TYPE_CHAR, serialNumber))

        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MSG, "128", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RESULT, "4", OQCInfo.TYPE_INT))

        val bodySize = sendBodyList.sumOf { it[1].toInt() }

        val response = oqc.getData(OQCInfo.CODE_CHANGE_SN, bodySize, sendBodyList)
        readBody(response, getBodyList)
    }

    fun getSN() = viewModelScope.launch(Dispatchers.IO){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }

        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_SN, "64", OQCInfo.TYPE_CHAR))

        val response = oqc.getData(OQCInfo.CODE_GET_SN, OQCInfo.BODY_SIZE_DEFAULT, mutableListOf())
        readBody(response, getBodyList)
    }

    fun getCameraInfo() = viewModelScope.launch(Dispatchers.IO){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }

        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MAC, "40", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_SN, "40", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MODEL, "20", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_FW_VERSION, "16", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_HW_VERSION, "16", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RESULT, "4", OQCInfo.TYPE_INT))

        val response = oqc.getData(OQCInfo.CODE_GET_CAMERA_INFO,
            OQCInfo.BODY_SIZE_DEFAULT, mutableListOf())
        readBody(response, getBodyList)
    }

    fun requestALS() = viewModelScope.launch(Dispatchers.IO){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }

        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MSG, "128", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RESULT, "4", OQCInfo.TYPE_INT))

        val response = oqc.getData(OQCInfo.CODE_REQUEST_ALS,
            OQCInfo.BODY_SIZE_DEFAULT, mutableListOf())
        readBody(response, getBodyList)
    }

    fun requestBell() = viewModelScope.launch(Dispatchers.IO){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }

        val response = oqc.getData(OQCInfo.CODE_REQUEST_BELL, OQCInfo.BODY_SIZE_DEFAULT, mutableListOf())
        readBody(response, mutableListOf())
    }

    fun disConnect() = viewModelScope.launch(Dispatchers.IO){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }

        val response = oqc.disconnect()
        writeText(response)
    }

    private suspend fun readBody(receiveBuffer: ByteBuffer, getBodyList : MutableList<MutableList<String>>){
        receiveBuffer.flip()

        val totalExpectedSize = getBodyList.sumOf { it[1].toInt() }

        if (totalExpectedSize != receiveBuffer.remaining()) {
            val count = receiveBuffer.int

            repeat(count) {
                for (getBody in getBodyList.drop(1)) {
                    writeText("${getBody[0]} : ${processingData(receiveBuffer, getBody[2], getBody[1].toInt())}")
                }
            }
        } else {
            for (getBody in getBodyList) {
                writeText("${getBody[0]} : ${processingData(receiveBuffer, getBody[2], getBody[1].toInt())}")
            }
        }
    }

    private fun processingData(receiveBuffer: ByteBuffer, bodyType: String, bodySize: Int): Any {
        return when (bodyType) {
            OQCInfo.TYPE_CHAR -> {
                val byteArray = ByteArray(bodySize)
                receiveBuffer.get(byteArray)
                String(byteArray, Charsets.UTF_8).trimEnd { it == '\u0000' }
            }
            OQCInfo.TYPE_INT -> receiveBuffer.int
            OQCInfo.TYPE_UINT8 -> receiveBuffer.get().toUByte()
            OQCInfo.TYPE_INT16 -> receiveBuffer.short
            else -> receiveBuffer.get()
        }
    }

    private suspend fun requestConnect(){
        if(!::oqc.isInitialized || !oqc.isSocketConnected()){
            showToast(OQCInfo.MESSAGE_REQUEST_CONNECT)
        }
    }

    private suspend fun writeText(text: String) = withContext(Dispatchers.Main){
        _liveResult.value = text + "\n"
    }

    private suspend fun showToast(text: String) = withContext(Dispatchers.Main){
        _liveToast.value = text
    }
}