package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.utils.mapToActivity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Manages the database transactions and returns the results of the tracsactions
 */
class ActivityDAO {

    /**
     * Get all [activity] from the Activities table.
     * @return the list of the activity following getAll.
     */
    //Get all the activities in the database regardless of user id
    fun getAll(): ArrayList<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it)) }
        }
        return activitiesList
    }

    /**
     * Get a [activity] from the Activities table.
     * @return the ID of the activity following the finding.
     */
    //Find a specific activity by activity id
    fun findByActivityId(id: Int): Activity?{
        return transaction {
            Activities
                .select() { Activities.id eq id}
                .map{mapToActivity(it)}
                .firstOrNull()
        }
    }

    /**
     * Get [activity] from the Activities table by user ID.
     * @return the ID of the user following the finding.
     */
    //Find all activities for a specific user id
    fun findByUserId(userId: Int): List<Activity>{
        return transaction {
            Activities
                .select {Activities.userId eq userId}
                .map {mapToActivity(it)}
        }
    }

    /**
     * Adds a [activity] to the Activities table.
     * @return the id of the activity following the add.
     */
    //Save an activity to the database
    fun save(activity: Activity): Int {
        return transaction {
            Activities.insert {
                it[description] = activity.description
                it[duration] = activity.duration
                it[calories] = activity.calories
                it[started] = activity.started
                it[userId] = activity.userId
            }
        } get Activities.id
    }

    /**
     * Update [activity] from the Activities table.
     * @return the activity following the update.
     */
    fun updateByActivityId(activityId: Int, activityToUpdate: Activity) : Int{
        return transaction {
            Activities.update ({
                Activities.id eq activityId}) {
                it[description] = activityToUpdate.description
                it[duration] = activityToUpdate.duration
                it[calories] = activityToUpdate.calories
                it[started] = activityToUpdate.started
                it[userId] = activityToUpdate.userId
            }
        }
    }

    /**
     * Delete [activity] from the Activity table by activity id.
     * @return the ID of the activity following the delete.
     */
    fun deleteByActivityId (activityId: Int): Int{
        return transaction{
            Activities.deleteWhere { Activities.id eq activityId }
        }
    }

    /**
     * Delete [activity] from the Activity table by user id.
     * @return the ID of the user following the delete.
     */
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Activities.deleteWhere { Activities.userId eq userId }
        }
    }
}