package ie.setu.domain.repository

import ie.setu.domain.Bmi
import ie.setu.domain.Calorie
import ie.setu.domain.db.Bmis
import ie.setu.utils.mapToBmi
import ie.setu.utils.mapToCalorie
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Manages the database transactions and returns the results of the tracsactions
 */
class CalorieDAO {

    /**
     * Get all [calorie] from the Calories table.
     * @return the list of the calorie following getAll.
     */
        fun getAll(): ArrayList<Calorie> {
            val CaloriesList: ArrayList<Calorie> = arrayListOf()
            transaction {
                Calories.selectAll().map {
                    CaloriesList.add(mapToCalorie(it))
                }
            }
            return CaloriesList
        }

    /**
     * Get a [calorie] from the Calories table.
     * @return the ID of the calorie following the finding.
     */
        fun findByCalorieId(id: Int): Calorie? {
            return transaction {
                Calories
                    .select() { Calories.id eq id }
                    .map { mapToCalorie(it) }
                    .firstOrNull()
            }
        }

    /**
     * Get [calorie] from the Calories table by user ID.
     * @return the ID of the calorie following the finding.
     */
        fun findByUserId(userId: Int): List<Calorie> {
            return transaction {
                Calories
                    .select { Calories.userId eq userId }
                    .map { mapToCalorie(it) }
            }
        }

    /**
     * Adds a [calorie] to the Calories table.
     * @return the id of the calorie following the add.
     */
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

    /**
     * Update [calorie] from the Calories table.
     * @return the calorie following the update.
     */
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

    /**
     * Delete [calorie] from the calorie table by calorie id.
     * @return the ID of the calorie following the delete.
     */
        fun deleteByCalorieId(CalorieId: Int): Int {
            return transaction {
                Calories.deleteWhere { Calories.id eq CalorieId }
            }
        }

    /**
     * Delete [calorie] from the Calorie table by user id.
     * @return the ID of the calorie following the delete.
     */
        fun deleteByUserId(userId: Int): Int {
            return transaction {
                Calories.deleteWhere { Calories.userId eq userId }
            }
        }
    }