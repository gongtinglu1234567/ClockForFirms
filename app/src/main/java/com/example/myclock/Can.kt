package com.example.myclock

class Can {
    var id: Int
    var remainingTime: Int
    var isRunning: Boolean

    companion object {
        private const val ORIGIN_START_TIME = 10 * 60
    }

    constructor(id: Int, timePeriod: Int, isRunning: Boolean) {
        this.id = id
        this.remainingTime = timePeriod
        this.isRunning = isRunning
    }

    constructor(id: Int) {
        this.id = id
        this.remainingTime = ORIGIN_START_TIME
        this.isRunning = false
    }
}