package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object Bmis : Table("bmis") {
    val id = integer("id").autoIncrement().primaryKey()
    val gender = varchar("gender", 60)
    val height = integer("height")
    val weight = double("weight")
    val bmiData = varchar("BMI", 60)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}