package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Activity
import ie.setu.domain.Bmi
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthTrackerControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class ReadUsers {
        @Test
        fun `get all users from the database returns 200 or 404 response`() {
            val response = Unirest.get(origin + "/api/users/").asString()
            if (response.status == 200) {
                val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedUsers.size)
            }
            else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get user by id when user does not exist returns 404 response`() {

            //Arrange & Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = retrieveUserById(Integer.MIN_VALUE)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `get user by email when user does not exist returns 404 response`() {
            // Arrange & Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = retrieveUserByEmail(nonExistingEmail)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting a user by id when id exists, returns a 200 response`() {

            //Arrange - add the user
            val addResponse = addUser(validName, validEmail)
            val addedUser : User = jsonToObject(addResponse.body.toString())

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = retrieveUserById(addedUser.id)
            assertEquals(200, retrieveResponse.status)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }

        @Test
        fun `getting a user by email when email exists, returns a 200 response`() {

            //Arrange - add the user
            addUser(validName, validEmail)

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = retrieveUserByEmail(validEmail)
            assertEquals(200, retrieveResponse.status)

            //After - restore the db to previous state by deleting the added user
            val retrievedUser : User = jsonToObject(retrieveResponse.body.toString())
            deleteUser(retrievedUser.id)
        }
    }

    @Nested
    inner class CreateUsers {
        @Test
        fun `add a user with correct details returns a 201 response`() {

            //Arrange & Act & Assert
            //    add the user and verify return code (using fixture data)
            val addResponse = addUser(validName, validEmail)
            assertEquals(201, addResponse.status)

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse= retrieveUserByEmail(validEmail)
            assertEquals(200, retrieveResponse.status)

            //Assert - verify the contents of the retrieved user
            val retrievedUser : User = jsonToObject(addResponse.body.toString())
            assertEquals(validEmail, retrievedUser.email)
            assertEquals(validName, retrievedUser.name)

            //After - restore the db to previous state by deleting the added user
            val deleteResponse = deleteUser(retrievedUser.id)
            assertEquals(204, deleteResponse.status)
        }
    }

    @Nested
    inner class UpdateUsers {
        @Test
        fun `updating a user when it exists, returns a 204 response`() {

            //Arrange - add the user that we plan to do an update on
            val addedResponse = addUser(validName, validEmail)
            val addedUser : User = jsonToObject(addedResponse.body.toString())

            //Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(204, updateUser(addedUser.id, updatedName, updatedEmail).status)

            //Act & Assert - retrieve updated user and assert details are correct
            val updatedUserResponse = retrieveUserById(addedUser.id)
            val updatedUser : User = jsonToObject(updatedUserResponse.body.toString())
            assertEquals(updatedName, updatedUser.name)
            assertEquals(updatedEmail, updatedUser.email)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }

        @Test
        fun `updating a user when it doesn't exist, returns a 404 response`() {

            //Act & Assert - attempt to update the email and name of user that doesn't exist
            assertEquals(404, updateUser(-1, updatedName, updatedEmail).status)
        }
    }

    @Nested
    inner class DeleteUsers {
        @Test
        fun `deleting a user when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteUser(-1).status)
        }

        @Test
        fun `deleting a user when it exists, returns a 204 response`() {

            //Arrange - add the user that we plan to do a delete on
            val addedResponse = addUser(validName, validEmail)
            val addedUser : User = jsonToObject(addedResponse.body.toString())

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted user --> 404 response
            assertEquals(404, retrieveUserById(addedUser.id).status)
        }
    }


    @Nested
    inner class CreateActivities {

        @Test
        fun `add an activity when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated activity that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id
            )
            assertEquals(201, addActivityResponse.status)

            //After - delete the user (Activity will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an activity when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addActivityResponse = addActivity(
                activities.get(0).description, activities.get(0).duration,
                activities.get(0).calories, activities.get(0).started, userId
            )
            assertEquals(404, addActivityResponse.status)
        }
    }

    @Nested
    inner class ReadActivities {

        @Test
        fun `get all activities from the database returns 200 or 404 response`() {
            val response = retrieveAllActivities()
            if (response.status == 200){
                val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
                assertNotEquals(0, retrievedActivities.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all activities by user id when user and activities exists returns 200 response`() {
            //Arrange - add a user and 3 associated activities that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id)
            addActivity(
                activities[1].description, activities[1].duration,
                activities[1].calories, activities[1].started, addedUser.id)
            addActivity(
                activities[2].description, activities[2].duration,
                activities[2].calories, activities[2].started, addedUser.id)

            //Assert and Act - retrieve the three added activities by user id
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
            assertEquals(3, retrievedActivities.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all activities by user id when no activities exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the activities by user id
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all activities by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve activities by user id
            val response = retrieveActivitiesByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get activity by activity id when no activity exists returns 404 response`() {
            //Arrange
            val activityId = -1
            //Assert and Act - attempt to retrieve the activity by activity id
            val response = retrieveActivityByActivityId(activityId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get activity by activity id when activity exists returns 200 response`() {
            //Arrange - add a user and associated activity
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addActivityResponse = addActivity(
                activities[0].description,
                activities[0].duration, activities[0].calories,
                activities[0].started, addedUser.id)
            assertEquals(201, addActivityResponse.status)
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)

            //Act & Assert - retrieve the activity by activity id
            val response = retrieveActivityByActivityId(addedActivity.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating an activity by activity id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val activityID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an activity/user that doesn't exist
            assertEquals(
                404, updateActivity(
                    activityID, updatedDescription, updatedDuration,
                    updatedCalories, updatedStarted, userId
                ).status
            )
        }

        @Test
        fun `updating an activity by activity id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addActivityResponse = addActivity(
                activities[0].description,
                activities[0].duration, activities[0].calories,
                activities[0].started, addedUser.id)
            assertEquals(201, addActivityResponse.status)
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)

            //Act & Assert - update the added activity and assert a 204 is returned
            val updatedActivityResponse = updateActivity(addedActivity.id, updatedDescription,
                updatedDuration, updatedCalories, updatedStarted, addedUser.id)
            assertEquals(204, updatedActivityResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedActivityResponse = retrieveActivityByActivityId(addedActivity.id)
            val updatedActivity = jsonNodeToObject<Activity>(retrievedActivityResponse)
            assertEquals(updatedDescription,updatedActivity.description)
            assertEquals(updatedDuration, updatedActivity.duration, 0.1)
            assertEquals(updatedCalories, updatedActivity.calories)
            assertEquals(updatedStarted, updatedActivity.started )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting an activity by activity id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivityByActivityId(-1).status)
        }

        @Test
        fun `deleting activities by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivitiesByUserId(-1).status)
        }

        @Test
        fun `deleting an activity by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id)
            assertEquals(201, addActivityResponse.status)

            //Act & Assert - delete the added activity and assert a 204 is returned
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)
            assertEquals(204, deleteActivityByActivityId(addedActivity.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all activities by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated activities that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addActivityResponse1 = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id)
            assertEquals(201, addActivityResponse1.status)
            val addActivityResponse2 = addActivity(
                activities[1].description, activities[1].duration,
                activities[1].calories, activities[1].started, addedUser.id)
            assertEquals(201, addActivityResponse2.status)
            val addActivityResponse3 = addActivity(
                activities[2].description, activities[2].duration,
                activities[2].calories, activities[2].started, addedUser.id)
            assertEquals(201, addActivityResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted activities
            val addedActivity1 = jsonNodeToObject<Activity>(addActivityResponse1)
            val addedActivity2 = jsonNodeToObject<Activity>(addActivityResponse2)
            val addedActivity3 = jsonNodeToObject<Activity>(addActivityResponse3)
            assertEquals(404, retrieveActivityByActivityId(addedActivity1.id).status)
            assertEquals(404, retrieveActivityByActivityId(addedActivity2.id).status)
            assertEquals(404, retrieveActivityByActivityId(addedActivity3.id).status)
        }
    }

    @Nested
    inner class CreateBmis {
        @Test
        fun `add an bmi when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated bmi that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addBmiResponse = addBmi(
                bmis[0].gender, bmis[0].height,
                bmis[0].weight, bmis[0].bmidata, addedUser.id
            )
            assertEquals(201, addBmiResponse.status)

            //After - delete the user (Bmi will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an bmi when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addBmiResponse = addBmi(
                bmis.get(0).gender, bmis.get(0).height,
                bmis.get(0).weight, bmis.get(0).bmidata, userId
            )
            assertEquals(404, addBmiResponse.status)
        }
    }

    @Nested
    inner class ReadBmis {
        @Test
        fun `get all bmis from the database returns 200 or 404 response`() {
            val response = retrieveAllBmis()
            if (response.status == 200){
                val retrievedBmis = jsonNodeToObject<Array<Bmi>>(response)
                assertNotEquals(0, retrievedBmis.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all bmis by user id when user and bmis exists returns 200 response`() {
            //Arrange - add a user and 3 associated bmis that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            addBmi(
                bmis[0].gender, bmis[0].height,
                bmis[0].weight, bmis[0].bmidata, addedUser.id)
            addBmi(
                bmis[1].gender, bmis[1].height,
                bmis[1].weight, bmis[1].bmidata, addedUser.id)
            addBmi(
                bmis[2].gender, bmis[2].height,
                bmis[2].weight, bmis[2].bmidata, addedUser.id)

            //Assert and Act - retrieve the three added bmis by user id
            val response = retrieveBmisByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedBmis = jsonNodeToObject<Array<Bmi>>(response)
            assertEquals(3, retrievedBmis.size)
            //After - delete the added user and assert a 204 is returned (bmis are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all bmis by user id when no bmis exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the bmis by user id
            val response = retrieveBmisByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all bmis by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve bmis by user id
            val response = retrieveBmisByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get bmi by bmi id when no bmi exists returns 404 response`() {
            //Arrange
            val bmiId = -1
            //Assert and Act - attempt to retrieve the bmi by bmi id
            val response = retrieveBmiByBmiId(bmiId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get bmi by bmi id when bmi exists returns 200 response`() {
            //Arrange - add a user and associated bmi
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addBmiResponse = addBmi(
                bmis[0].gender, bmis[0].height,
                bmis[0].weight, bmis[0].bmidata, addedUser.id)
            assertEquals(201, addBmiResponse.status)
            val addedBmi = jsonNodeToObject<Bmi>(addBmiResponse)

            //Act & Assert - retrieve the bmi by bmi id
            val response = retrieveBmiByBmiId(addedBmi.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }
    }

    @Nested
    inner class UpdateBmis {
        @Test
        fun `updating an bmi by bmi id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val bmiID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an bmi/user that doesn't exist
            assertEquals(
                404, updateBmi(
                    bmiID, updatedGender, updatedHeight,
                    updatedWeight, updatedBmidata, userId
                ).status
            )
        }

        @Test
        fun `updating an bmi by bmi id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated bmi that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addBmiResponse = addBmi(
                bmis[0].gender,
                bmis[0].height, bmis[0].weight,
                bmis[0].bmidata, addedUser.id)
            assertEquals(201, addBmiResponse.status)
            val addedBmi = jsonNodeToObject<Bmi>(addBmiResponse)

            //Act & Assert - update the added bmi and assert a 204 is returned
            val updatedBmiResponse = updateBmi(addedBmi.id, updatedGender,
                updatedHeight, updatedWeight, updatedBmidata, addedUser.id)
            assertEquals(204, updatedBmiResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedBmiResponse = retrieveBmiByBmiId(addedBmi.id)
            val updatedBmi = jsonNodeToObject<Bmi>(retrievedBmiResponse)
            assertEquals(updatedGender,updatedBmi.gender)
            assertEquals(updatedHeight, updatedBmi.height)
            assertEquals(updatedWeight, updatedBmi.weight)
            assertEquals(updatedBmidata, updatedBmi.bmidata )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteBmis {
        @Test
        fun `deleting an bmi by bmi id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteBmiByBmiId(-1).status)
        }

        @Test
        fun `deleting bmis by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteBmisByUserId(-1).status)
        }

        @Test
        fun `deleting an bmi by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated bmi that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addBmiResponse = addBmi(
                bmis[0].gender, bmis[0].height,
                bmis[0].weight, bmis[0].bmidata, addedUser.id)
            assertEquals(201, addBmiResponse.status)

            //Act & Assert - delete the added bmi and assert a 204 is returned
            val addedBmi = jsonNodeToObject<Bmi>(addBmiResponse)
            assertEquals(204, deleteBmiByBmiId(addedBmi.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all bmis by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated bmis that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addBmiResponse1 = addBmi(
                bmis[0].gender, bmis[0].height,
                bmis[0].weight, bmis[0].bmidata, addedUser.id)
            assertEquals(201, addBmiResponse1.status)
            val addBmiResponse2 = addBmi(
                bmis[1].gender, bmis[1].height,
                bmis[1].weight, bmis[1].bmidata, addedUser.id)
            assertEquals(201, addBmiResponse2.status)
            val addBmiResponse3 = addBmi(
                bmis[2].gender, bmis[2].height,
                bmis[2].weight, bmis[2].bmidata, addedUser.id)
            assertEquals(201, addBmiResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted bmis
            val addedBmi1 = jsonNodeToObject<Bmi>(addBmiResponse1)
            val addedBmi2 = jsonNodeToObject<Bmi>(addBmiResponse2)
            val addedBmi3 = jsonNodeToObject<Bmi>(addBmiResponse3)
            assertEquals(404, retrieveBmiByBmiId(addedBmi1.id).status)
            assertEquals(404, retrieveBmiByBmiId(addedBmi2.id).status)
            assertEquals(404, retrieveBmiByBmiId(addedBmi3.id).status)
        }
    }

    //--------------------------------------------------------------------------------------
    // HELPER METHODS - could move them into a test utility class when submitting assignment
    //--------------------------------------------------------------------------------------

    //helper function to add a test user to the database
    private fun addUser (name: String, email: String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\"}")
            .asJson()
    }

    //helper function to delete a test user from the database
    private fun deleteUser (id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id").asString()
    }

    //helper function to retrieve a test user from the database by email
    private fun retrieveUserByEmail(email : String) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/email/${email}").asString()
    }

    //helper function to retrieve a test user from the database by id
    private fun retrieveUserById(id: Int) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${id}").asString()
    }

    //helper function to add a test user to the database
    private fun updateUser (id: Int, name: String, email: String): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/users/$id")
            .body("{\"name\":\"$name\", \"email\":\"$email\"}")
            .asJson()
    }

    //helper function to retrieve all activities
    private fun retrieveAllActivities(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/activities").asJson()
    }

    //helper function to retrieve activities by user id
    private fun retrieveActivitiesByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/activities").asJson()
    }

    //helper function to retrieve activity by activity id
    private fun retrieveActivityByActivityId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/activities/${id}").asJson()
    }

    //helper function to delete an activity by activity id
    private fun deleteActivityByActivityId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/activities/$id").asString()
    }

    //helper function to delete an activity by activity id
    private fun deleteActivitiesByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/activities").asString()
    }

    //helper function to add a test user to the database
    private fun updateActivity(id: Int, description: String, duration: Double, calories: Int,
                               started: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/activities/$id")
            .body("""
                {
                  "description":"$description",
                  "duration":$duration,
                  "calories":$calories,
                  "started":"$started",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add an activity
    private fun addActivity(description: String, duration: Double, calories: Int,
                            started: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/activities")
            .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "calories":$calories,
                   "started":"$started",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }

    //helper function to add an bmi
    private fun addBmi(gender: String, height: Int, weight: Double,
                            bmidata: String, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/bmis")
            .body("""
                {
                   "gender":"$gender",
                   "height":$height,
                   "weight":$weight,
                   "bmidata":"$bmidata",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }

    //helper function to retrieve all bmis
    private fun retrieveAllBmis(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/bmis").asJson()
    }

    //helper function to retrieve bmis by user id
    private fun retrieveBmisByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/bmis").asJson()
    }

    //helper function to retrieve bmi by bmi id
    private fun retrieveBmiByBmiId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/bmis/${id}").asJson()
    }

    //helper function to add a test user to the database
    private fun updateBmi(id: Int, gender: String, height: Int, weight: Double,
                               bmidata: String, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/bmis/$id")
            .body("""
                {
                  "gender":"$gender",
                  "height":$height,
                  "weight":$weight,
                  "bmidata":"$bmidata",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to delete an bmi by bmi id
    private fun deleteBmiByBmiId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/bmis/$id").asString()
    }

    //helper function to delete an bmi by bmi id
    private fun deleteBmisByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/bmis").asString()
    }
}