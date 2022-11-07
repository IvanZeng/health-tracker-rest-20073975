package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object SleepingTimes : Table("sleepingTimes") {
    val id = integer("id").autoIncrement().primaryKey()
    val started = datetime("started")
    val finished = datetime("finished")
    val duration = double("duration")
    val deepSleepingTime = integer("deepSleepingTime")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}
