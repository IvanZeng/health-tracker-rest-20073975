package ie.setu.domain.repository

import ie.setu.domain.Bmi
import ie.setu.domain.Calorie
import ie.setu.domain.db.Bmis
import ie.setu.utils.mapToBmi
import ie.setu.utils.mapToCalorie
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CalorieDAO {
        fun getAll(): ArrayList<Calorie> {
            val CaloriesList: ArrayList<Calorie> = arrayListOf()
            transaction {
                Calories.selectAll().map {
                    CaloriesList.add(mapToCalorie(it))
                }
            }
            return CaloriesList
        }

        fun findByCalorieId(id: Int): Calorie? {
            return transaction {
                Calories
                    .select() { Calories.id eq id }
                    .map { mapToCalorie(it) }
                    .firstOrNull()
            }
        }

        fun findByUserId(userId: Int): List<Calorie> {
            return transaction {
                Calories
                    .select { Calories.userId eq userId }
                    .map { mapToCalorie(it) }
            }
        }


        fun save(calorie: Calorie) {
            transaction {
                Calories.insert {
                    it[dateTime] = calorie.dateTime
                    it[calorieGet] = calorie.calorieGet
                    it[state] = calorie.state
                    it[userId] = calorie.userId
                }
            }
        }

        fun updateByCalorieId(calorieId: Int, calorieDTO: Calorie) {
            transaction {
                Calories.update({
                    Calories.id eq calorieId
                }) {
                    it[dateTime] = calorieDTO.dateTime
                    it[calorieGet] = calorieDTO.calorieGet
                    it[state] = calorieDTO.state
                    it[userId] = calorieDTO.userId
                }
            }
        }

        fun deleteByCalorieId(CalorieId: Int): Int {
            return transaction {
                Calories.deleteWhere { Calories.id eq CalorieId }
            }
        }

        fun deleteByUserId(userId: Int): Int {
            return transaction {
                Calories.deleteWhere { Calories.userId eq userId }
            }
        }
    }