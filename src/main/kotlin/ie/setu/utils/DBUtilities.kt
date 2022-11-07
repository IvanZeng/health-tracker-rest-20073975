package ie.setu.utils

import ie.setu.domain.*
import ie.setu.domain.db.*
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

fun mapToBMI(it: ResultRow) = Bmi(
    id = it[Bmis.id],
    gender =it[Bmis.gender],
    height = it[Bmis.height],
    weight = it[Bmis.weight],
    bmiData = it[Bmis.bmiData],
    userId = it[Bmis.userId],
)

fun mapToCalorie(it: ResultRow) = Calorie(
    id = it[Calories.id],
    dateTime =it[Calories.dateTime],
    calorieGet = it[Calories.calorieGet],
    state = it[Calories.state],
    userId = it[Calories.userId],
)