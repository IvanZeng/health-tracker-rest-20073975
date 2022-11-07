package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Bmis : Table("bmis") {
    val id = integer("id").autoIncrement().primaryKey()
    val gender = varchar("gender", 60)
    val height = integer("height")
    val weight = double("weight")
    val bmiData = varchar("bmiData", 60)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}