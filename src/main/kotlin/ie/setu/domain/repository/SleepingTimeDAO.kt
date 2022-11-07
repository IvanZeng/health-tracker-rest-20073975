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


    fun save(sleepingTime: SleepingTime) {
        transaction {
            SleepingTimes.insert {
                it[started] = sleepingTime.started
                it[finished] = sleepingTime.finished
                it[duration] = sleepingTime.duration
                it[deepSleepingTime] = sleepingTime.deepSleepingTime
                it[userId] = sleepingTime.userId
            }
        }
    }

    fun updateBySleepingTimeId(sleepingTimeId: Int, sleepingTimeDTO: SleepingTime) {
        transaction {
            SleepingTimes.update({
                SleepingTimes.id eq sleepingTimeId
            }) {
                it[started] = sleepingTimeDTO.started
                it[finished] = sleepingTimeDTO.finished
                it[duration] = sleepingTimeDTO.duration
                it[deepSleepingTime] = sleepingTimeDTO.deepSleepingTime
                it[userId] = sleepingTimeDTO.userId
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