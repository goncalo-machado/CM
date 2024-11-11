package com.example.kotlinfundamentals

private fun ex2() {
    println("Use the val keyword when the value doesn't change.")
    println("Use the var keyword when the value can change.")
    println("When you define a function, you define the parameters that can be passed to it.")
    println("When you call a function, you pass arguments for the parameters.")
}

private fun ex3() {
    println("New chat message from a friend")
}

private fun ex4() {
    val discountPercentage = 20
    val item = "Google Chromecast"
    val offer = "Sale  - Up to $discountPercentage% discount off $item! Hurry Up!"

    println(offer)
}

private fun ex5() {
    val numberOfAdults = 20
    val numberOfKids = 30
    val total = numberOfAdults + numberOfKids
    println("The total party size is: $total")
}

private fun ex6() {
    val baseSalary = 5000
    val bonusAmount = 1000
    val totalSalary = "$baseSalary + $bonusAmount"
    println("Congratulations for your bonus! You will receive a total of $totalSalary (additional bonus).")
}

private fun ex7step1() {
    val firstNumber = 10
    val secondNumber = 5
    val result = firstNumber % secondNumber
    println("The remainder of the division of $firstNumber by $secondNumber is $result.")
}

private fun add(firstNumber: Int, secondNumber: Int): Int {
    return firstNumber + secondNumber
}

private fun ex7step2() {
    val firstNumber = 10
    val secondNumber = 5
    val thirdNumber = 8

    val result = add(firstNumber, secondNumber)
    val anotherResult = add(firstNumber, thirdNumber)

    println("$firstNumber + $secondNumber = $result")
    println("$firstNumber + $thirdNumber = $anotherResult")
}

private fun subtract(firstNumber: Int, secondNumber: Int): Int {
    return firstNumber - secondNumber
}

private fun ex7step3() {
    val firstNumber = 10
    val secondNumber = 5
    val thirdNumber = 8

    val result = add(firstNumber, secondNumber)
    val anotherResult = subtract(firstNumber, thirdNumber)

    println("$firstNumber + $secondNumber = $result")
    println("$firstNumber - $thirdNumber = $anotherResult")
}

private fun displayAlertMessage(
    operatingSystem: String = "Unknown OS", emailId: String
): String {
    return "There is a new sign-in request on $operatingSystem for your Google Account $emailId."
}

private fun ex8() {
    val firstUserEmailId = "user_one@gmail.com"

    // The following line of code assumes that you named your parameter as emailId.
    // If you named it differently, feel free to update the name.
    println(displayAlertMessage(emailId = firstUserEmailId))
    println()

    val secondUserOperatingSystem = "Windows"
    val secondUserEmailId = "user_two@gmail.com"

    println(displayAlertMessage(secondUserOperatingSystem, secondUserEmailId))
    println()

    val thirdUserOperatingSystem = "Mac OS"
    val thirdUserEmailId = "user_three@gmail.com"

    println(displayAlertMessage(thirdUserOperatingSystem, thirdUserEmailId))
    println()
}

private fun pedometerStepsToCalories(numberOfSteps: Int): Double {
    val caloriesBurnedForEachStep = 0.04
    val totalCaloriesBurned = numberOfSteps * caloriesBurnedForEachStep
    return totalCaloriesBurned
}

private fun ex9() {
    val steps = 4000
    val caloriesBurned = pedometerStepsToCalories(steps)
    println("Walking $steps steps burns $caloriesBurned calories")
}

private fun compareTime(timeSpentToday: Int, timeSpentYesterday: Int): Boolean {
    return timeSpentToday > timeSpentYesterday
}

private fun ex10() {
    println("Have I spent more time using my phone today: ${compareTime(300, 250)}")
    println("Have I spent more time using my phone today: ${compareTime(300, 300)}")
    println("Have I spent more time using my phone today: ${compareTime(200, 220)}")
}

private fun printWeatherForCity(cityName: String, lowTemp: Int, highTemp: Int, chanceOfRain: Int) {
    println("City: $cityName")
    println("Low temperature: $lowTemp, High temperature: $highTemp")
    println("Chance of rain: $chanceOfRain%")
    println()
}

private fun ex11() {
    printWeatherForCity("Ankara", 27, 31, 82)
    printWeatherForCity("Tokyo", 32, 36, 10)
    printWeatherForCity("Cape Town", 59, 64, 2)
    printWeatherForCity("Guatemala City", 50, 55, 7)
}

fun main(){
    println("Ex2: ")
    ex2()
    println()
    println("Ex3: ")
    ex3()
    println()
    println("Ex4: ")
    ex4()
    println()
    println("Ex5: ")
    ex5()
    println()
    println("Ex6: ")
    ex6()
    println()
    println("Ex7step1: ")
    ex7step1()
    println()
    println("Ex7step2: ")
    ex7step2()
    println()
    println("Ex7step3: ")
    ex7step3()
    println()
    println("Ex8: ")
    ex8()
    println()
    println("Ex9: ")
    ex9()
    println()
    println("Ex10: ")
    ex10()
    println()
    println("Ex11: ")
    ex11()
    println()
}