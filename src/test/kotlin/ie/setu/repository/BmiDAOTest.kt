package ie.setu.repository

import ie.setu.controllers.HealthTrackerController.bmiDAO
import ie.setu.domain.Bmi
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.Bmis
import ie.setu.domain.repository.BmiDAO
import ie.setu.helpers.*
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val bmi1 = bmis.get(0)
private val bmi2 = bmis.get(1)
private val bmi3 = bmis.get(2)

class BmiDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateBmis {

        @Test
        fun `multiple bmis added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()
                //Act & Assert
                assertEquals(3, bmiDAO.getAll().size)
                assertEquals(bmi1, bmiDAO.findByBmiId(bmi1.id))
                assertEquals(bmi2, bmiDAO.findByBmiId(bmi2.id))
                assertEquals(bmi3, bmiDAO.findByBmiId(bmi3.id))
            }
        }
    }

    @Nested
    inner class ReadBmis {

        @Test
        fun `getting all bmis from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()
                //Act & Assert
                assertEquals(3, bmiDAO.getAll().size)
            }
        }

        @Test
        fun `get bmi by user id that has no bmis, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()
                //Act & Assert
                assertEquals(0, bmiDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get bmi by user id that exists, results in a correct bmi(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()
                //Act & Assert
                assertEquals(bmi1, bmiDAO.findByUserId(1).get(0))
                assertEquals(bmi2, bmiDAO.findByUserId(1).get(1))
                assertEquals(bmi3, bmiDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all bmis over empty table returns none`() {
            transaction {

                //Arrange - create and setup bmiDAO object
                SchemaUtils.create(Bmis)
                val bmiDAO = BmiDAO()

                //Act & Assert
                assertEquals(0, bmiDAO.getAll().size)
            }
        }

        @Test
        fun `get bmiTime by bmiTime id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()
                //Act & Assert
                assertEquals(null, bmiDAO.findByBmiId(4))
            }
        }

        @Test
        fun `get bmi by bmi id that exists, results in a correct bmi returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()
                //Act & Assert
                assertEquals(bmi1, bmiDAO.findByBmiId(1))
                assertEquals(bmi3, bmiDAO.findByBmiId(3))
            }
        }
    }

    @Nested
    inner class UpdateBmis {

        @Test
        fun `updating existing bmi in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()

                //Act & Assert
                val bmi3updated = Bmi(id = 3, gender = "Female" ,height = 169, weight = 44.4, bmiData = "Underweight", userId = 2)
                bmiDAO.updateByBmiId(bmi3updated.id, bmi3updated)
                assertEquals(bmi3updated, bmiDAO.findByBmiId(3))
            }
        }

        @Test
        fun `updating non-existant bmi in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()

                //Act & Assert
                val bmi4updated = Bmi(id = 3, gender = "Female" ,height = 169, weight = 44.4, bmiData = "Underweight", userId = 2)
                bmiDAO.updateByBmiId(4, bmi4updated)
                assertEquals(null, bmiDAO.findByBmiId(4))
                assertEquals(3, bmiDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteBmis {

        @Test
        fun `deleting a non-existant bmi (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()

                //Act & Assert
                assertEquals(3, bmiDAO.getAll().size)
                bmiDAO.deleteByBmiId(4)
                assertEquals(3, bmiDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing bmi (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val sbmiDAO = populateBmiTable()

                //Act & Assert
                assertEquals(3, bmiDAO.getAll().size)
                bmiDAO.deleteByBmiId(bmi3.id)
                assertEquals(2, bmiDAO.getAll().size)
            }
        }


        @Test
        fun `deleting bmis when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()

                //Act & Assert
                assertEquals(3, bmiDAO.getAll().size)
                bmiDAO.deleteByUserId(3)
                assertEquals(3, bmiDAO.getAll().size)
            }
        }

        @Test
        fun `deleting bmis when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three bmis
                val userDAO = populateUserTable()
                val bmiDAO = populateBmiTable()

                //Act & Assert
                assertEquals(3, bmiDAO.getAll().size)
                bmiDAO.deleteByUserId(1)
                assertEquals(1, bmiDAO.getAll().size)
            }
        }
    }
}