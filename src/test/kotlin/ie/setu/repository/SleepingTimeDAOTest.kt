package ie.setu.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.SleepingTime
import ie.setu.domain.db.SleepingTimes
import ie.setu.domain.repository.SleepingTimeDAO
import ie.setu.helpers.*
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val sleepingTime1 = sleepingTimes.get(0)
private val sleepingTime2 = sleepingTimes.get(1)
private val sleepingTime3 = sleepingTimes.get(2)

class SleepingTimeDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateSleepingTimes {

        @Test
        fun `multiple sleepingTimes added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()
                //Act & Assert
                assertEquals(3, sleepingTimeDAO.getAll().size)
                assertEquals(sleepingTime1, sleepingTimeDAO.findBySleepingTimeId(sleepingTime1.id))
                assertEquals(sleepingTime2, sleepingTimeDAO.findBySleepingTimeId(sleepingTime2.id))
                assertEquals(sleepingTime3, sleepingTimeDAO.findBySleepingTimeId(sleepingTime3.id))
            }
        }
    }

    @Nested
    inner class ReadSleepingTimes {

        @Test
        fun `getting all sleepingTimes from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()
                //Act & Assert
                assertEquals(3, sleepingTimeDAO.getAll().size)
            }
        }

        @Test
        fun `get sleepingTime by user id that has no sleepingTimes, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()
                //Act & Assert
                assertEquals(0, sleepingTimeDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get sleepingTime by user id that exists, results in a correct sleepingTime(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()
                //Act & Assert
                assertEquals(sleepingTime1, sleepingTimeDAO.findByUserId(1).get(0))
                assertEquals(sleepingTime2, sleepingTimeDAO.findByUserId(1).get(1))
                assertEquals(sleepingTime3, sleepingTimeDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all sleepingTimes over empty table returns none`() {
            transaction {

                //Arrange - create and setup sleepingTimeDAO object
                SchemaUtils.create(SleepingTimes)
                val sleepingTimeDAO = SleepingTimeDAO()

                //Act & Assert
                assertEquals(0, sleepingTimeDAO.getAll().size)
            }
        }

        @Test
        fun `get sleepingTime by sleepingTime id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()
                //Act & Assert
                assertEquals(null, sleepingTimeDAO.findBySleepingTimeId(4))
            }
        }

        @Test
        fun `get sleepingTime by sleepingTime id that exists, results in a correct sleepingTime returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()
                //Act & Assert
                assertEquals(sleepingTime1, sleepingTimeDAO.findBySleepingTimeId(1))
                assertEquals(sleepingTime3, sleepingTimeDAO.findBySleepingTimeId(3))
            }
        }
    }

    @Nested
    inner class UpdateSleepingTimes {

        @Test
        fun `updating existing sleepingTime in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()

                //Act & Assert
                val sleepingTime3updated = SleepingTime(id = 3, started = DateTime.now(), finished = DateTime.now(), duration = 6.3, deepSleepingTime = 5, userId = 2)
                sleepingTimeDAO.updateBySleepingTimeId(sleepingTime3updated.id, sleepingTime3updated)
                assertEquals(sleepingTime3updated, sleepingTimeDAO.findBySleepingTimeId(3))
            }
        }

        @Test
        fun `updating non-existant sleepingTime in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()

                //Act & Assert
                val sleepingTime4updated = SleepingTime(id = 4, started = DateTime.now(), finished = DateTime.now(), duration = 6.3, deepSleepingTime = 5, userId = 2)
                sleepingTimeDAO.updateBySleepingTimeId(4, sleepingTime4updated)
                assertEquals(null, sleepingTimeDAO.findBySleepingTimeId(4))
                assertEquals(3, sleepingTimeDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteSleepingTimes {

        @Test
        fun `deleting a non-existant sleepingTime (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()

                //Act & Assert
                assertEquals(3, sleepingTimeDAO.getAll().size)
                sleepingTimeDAO.deleteBySleepingTimeId(4)
                assertEquals(3, sleepingTimeDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing sleepingTime (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()

                //Act & Assert
                assertEquals(3, sleepingTimeDAO.getAll().size)
                sleepingTimeDAO.deleteBySleepingTimeId(sleepingTime3.id)
                assertEquals(2, sleepingTimeDAO.getAll().size)
            }
        }


        @Test
        fun `deleting sleepingTimes when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()

                //Act & Assert
                assertEquals(3, sleepingTimeDAO.getAll().size)
                sleepingTimeDAO.deleteByUserId(3)
                assertEquals(3, sleepingTimeDAO.getAll().size)
            }
        }

        @Test
        fun `deleting sleepingTimes when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleepingTimes
                val userDAO = populateUserTable()
                val sleepingTimeDAO = populateSleepingTimeTable()

                //Act & Assert
                assertEquals(3, sleepingTimeDAO.getAll().size)
                sleepingTimeDAO.deleteByUserId(1)
                assertEquals(1, sleepingTimeDAO.getAll().size)
            }
        }
    }
}