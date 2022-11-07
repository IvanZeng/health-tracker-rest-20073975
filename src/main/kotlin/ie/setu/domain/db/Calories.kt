
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Calories : Table("calories") {
    val id = integer("id").autoIncrement().primaryKey()
    val dateTime = datetime("date")
    val calorieGet = integer("calorie")
    val state = varchar("state", 60)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}