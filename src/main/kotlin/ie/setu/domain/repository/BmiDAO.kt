package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.Bmi
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Bmis
import ie.setu.utils.mapToBmi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BmiDAO {

    fun getAll(): ArrayList<Bmi> {
        val bmisList: ArrayList<Bmi> = arrayListOf()
        transaction {
            Bmis.selectAll().map {
                bmisList.add(mapToBmi(it))
            }
        }
        return bmisList
    }

    fun findByBmiId(id: Int): Bmi? {
        return transaction {
            Bmis
                .select() { Bmis.id eq id }
                .map { mapToBmi(it) }
                .firstOrNull()
        }
    }

    fun findByUserId(userId: Int): List<Bmi> {
        return transaction {
            Bmis
                .select { Bmis.userId eq userId }
                .map { mapToBmi(it)}
        }
    }

    //Save an bmi to the database
    fun save(bmi: Bmi): Int {
        return transaction {
            Bmis.insert {
                it[gender] = bmi.gender
                it[height] = bmi.height
                it[weight] = bmi.weight
                it[bmidata] = bmi.bmidata
                it[userId] = bmi.userId
            }
        } get Bmis.id
    }

    fun updateByBmiId(bmiId: Int, bmiToUpdate: Bmi) : Int{
        return transaction {
            Bmis.update ({
                Bmis.id eq bmiId}) {
                it[gender] = bmiToUpdate.gender
                it[height] = bmiToUpdate.height
                it[weight] = bmiToUpdate.weight
                it[bmidata] = bmiToUpdate.bmidata
                it[userId] = bmiToUpdate.userId
            }
        }
    }

    fun deleteByBmiId(BmiId: Int): Int {
        return transaction {
            Bmis.deleteWhere { Bmis.id eq BmiId }
        }
    }

    fun deleteByUserId(userId: Int): Int {
        return transaction {
            Bmis.deleteWhere { Bmis.userId eq userId }
        }
    }
}