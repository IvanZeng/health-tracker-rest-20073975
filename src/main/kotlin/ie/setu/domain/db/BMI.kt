package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object BMI : Table("BMIs") {
    val id = integer("id").autoIncrement().primaryKey()
    val height = integer("height")
    val weight = double("duration")
    val bmi = integer("BMI")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}