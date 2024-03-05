package com.example.camertcp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){

    private val _liveResult = MutableLiveData<String>()
    val liveResult : LiveData<String>
        get() = _liveResult

    fun requestAPInfo() = viewModelScope.launch(Dispatchers.IO){
        OQC(
            OQCInfo.IP_ADDRESS,
            OQCInfo.PORT_NUMBER,
            OQCInfo.CODE_REQUEST_AP_INFO,
            OQCInfo.BODY_SIZE_DEFAULT,
            _liveResult
        ).getAPInfo()
    }

    fun changeSN(serialNumber: String) = viewModelScope.launch(Dispatchers.IO){
        val sendBodyList : MutableList<MutableList<String>> = mutableListOf()
        sendBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_SN, "40", OQCInfo.TYPE_CHAR, serialNumber))

        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MSG, "128", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RESULT, "4", OQCInfo.TYPE_INT))

        val bodySize = sendBodyList.sumOf { it[1].toInt() }

        OQC(OQCInfo.IP_ADDRESS, OQCInfo.PORT_NUMBER, OQCInfo.CODE_CHANGE_SN, bodySize, _liveResult)
            .getDataWithSendAndGetBodyList(sendBodyList, getBodyList)
    }

    fun getSN() = viewModelScope.launch(Dispatchers.IO){
        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_SN, "40", OQCInfo.TYPE_CHAR))

        OQC(
            OQCInfo.IP_ADDRESS,
            OQCInfo.PORT_NUMBER,
            OQCInfo.CODE_GET_SN,
            OQCInfo.BODY_SIZE_DEFAULT,
            _liveResult
        ).getDataWithGetBodyList(getBodyList)
    }

    fun getCameraInfo() = viewModelScope.launch(Dispatchers.IO){
        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MAC, "40", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_SN, "40", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MODEL, "20", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_FW_VERSION, "16", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_HW_VERSION, "16", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RESULT, "4", OQCInfo.TYPE_INT))

        OQC(
            OQCInfo.IP_ADDRESS,
            OQCInfo.PORT_NUMBER,
            OQCInfo.CODE_GET_CAMERA_INFO,
            OQCInfo.BODY_SIZE_DEFAULT,
            _liveResult
        ).getDataWithGetBodyList(getBodyList)
    }

    fun requestALS() = viewModelScope.launch(Dispatchers.IO){
        val getBodyList : MutableList<MutableList<String>> = mutableListOf()
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_MSG, "128", OQCInfo.TYPE_CHAR))
        getBodyList.add(mutableListOf(OQCInfo.FIELD_NAME_RESULT, "4", OQCInfo.TYPE_INT))

        OQC(
            OQCInfo.IP_ADDRESS,
            OQCInfo.PORT_NUMBER,
            OQCInfo.CODE_REQUEST_ALS,
            OQCInfo.BODY_SIZE_DEFAULT,
            _liveResult
        ).getDataWithGetBodyList(getBodyList)
    }

    fun requestBell() = viewModelScope.launch(Dispatchers.IO){
        OQC(
            OQCInfo.IP_ADDRESS,
            OQCInfo.PORT_NUMBER,
            OQCInfo.CODE_REQUEST_BELL,
            OQCInfo.BODY_SIZE_DEFAULT,
            _liveResult
        ).getData()
    }

}