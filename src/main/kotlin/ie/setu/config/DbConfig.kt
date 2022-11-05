package ie.setu.config

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

class DbConfig{

    private val logger = KotlinLogging.logger {}

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{

        logger.info{"Starting DB Connection..."}

        val dbConfig = Database.connect(
            "jdbc:postgresql://ec2-54-147-33-38.compute-1.amazonaws.com:5432/dd1alnffpluckj?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "decoqpkgvcdpue",
            password = "c03e409c7da188b020d3be9238b88e61fa4b4fb4ecd0d6551523dec6daf1d78a")

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}