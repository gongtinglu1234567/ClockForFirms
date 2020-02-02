package com.example.db

data class DeviceTable(val map: MutableMap<String, Any?>) {
    var deviceId: Int by map
    var remainingTime: Int by map
    var status: Boolean by map

    constructor() : this(HashMap())

    constructor(deviceId: Int, remainingTime: Int, status: Boolean) : this(HashMap()) {
        this.deviceId = deviceId
        this.remainingTime = remainingTime
        this.status = status
    }
}