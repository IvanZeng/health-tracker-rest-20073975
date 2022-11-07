package ie.setu.helpers

import ie.setu.domain.Activity
import ie.setu.domain.Bmi
import ie.setu.domain.SleepingTime
import ie.setu.domain.User
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Bmis
import ie.setu.domain.db.SleepingTimes
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.BmiDAO
import ie.setu.domain.repository.SleepingTimeDAO
import ie.setu.domain.repository.UserDAO
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

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

val sleepingTimes = arrayListOf<SleepingTime>(
    SleepingTime(id = 1, started = DateTime.now(), finished = DateTime.now(), duration = 8.2, deepSleepingTime = 4, userId = 1),
    SleepingTime(id = 2, started = DateTime.now(), finished = DateTime.now(), duration = 6.3, deepSleepingTime = 5, userId = 1),
    SleepingTime(id = 3, started = DateTime.now(), finished = DateTime.now(), duration = 9.6, deepSleepingTime = 3, userId = 2)
)

val bmis = arrayListOf<Bmi>(
    Bmi(id = 1, gender = "Male" ,height = 175, weight = 90.4, bmiData = "Overweight", userId = 1),
    Bmi(id = 2, gender = "Male" ,height = 180, weight = 77.2, bmiData = "Normal", userId = 1),
    Bmi(id = 3, gender = "Female" ,height = 169, weight = 44.4, bmiData = "Underweight", userId = 2)
)

fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(users.get(0))
    userDAO.save(users.get(1))
    userDAO.save(users.get(2))
    return userDAO
}
fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities.get(0))
    activityDAO.save(activities.get(1))
    activityDAO.save(activities.get(2))
    return activityDAO
}

fun populateSleepingTimeTable(): SleepingTimeDAO {
    SchemaUtils.create(SleepingTimes)
    val sleepingTimeDAO = SleepingTimeDAO()
    sleepingTimeDAO.save(sleepingTimes.get(0))
    sleepingTimeDAO.save(sleepingTimes.get(1))
    sleepingTimeDAO.save(sleepingTimes.get(2))
    return sleepingTimeDAO
}

fun populateBmiTable(): BmiDAO {
    SchemaUtils.create(Bmis)
    val bmiDAO = BmiDAO()
    bmiDAO.save(bmis.get(0))
    bmiDAO.save(bmis.get(1))
    bmiDAO.save(bmis.get(2))
    return bmiDAO
}