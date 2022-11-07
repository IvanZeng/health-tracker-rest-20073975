package ie.setu.domain

import org.joda.time.DateTime

data class Calorie (var id: Int,
                    var dateTime: DateTime,
                    var calorieGet: Int,
                    var state: String,
                    var userId: Int)