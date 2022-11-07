package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.SleepingTime
import ie.setu.domain.User
import ie.setu.domain.db.Activities
import ie.setu.domain.db.SleepingTimes
import ie.setu.domain.db.SleepingTimes.autoIncrement
import ie.setu.domain.db.SleepingTimes.primaryKey
import ie.setu.domain.db.SleepingTimes.references
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email]
)

fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)


fun mapToSleepingTime(it: ResultRow) = SleepingTime(
    id = it[SleepingTimes.id],
    started = it[SleepingTimes.started],
    finished = it[SleepingTimes.finished],
    duration = it[SleepingTimes.duration],
    deepSleepingTime = it[SleepingTimes.deepSleepingTime],
    userId = it[SleepingTimes.userId],
)