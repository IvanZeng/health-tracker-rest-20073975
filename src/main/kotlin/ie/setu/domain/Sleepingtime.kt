package ie.setu.domain

import org.joda.time.DateTime

data class Sleepingtime (var id: Int,
                         var startedAt: DateTime,
                         var deepSleepingTime: Int,
                         var userId: Int)