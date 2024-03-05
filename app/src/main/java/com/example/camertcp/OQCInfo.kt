package com.example.camertcp

object OQCInfo {
    const val IP_ADDRESS = "192.168.1.1"
    const val PORT_NUMBER = 30300

    const val HEADER_DEFAULT_SIZE = 20
    const val HEADER_NAME_SIZE = 8
    const val HEADER_VERSION_SIZE = 4

    const val HEADER_NAME = "Name"
    const val HEADER_VERSION = "Version"
    const val HEADER_CODE = "Code"
    const val HEADER_BODY_SIZE = "BodySize"

    const val HEADER_NAME_VALUE = "CCTVCODE"
    const val HEADER_VERSION_VALUE = "0010"

    const val SERIAL_NUMBER = "1008160202559"

    const val TYPE_CHAR = "Char"
    const val TYPE_INT = "Unsigned int"

    const val FIELD_NAME_SN = "SN"
    const val FIELD_NAME_MSG = "Msg"
    const val FIELD_NAME_RESULT = "Result"
    const val FIELD_NAME_MAC = "Mac"
    const val FIELD_NAME_MODEL = "Model"
    const val FIELD_NAME_FW_VERSION = "FW Version"
    const val FIELD_NAME_HW_VERSION = "HW Version"

    const val CODE_REQUEST_AP_INFO = 104
    const val CODE_CHANGE_SN = 1003
    const val CODE_GET_SN = 1004
    const val CODE_GET_CAMERA_INFO = 1005
    const val CODE_REQUEST_ALS = 1101
    const val CODE_REQUEST_BELL = 1102

    const val BODY_SIZE_DEFAULT = 0
}