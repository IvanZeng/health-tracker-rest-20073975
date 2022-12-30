package ie.setu.domain.repository


import ie.setu.domain.SleepingTime
import ie.setu.domain.db.SleepingTimes
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
    fun getAll(): ArrayList<SleepingTime> {
        val sleepingTimesList: ArrayList<SleepingTime> = arrayListOf()
        transaction {
            SleepingTimes.selectAll().map {
                sleepingTimesList.add(mapToSleepingTime(it))
            }
        }
        return sleepingTimesList
    }

    /**
     * Get a [sleepingTime] from the SleepingTimes table.
     * @return the ID of the sleepingTime following the finding.
     */
    fun findBySleepingTimeId(id: Int): SleepingTime? {
        return transaction {
            SleepingTimes
                .select() { SleepingTimes.id eq id }
                .map { mapToSleepingTime(it) }
                .firstOrNull()
        }
    }

    /**
     * Get [sleepingTime] from the SleepingTimes table by user ID.
     * @return the ID of the sleepingTime following the finding.
     */
    fun findByUserId(userId: Int): List<SleepingTime> {
        return transaction {
            SleepingTimes
                .select { SleepingTimes.userId eq userId }
                .map { mapToSleepingTime(it)}
        }
    }

    /**
     * Adds a [sleepingTime] to the SleepingTimes table.
     * @return the id of the sleepingTime following the add.
     */
    fun save(sleepingTime: SleepingTime): Int {
        return transaction {
            SleepingTimes.insert {
                it[startedAt] = sleepingTime.startedAt
                it[deepSleepingTime] = sleepingTime.deepSleepingTime
                it[userId] = sleepingTime.userId
            }
        } get SleepingTimes.id
    }

    /**
     * Update [sleepingTime] from the SleepingTimes table.
     * @return the sleepingTime following the update.
     */
    fun updateBySleepingTimeId(sleepingTimeId: Int, sleepingTimeToUpdate: SleepingTime) : Int{
        return transaction {
            SleepingTimes.update ({
                SleepingTimes.id eq sleepingTimeId}) {
                it[startedAt] = sleepingTimeToUpdate.startedAt
                it[deepSleepingTime] = sleepingTimeToUpdate.deepSleepingTime
                it[userId] = sleepingTimeToUpdate.userId
            }
        }
    }

    /**
     * Delete [sleepingTime] from the calorie table by SleepingTime id.
     * @return the ID of the sleepingTime following the delete.
     */
    fun deleteBySleepingTimeId(sleepingTimeId: Int): Int {
        return transaction {
            SleepingTimes.deleteWhere { SleepingTimes.id eq sleepingTimeId }
        }
    }

    /**
     * Delete [sleepingTime] from the sleepingTime table by user id.
     * @return the ID of the sleepingTime following the delete.
     */
    fun deleteByUserId(userId: Int): Int {
        return transaction {
            SleepingTimes.deleteWhere { SleepingTimes.userId eq userId }
        }
    }
}