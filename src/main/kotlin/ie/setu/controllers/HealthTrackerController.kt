package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.*
import ie.setu.domain.repository.*

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import jsonToObject

object HealthTrackerController {

    val userDao = UserDAO()
    private val activityDAO = ActivityDAO()
    private val sleepingTimeDAO = SleepingTimeDAO()
    private val bmiDAO = BmiDAO()
    private val calorieDAO = CalorieDAO()

    @OpenApi(
        summary = "Get all users",
        operationId = "getAllUsers",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )

    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }


    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )

    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add User",
        operationId = "addUser",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses = [OpenApiResponse("200")]
    )
    fun addUser(ctx: Context) {
        val user : User = jsonToObject(ctx.body())
        val userId = userDao.save(user)
        if (userId != null) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
    }

    @OpenApi(
        summary = "Get user by Email",
        operationId = "getUserByEmail",
        tags = ["User"],
        path = "/api/users/email/{email}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("email", Int::class, "The user email")],
        responses = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses = [OpenApiResponse("204")]
    )
    fun deleteUser(ctx: Context){
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses = [OpenApiResponse("204")]
    )
    fun updateUser(ctx: Context){
        val foundUser : User = jsonToObject(ctx.body())
        if ((userDao.update(id = ctx.pathParam("user-id").toInt(), user=foundUser)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    //--------------------------------------------------------------
    // ActivityDAOI specifics
    //-------------------------------------------------------------

    fun getAllActivities(ctx: Context) {
        val activities = activityDAO.getAll()
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                ctx.json(activities)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }

    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            ctx.json(activity)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun addActivity(ctx: Context) {
        val activity : Activity = jsonToObject(ctx.body())
        val userId = userDao.findById(activity.userId)
        if (userId != null) {
            val activityId = activityDAO.save(activity)
            activity.id = activityId
            ctx.json(activity)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    fun deleteActivityByActivityId(ctx: Context){
        if (activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun deleteActivityByUserId(ctx: Context){
        if (activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateActivity(ctx: Context){
        val activity : Activity = jsonToObject(ctx.body())
        if (activityDAO.updateByActivityId(
                activityId = ctx.pathParam("activity-id").toInt(),
                activityToUpdate =activity) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    //--------------------------------------------------------------
// SleepingTimeDAO specifics
//-------------------------------------------------------------
//
    @OpenApi(
        summary = "Get all sleepTimes",
        operationId = "getAllSleepingTimes",
        tags = ["SleepingTime"],
        path = "/api/sleeping_times",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<SleepingTime>::class)])]
    )

    fun getAllSleepingTimes(ctx: Context) {
        val sleepingTimes = sleepingTimeDAO.getAll()
        if (sleepingTimes.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(sleepingTimes)
    }

@OpenApi(
    summary = "Get sleepingTime by ID",
    operationId = "getSleepingTimeById",
    tags = ["SleepingTime"],
    path = "/api/sleeping_times/{sleepingtime-id}",
    method = HttpMethod.GET,
    pathParams = [OpenApiParam("sleepingtime-id", Int::class, "The sleeping time ID")],
    responses  = [OpenApiResponse("200", [OpenApiContent(SleepingTime::class)])]
)

    fun getSleepingTimesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val sleepingTimes = sleepingTimeDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (sleepingTimes.isNotEmpty()) {
                ctx.json(sleepingTimes)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
    }
    @OpenApi(
        summary = "Add SleepingTime",
        operationId = "addSleepingTime",
        tags = ["SleepingTime"],
        path = "/api/sleeping_times",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("sleepingtime-id", Int::class, "The sleeping time ID")],
        responses = [OpenApiResponse("200")]
    )

    fun addSleepingTime(ctx: Context) {
        val sleepingTime : SleepingTime = jsonToObject(ctx.body())
        val userId = userDao.findById(sleepingTime.userId)
        if (userId != null) {
            val sleepingTimeId = sleepingTimeDAO.save(sleepingTime)
            sleepingTime.id = sleepingTimeId
            ctx.json(sleepingTime)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Get sleepingTime by ID",
        operationId = "getsleepingTimeById",
        tags = ["SleepingTime"],
        path = "/api/sleeping_times/{sleepingtime-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("sleepingtime-id", Int::class, "The sleeping time ID")],
        responses = [OpenApiResponse("200", [OpenApiContent(SleepingTime::class)])]
    )

    fun getSleepingTimesBySleepingTimeId(ctx: Context) {
        val sleepingTime = sleepingTimeDAO.findBySleepingTimeId((ctx.pathParam("sleepingtime-id").toInt()))
        if (sleepingTime != null){
            ctx.json(sleepingTime)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

fun deleteSleepingTimeBySleepingTimeId(ctx: Context){
    if (sleepingTimeDAO.deleteBySleepingTimeId(ctx.pathParam("sleepingtime-id").toInt()) != 0)
        ctx.status(204)
    else
        ctx.status(404)
}

fun deleteSleepingTimeByUserId(ctx: Context){
    if (sleepingTimeDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
        ctx.status(204)
    else
        ctx.status(404)
}

fun updateSleepingTime(ctx: Context){
    val sleepingTime : SleepingTime = jsonToObject(ctx.body())
    if (sleepingTimeDAO.updateBySleepingTimeId(
            sleepingTimeId = ctx.pathParam("sleepingtime-id").toInt(),
            sleepingTimeToUpdate =sleepingTime) != 0)
        ctx.status(204)
    else
        ctx.status(404)
}


    //--------------------------------------------------------------
// BmiDAO specifics
//-------------------------------------------------------------
//
    @OpenApi(
        summary = "Get all bmis",
        operationId = "getAllBmis",
        tags = ["Bmi"],
        path = "/api/bmis",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Bmi>::class)])]
    )

    fun getAllBmis(ctx: Context) {
        val bmis = bmiDAO.getAll()
        if (bmis.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(bmis)
    }

    @OpenApi(
        summary = "Get bmi by ID",
        operationId = "getBmiById",
        tags = ["Bmi"],
        path = "/api/bmis/{bmi-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("bmi-id", Int::class, "The bmi ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Bmi::class)])]
    )

    fun getBmisByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val bmis = bmiDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (bmis.isNotEmpty()) {
                ctx.json(bmis)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Get bmi by ID",
        operationId = "getbmiById",
        tags = ["Bmi"],
        path = "/api/bmis/{bmi-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("bmi-id", Int::class, "The bmi ID")],
        responses = [OpenApiResponse("200", [OpenApiContent(Bmi::class)])]
    )
    fun getBmisByBmiId(ctx: Context) {
        val bmi = bmiDAO.findByBmiId((ctx.pathParam("bmi-id").toInt()))
        if (bmi != null){
            ctx.json(bmi)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Bmi",
        operationId = "addBmi",
        tags = ["Bmi"],
        path = "/api/bmis",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("bmi-id", Int::class, "The bmi ID")],
        responses = [OpenApiResponse("200")]
    )

    fun addBmi(ctx: Context) {
        val bmi : Bmi = jsonToObject(ctx.body())
        val userId = userDao.findById(bmi.userId)
        if (userId != null) {
            val bmiId = bmiDAO.save(bmi)
            bmi.id = bmiId
            ctx.json(bmi)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    fun deleteBmiByBmiId(ctx: Context){
        if (bmiDAO.deleteByBmiId(ctx.pathParam("bmi-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun deleteBmiByUserId(ctx: Context){
        if (bmiDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateBmi(ctx: Context){
        val bmi : Bmi = jsonToObject(ctx.body())
        if (bmiDAO.updateByBmiId(
                bmiId = ctx.pathParam("bmi-id").toInt(),
                bmiToUpdate =bmi) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    //--------------------------------------------------------------
// CalorieDAO specifics
//-------------------------------------------------------------
//
    @OpenApi(
        summary = "Get all calories",
        operationId = "getAllCalories",
        tags = ["Calorie"],
        path = "/api/calories",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Calorie>::class)])]
    )
    fun getAllCalories(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString(calorieDAO.getAll()))
    }

    @OpenApi(
        summary = "Get calorie by ID",
        operationId = "getCalorieById",
        tags = ["Calorie"],
        path = "/api/calories/{calorie-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("calorie-id", Int::class, "The calorie ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Calorie::class)])]
    )
    fun getCaloriesByUserId(ctx: Context) {
        if (HealthTrackerController.userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val Calories = HealthTrackerController.calorieDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (Calories.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(Calories))
            }
        }
    }
    @OpenApi(
        summary = "Add Calorie",
        operationId = "addCalorie",
        tags = ["Calorie"],
        path = "/api/calories",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("calorie-id", Int::class, "The calorie ID")],
        responses = [OpenApiResponse("200")]
    )
    fun addCalorie(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val calorie = mapper.readValue<Calorie>(ctx.body())
        HealthTrackerController.calorieDAO.save(calorie)
        ctx.json(calorie)
    }


    @OpenApi(
        summary = "Get calorie by ID",
        operationId = "getcalorieById",
        tags = ["Calorie"],
        path = "/api/calories/{calorie-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("calorie-id", Int::class, "The calorie ID")],
        responses = [OpenApiResponse("200", [OpenApiContent(Calorie::class)])]
    )
    fun getCaloriesByCalorieId(ctx: Context) {
        val calorie = HealthTrackerController.calorieDAO.findByCalorieId((ctx.pathParam("calorie-id").toInt()))
        if (calorie != null){
            val mapper = jacksonObjectMapper()
                .registerModule(JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            ctx.json(mapper.writeValueAsString(calorie))
        }
    }

    fun deleteCalorieByCalorieId(ctx: Context){
        HealthTrackerController.calorieDAO.deleteByCalorieId(ctx.pathParam("calorie-id").toInt())
    }

    fun deleteCalorieByUserId(ctx: Context){
        HealthTrackerController.calorieDAO.deleteByUserId(ctx.pathParam("user-id").toInt())
    }

    fun updateCalorie(ctx: Context){
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val calorie = mapper.readValue<Calorie>(ctx.body())
        HealthTrackerController.calorieDAO.updateByCalorieId(
            calorieId = ctx.pathParam("sleepingtime-id").toInt(),
            calorieDTO=calorie)
    }
}

