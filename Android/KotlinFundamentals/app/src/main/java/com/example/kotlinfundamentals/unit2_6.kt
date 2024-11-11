package com.example.kotlinfundamentals

private fun printNotificationSummary(numberOfMessages: Int) {
    val message = if (numberOfMessages < 100) {
        "You have $numberOfMessages notifications."
    } else {
        "Your phone is blowing up! You have 99+ notifications."
    }
    println(message)
}

private fun ex2() {
    val morningNotification = 51
    val eveningNotification = 135

    printNotificationSummary(morningNotification)
    printNotificationSummary(eveningNotification)
}

fun ticketPrice(age: Int, isMonday: Boolean): Int {
    when (age) {
        in 0..12 -> return 15
        in 13..60 -> return if (isMonday) 25 else 30
        in 61..100 -> return 20
        else -> return -1
    }
}

private fun ex3() {
    val child = 5
    val adult = 28
    val senior = 87

    val isMonday = true

    println("The movie ticket price for a person aged $child is \$${ticketPrice(child, isMonday)}.")
    println("The movie ticket price for a person aged $adult is \$${ticketPrice(adult, isMonday)}.")
    println(
        "The movie ticket price for a person aged $senior is \$${
            ticketPrice(
                senior, isMonday
            )
        }."
    )

}

private fun printFinalTemperature(
    initialMeasurement: Double,
    initialUnit: String,
    finalUnit: String,
    conversionFormula: (Double) -> Double
) {
    val finalMeasurement =
        String.format("%.2f", conversionFormula(initialMeasurement)) // two decimal places
    println("$initialMeasurement degrees $initialUnit is $finalMeasurement degrees $finalUnit.")
}

private fun ex4() {
    printFinalTemperature(27.0, "Celsius", "Fahrenheit") { 9.0 / 5.0 * it + 32 }
    printFinalTemperature(350.0, "Kelvin", "Celsius") { it - 273.15 }
    printFinalTemperature(10.0, "Fahrenheit", "Kelvin") { 5.0 / 9.0 * (it - 32) + 273.15 }
}

//ex5
private class Song() {
    private var title: String = ""
    private var artist: String = ""
    private var yearPublished: Int = 0
    private var playCount: Int = 0

    constructor(title: String, artist: String, yearPublished: Int, playCount: Int) : this() {
        this.title = title
        this.artist = artist
        this.yearPublished = yearPublished
        this.playCount = playCount
    }

    fun printSongDescription() {
        println("$title, performed by $artist, was released in $yearPublished.")
    }

    val isPopular: Boolean
        get() = playCount >= 1000
}

private fun ex6() {
    val amanda = Person("Amanda", 33, "play tennis", null)
    val atiqah = Person("Atiqah", 28, "climb", amanda)

    amanda.showProfile()
    atiqah.showProfile()
}


private class Person(val name: String, val age: Int, val hobby: String?, val referrer: Person?) {
    fun showProfile() {
        println("Name: $name")
        println("Age: $age")
        print("Likes to $hobby. ")
        val message = if (referrer != null) {
            if (referrer.hobby == null) {
                "Has a referrer named ${referrer.name}, who has no hobby}."
            } else {
                "Has a referrer named ${referrer.name}, who likes to ${referrer.hobby}."
            }
        } else {
            "Doesn't have a referrer."
        }
        println(message)
    }
}

//ex7
private open class Phone(var isScreenLightOn: Boolean = false){
    open fun switchOn() {
        isScreenLightOn = true
    }

    fun switchOff() {
        isScreenLightOn = false
    }

    fun checkPhoneScreenLight() {
        val phoneScreenLight = if (isScreenLightOn) "on" else "off"
        println("The phone screen's light is $phoneScreenLight.")
    }
}

private class FoldablePhone(var isFolded: Boolean = true) : Phone() {
    override fun switchOn() {
        if (!isFolded) {
            super.switchOn()
        }
    }
    fun fold() {
        isFolded = true
    }
    fun unfold() {
        isFolded = false
    }
}
private class Bid(val amount: Int, val bidder: String)

private fun auctionPrice(bid: Bid?, minimumPrice: Int): Int {
    return bid?.amount ?: minimumPrice
}

private fun ex8() {
    val winningBid = Bid(5000, "Private Collector")

    println("Item A is sold at ${auctionPrice(winningBid, 2000)}.")
    println("Item B is sold at ${auctionPrice(null, 3000)}.")
}



fun main() {
    println("Ex2: ")
    ex2()
    println()
    println("Ex3: ")
    ex3()
    println()
    println("Ex4: ")
    ex4()
    println()
    println("Ex6: ")
    ex6()
    println()
    println("Ex8: ")
    ex8()
    println()
}