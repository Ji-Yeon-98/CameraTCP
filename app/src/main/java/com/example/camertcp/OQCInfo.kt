package com.example.camertcp

object OQCInfo {
    const val IP_ADDRESS = "192.168.1.1"
    const val PORT_NUMBER = 30300

    const val HEADER_DEFAULT_SIZE = 20
    const val HEADER_NAME_SIZE = 8
    const val HEADER_VERSION_SIZE = 4

    const val EXCEPTION = "Exception"
    const val CONNECT_EXCEPTION = "ConnectException"
    const val UNKNOWN_HOST_EXCEPTION = "UnknownHostException"
    const val SOCKET_EXCEPTION = "SocketException"

    const val HEADER_NAME_VALUE = "CCTVCODE"
    const val HEADER_VERSION_VALUE = "0010"

    const val SERIAL_NUMBER = "1008160202559"

    const val TYPE_CHAR = "Char"
    const val TYPE_INT = "Unsigned int"
    const val TYPE_UINT8 = "UINT8"
    const val TYPE_INT16 = "INT16"

    const val FIELD_NAME_SN = "SN"
    const val FIELD_NAME_MSG = "Msg"
    const val FIELD_NAME_RESULT = "Result"
    const val FIELD_NAME_MAC = "Mac"
    const val FIELD_NAME_MODEL = "Model"
    const val FIELD_NAME_FW_VERSION = "FW Version"
    const val FIELD_NAME_HW_VERSION = "HW Version"
    const val FIELD_NAME_COUNT = "Count"
    const val FIELD_NAME_AP_SSID = "AP SSID"
    const val FIELD_NAME_CHANNEL_NUMBER = "Channel Number"
    const val FIELD_NAME_ENCRYPTION_METHOD = "암호화 방식"
    const val FIELD_NAME_RSSI = "RSSI"

    const val CODE_REQUEST_AP_INFO = 104
    const val CODE_CHANGE_SN = 1003
    const val CODE_GET_SN = 1004
    const val CODE_GET_CAMERA_INFO = 1005
    const val CODE_REQUEST_ALS = 1101
    const val CODE_REQUEST_BELL = 1102

    const val BODY_SIZE_DEFAULT = 0

    const val MESSAGE_SUCCESS = "Success"
    const val MESSAGE_FAIL = "Fail"
    const val MESSAGE_REQUEST_CONNECT = "Please Connect"
    const val MESSAGE_ALREADY_CONNECT = "Already Connect"
}