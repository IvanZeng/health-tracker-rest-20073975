package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Sleepingtimes : Table("sleepingtimes") {
    val id = integer("id").autoIncrement().primaryKey()
    val startedAt = datetime("started_at")
    val deepSleepingTime = integer("deep_sleeping_time")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}
