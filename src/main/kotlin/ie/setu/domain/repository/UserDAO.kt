package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Manages the database transactions and returns the results of the tracsactions
 */
class UserDAO {

    /**
     * Get all [user] from the Users table.
     * @return the list of the user following getAll.
     */
    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }

    /**
     * Get a [user] from the Users table.
     * @return the ID of the user following the finding.
     */
    fun findById(id: Int): User?{
        return transaction {
            Users.select() {
                Users.id eq id}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    /**
     * Adds a [user] to the Users table.
     * @return the id of the user following the add.
     */
    fun save(user: User) : Int?{
        return transaction {
            Users.insert {
                it[name] = user.name
                it[email] = user.email
            } get Users.id
        }
    }

    /**
     * Get [user] from the User table by email.
     * @return the ID of the user following the finding.
     */
    fun findByEmail(email: String) :User?{
        return transaction {
            Users.select() {
                Users.email eq email}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    /**
     * Delete [user] from the User table.
     * @return the ID of the user following the delete.
     */
    fun delete(id: Int):Int{
        return transaction{
            Users.deleteWhere{
                Users.id eq id
            }
        }
    }

    /**
     * Update [user] from the User table.
     * @return the ID of the user following the update.
     */
    fun update(id: Int, user: User): Int{
        return transaction {
            Users.update ({
                Users.id eq id}) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }
}