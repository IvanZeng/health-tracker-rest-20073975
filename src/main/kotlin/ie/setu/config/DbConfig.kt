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
            "jdbc:postgresql://ec2-44-195-132-31.compute-1.amazonaws.com:5432/dcq2270vico6cm?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "hpdevxvinhxkvh",
            password = "e1bb5ee95437553473e02c5776b211f8954394a5d498992f98b2e6fbc15d9523")

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}