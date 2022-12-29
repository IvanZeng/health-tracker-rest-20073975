package ie.setu.domain.repository


import ie.setu.domain.SleepingTime
import ie.setu.domain.db.SleepingTimes
import ie.setu.utils.mapToSleepingTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class SleepingTimeDAO {

    fun getAll(): ArrayList<SleepingTime> {
        val sleepingTimesList: ArrayList<SleepingTime> = arrayListOf()
        transaction {
            SleepingTimes.selectAll().map {
                sleepingTimesList.add(mapToSleepingTime(it))
            }
        }
        return sleepingTimesList
    }

    fun findBySleepingTimeId(id: Int): SleepingTime? {
        return transaction {
            SleepingTimes
                .select() { SleepingTimes.id eq id }
                .map { mapToSleepingTime(it) }
                .firstOrNull()
        }
    }

    fun findByUserId(userId: Int): List<SleepingTime> {
        return transaction {
            SleepingTimes
                .select { SleepingTimes.userId eq userId }
                .map { mapToSleepingTime(it)}
        }
    }

    fun save(sleepingTime: SleepingTime): Int {
        return transaction {
            SleepingTimes.insert {
                it[startedAt] = sleepingTime.startedAt
                it[deepSleepingTime] = sleepingTime.deepSleepingTime
                it[userId] = sleepingTime.userId
            }
        } get SleepingTimes.id
    }

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

    fun deleteBySleepingTimeId(sleepingTimeId: Int): Int {
        return transaction {
            SleepingTimes.deleteWhere { SleepingTimes.id eq sleepingTimeId }
        }
    }

    fun deleteByUserId(userId: Int): Int {
        return transaction {
            SleepingTimes.deleteWhere { SleepingTimes.userId eq userId }
        }
    }
}