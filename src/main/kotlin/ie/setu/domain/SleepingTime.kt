package ie.setu.domain

import org.joda.time.DateTime

data class SleepingTime (var id: Int,
                         var startedAt: DateTime,
                         var deepSleepingTime: Int,
                         var userId: Int)