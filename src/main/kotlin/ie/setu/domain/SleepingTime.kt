package ie.setu.domain

import org.joda.time.DateTime

data class SleepingTime (var id: Int,
                         var started: DateTime,
                         var finished: DateTime,
                         var duration: Double,
                         var deepSleepingTime: Int,
                         var userId: Int)