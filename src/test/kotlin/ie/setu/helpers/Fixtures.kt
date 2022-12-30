package ie.setu.helpers

import Calories
import ie.setu.domain.*
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Bmis
import ie.setu.domain.db.Sleepingtimes
import ie.setu.domain.db.Users
import ie.setu.domain.repository.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime
import ie.setu.domain.Activity
import ie.setu.domain.User

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"
val updatedName = "Updated Name"
val updatedEmail = "Updated Email"

val updatedDescription = "Updated Description"
val updatedDuration = 30.0
val updatedCalories = 945
val updatedStarted = DateTime.parse("2020-06-11T05:59:27.258Z")

val updatedGender = "Male"
val updatedHeight = 183
val updatedWeight = 62.6
val updatedBmidata = "Normal"

//val updatedStarted = DateTime.parse("2020-06-11T05:59:27.258Z")
//val deepSleepingTime = 3

val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4)
)

val activities = arrayListOf<Activity>(
    Activity(id = 1, description = "Running", duration = 22.0, calories = 230, started = DateTime.now(), userId = 1),
    Activity(id = 2, description = "Hopping", duration = 10.5, calories = 80, started = DateTime.now(), userId = 1),
    Activity(id = 3, description = "Walking", duration = 12.0, calories = 120, started = DateTime.now(), userId = 2)
)

val sleepingtimes = arrayListOf<Sleepingtime>(
    Sleepingtime(id = 1, startedAt = DateTime.now(), deepSleepingTime = 4, userId = 1),
    Sleepingtime(id = 2, startedAt = DateTime.now(), deepSleepingTime = 5, userId = 1),
    Sleepingtime(id = 3, startedAt = DateTime.now(), deepSleepingTime = 3, userId = 2)
)

val bmis = arrayListOf<Bmi>(
    Bmi(id = 1, gender = "Male" ,height = 175, weight = 90.4, bmidata = "Overweight", userId = 1),
    Bmi(id = 2, gender = "Male" ,height = 180, weight = 77.2, bmidata = "Normal", userId = 1),
    Bmi(id = 3, gender = "Female" ,height = 169, weight = 44.4, bmidata = "Underweight", userId = 2)
)

val calories = arrayListOf<Calorie>(
    Calorie(id = 1, dateTime = DateTime.now(), calorieGet = 4000, state = "Over", userId = 1),
    Calorie(id = 2, dateTime = DateTime.now(), calorieGet = 2100, state = "Normal", userId = 1),
    Calorie(id = 3, dateTime = DateTime.now(), calorieGet = 4920, state = "Over", userId = 2)
)

fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(users[0])
    userDAO.save(users[1])
    userDAO.save(users[2])
    return userDAO
}
fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities[0])
    activityDAO.save(activities[1])
    activityDAO.save(activities[2])
    return activityDAO
}

fun populateSleepingTimeTable(): SleepingTimeDAO {
    SchemaUtils.create(Sleepingtimes)
    val sleepingTimeDAO = SleepingTimeDAO()
    sleepingTimeDAO.save(sleepingtimes[0])
    sleepingTimeDAO.save(sleepingtimes[1])
    sleepingTimeDAO.save(sleepingtimes[2])
    return sleepingTimeDAO
}

fun populateBmiTable(): BmiDAO {
    SchemaUtils.create(Bmis)
    val bmiDAO = BmiDAO()
    bmiDAO.save(bmis[0])
    bmiDAO.save(bmis[1])
    bmiDAO.save(bmis[2])
    return bmiDAO
}

fun populateCalorieTable(): CalorieDAO {
    SchemaUtils.create(Calories)
    val calorieDAO = CalorieDAO()
    calorieDAO.save(calories[0])
    calorieDAO.save(calories[1])
    calorieDAO.save(calories[2])
    return calorieDAO
}