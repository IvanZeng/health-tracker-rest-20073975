package ie.setu.domain.repository

import ie.setu.domain.Bmi
import ie.setu.domain.db.Bmis
import ie.setu.utils.mapToBMI
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BmiDAO {

    fun getAll(): ArrayList<Bmi> {
        val BmisList: ArrayList<Bmi> = arrayListOf()
        transaction {
            Bmis.selectAll().map {
                BmisList.add(mapToBMI(it))
            }
        }
        return BmisList
    }

    fun findByBmiId(id: Int): Bmi? {
        return transaction {
            Bmis
                .select() { Bmis.id eq id }
                .map { mapToBMI(it) }
                .firstOrNull()
        }
    }

    fun findByUserId(userId: Int): List<Bmi> {
        return transaction {
            Bmis
                .select { Bmis.userId eq userId }
                .map { mapToBMI(it)}
        }
    }


    fun save(bmi: Bmi) {
        transaction {
            Bmis.insert {
                it[height] = bmi.height
                it[weight] = bmi.weight
                it[bmiData] = bmi.bmiData
                it[userId] = bmi.userId
            }
        }
    }

    fun updateByBmiId(bmiId: Int, bmiDTO: Bmi) {
        transaction {
            Bmis.update({
                Bmis.id eq bmiId
            }) {
                it[height] = bmiDTO.height
                it[weight] = bmiDTO.weight
                it[bmiData] = bmiDTO.bmiData
                it[userId] = bmiDTO.userId
            }
        }
    }

    fun deleteByBmiId(BMIId: Int): Int {
        return transaction {
            Bmis.deleteWhere { Bmis.id eq BMIId }
        }
    }

    fun deleteByUserId(userId: Int): Int {
        return transaction {
            Bmis.deleteWhere { Bmis.userId eq userId }
        }
    }
}