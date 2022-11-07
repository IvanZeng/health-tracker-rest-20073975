package ie.setu.config


import ie.setu.controllers.HealthTrackerController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.swagger.v3.oas.models.info.Info
class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create {
            it.registerPlugin(getConfiguredOpenApiPlugin())
            it.defaultContentType = "application/json"
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getHerokuAssignedPort())

        registerRoutes(app)
        return app
    }

    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/api/users") {
                get(HealthTrackerController::getAllUsers)
                post(HealthTrackerController::addUser)
                path("{user-id}") {
                    get(HealthTrackerController::getUserByUserId)
                    delete(HealthTrackerController::deleteUser)
                    patch(HealthTrackerController::updateUser)
                    path("activities") {
                        get(HealthTrackerController::getActivitiesByUserId)
                        delete(HealthTrackerController::deleteActivityByUserId)
                    }
                    path("sleepingTimes") {
                        get(HealthTrackerController::getSleepingTimesByUserId)
                        delete(HealthTrackerController::deleteSleepingTimeByUserId)
                    }
                    path("bmis") {
                        get(HealthTrackerController::getBmisByUserId)
                        delete(HealthTrackerController::deleteBmiByUserId)
                    }
                    path("calories") {
                        get(HealthTrackerController::getCaloriesByUserId)
                        delete(HealthTrackerController::deleteCalorieByUserId)
                    }
                }
                path("/email/{email}") {
                    get(HealthTrackerController::getUserByEmail)
                }
            }
            path("/api/activities") {
                get(HealthTrackerController::getAllActivities)
                post(HealthTrackerController::addActivity)
                path("{activity-id}") {
                    get(HealthTrackerController::getActivitiesByActivityId)
                    delete(HealthTrackerController::deleteActivityByActivityId)
                    patch(HealthTrackerController::updateActivity)
                }
            }
            path("/api/sleepingTimes") {
                get(HealthTrackerController::getAllSleepingTimes)
                post(HealthTrackerController::addSleepingTime)
                path("{sleepingTime-id}") {
                    get(HealthTrackerController::getSleepingTimesBySleepingTimeId)
                    delete(HealthTrackerController::deleteSleepingTimeBySleepingTimeId)
                    patch(HealthTrackerController::updateSleepingTime)
                }
            }
            path("/api/bmis") {
                get(HealthTrackerController::getAllBmis)
                post(HealthTrackerController::addBmi)
                path("{bmi-id}") {
                    get(HealthTrackerController::getBmisByBmiId)
                    delete(HealthTrackerController::deleteBmiByBmiId)
                    patch(HealthTrackerController::updateBmi)
                }
            }
            path("/api/calories") {
                get(HealthTrackerController::getAllCalories)
                post(HealthTrackerController::addCalorie)
                path("{calorie-id}") {
                    get(HealthTrackerController::getCaloriesByCalorieId)
                    delete(HealthTrackerController::deleteCalorieByCalorieId)
                    patch(HealthTrackerController::updateCalorie)
                }
            }
        }
    }

        fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
            OpenApiOptions(
                Info().apply {
                    title("Health Tracker App")
                    version("1.0")
                    description("Health Tracker API")
                }
            ).apply {
                path("/swagger-docs") // endpoint for OpenAPI json
                swagger(SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
                reDoc(ReDocOptions("/redoc")) // endpoint for redoc
            }
        )
    }

