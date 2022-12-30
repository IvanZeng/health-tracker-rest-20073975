package ie.setu.domain.repository


import ie.setu.domain.Sleepingtime
import ie.setu.domain.db.Sleepingtimes
import ie.setu.utils.mapToSleepingTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Manages the database transactions and returns the results of the tracsactions
 */
class SleepingTimeDAO {

    /**
     * Get all [sleepingTime] from the SleepingTimes table.
     * @return the list of the sleepingTime following getAll.
     */
    fun getAll(): ArrayList<Sleepingtime> {
        val sleepingTimesList: ArrayList<Sleepingtime> = arrayListOf()
        transaction {
            Sleepingtimes.selectAll().map {
                sleepingTimesList.add(mapToSleepingTime(it))
            }
        }
        return sleepingTimesList
    }

    /**
     * Get a [sleepingTime] from the SleepingTimes table.
     * @return the ID of the sleepingTime following the finding.
     */
    fun findBySleepingTimeId(id: Int): Sleepingtime? {
        return transaction {
            Sleepingtimes
                .select() { Sleepingtimes.id eq id }
                .map { mapToSleepingTime(it) }
                .firstOrNull()
        }
    }

    /**
     * Get [sleepingTime] from the SleepingTimes table by user ID.
     * @return the ID of the sleepingTime following the finding.
     */
    fun findByUserId(userId: Int): List<Sleepingtime> {
        return transaction {
            Sleepingtimes
                .select { Sleepingtimes.userId eq userId }
                .map { mapToSleepingTime(it)}
        }
    }

    /**
     * Adds a [sleepingTime] to the SleepingTimes table.
     * @return the id of the sleepingTime following the add.
     */
    fun save(sleepingTime: Sleepingtime): Int {
        return transaction {
            Sleepingtimes.insert {
                it[startedAt] = sleepingTime.startedAt
                it[deepSleepingTime] = sleepingTime.deepSleepingTime
                it[userId] = sleepingTime.userId
            }
        } get Sleepingtimes.id
    }

    /**
     * Update [sleepingTime] from the SleepingTimes table.
     * @return the sleepingTime following the update.
     */
    fun updateBySleepingTimeId(sleepingTimeId: Int, sleepingtimeToUpdate: Sleepingtime) : Int{
        return transaction {
            Sleepingtimes.update ({
                Sleepingtimes.id eq sleepingTimeId}) {
                it[startedAt] = sleepingtimeToUpdate.startedAt
                it[deepSleepingTime] = sleepingtimeToUpdate.deepSleepingTime
                it[userId] = sleepingtimeToUpdate.userId
            }
        }
    }

    /**
     * Delete [sleepingTime] from the calorie table by SleepingTime id.
     * @return the ID of the sleepingTime following the delete.
     */
    fun deleteBySleepingTimeId(sleepingTimeId: Int): Int {
        return transaction {
            Sleepingtimes.deleteWhere { Sleepingtimes.id eq sleepingTimeId }
        }
    }

    /**
     * Delete [sleepingTime] from the sleepingTime table by user id.
     * @return the ID of the sleepingTime following the delete.
     */
    fun deleteByUserId(userId: Int): Int {
        return transaction {
            Sleepingtimes.deleteWhere { Sleepingtimes.userId eq userId }
        }
    }
}