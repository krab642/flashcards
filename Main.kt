package flashcards

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import java.util.logging.LogRecord
import kotlin.text.Charsets.UTF_8


val scanner = Scanner(System.`in`)
val logList = MutableList<String>(0) {""}
val cards = MutableList<String>(0) {"sdf"}
val wrongAnswers = MutableList<Int>(0) {0}

fun main(args: Array<String>) {

    if (args.isEmpty()) {

    } else  {
        for (i in args.indices) {
            if (args[i] == "-import") {
                importFomFile(cards, args[i+1])
            }
        }


    }



    //importFomFile(cards, args[0])

    mainMenu(cards)
    println("Bye bye!")

    if (args.isEmpty()) {

    } else {
        for (i in args.indices) {
            if (args[i] == "-export") {
                exsportToFile(cards, args[i+1])
            }
        }

    }

}



fun mainMenu (cardsList: MutableList<String>) {
    do {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        logList.add("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        var command = scanner.nextLine()
        logList.add(command)
        when (command) {
            "add" -> add(cardsList)
            "remove" -> remove(cardsList)
            "import" -> import(cardsList)
            "export" -> exsport(cardsList)
            "ask" -> ask(cardsList)
            "log" -> saveLog()
            "hardest card" -> hardestCard(cardsList)
            "reset stats" -> resetStats(cardsList)
            "list" -> println(cardsList.withIndex().joinToString())
            "wrong" -> println(wrongAnswers.joinToString())
            "exit" -> return
            else -> {
                println("wrong command")
                logList.add("wrong command")
            }
        }
    } while (command != "exit")
}

fun resetStats(cardsList: MutableList<String>) {
    for (i in wrongAnswers.indices) {
        wrongAnswers[i] = 0
    }
    println("Card statistics has been reset.")
    logList.add("Card statistics has been reset.")
}


fun hardestCard(cardsList: MutableList<String>) {
    val hardQuestions = MutableList<String>(0) {""}
    var j = 0
    if (wrongAnswers.max() == 0 || wrongAnswers.size == 0) {
        println("There are no cards with errors.")
        logList.add("There are no cards with errors.")
    } else if (wrongAnswers.max() != 0 && wrongAnswers.filter { it == wrongAnswers.max() }.size == 1) {
        for (i in wrongAnswers.indices) {
            if (wrongAnswers[i] == wrongAnswers.max()) {
                hardQuestions.add(j, cardsList[i])
                j++
            }
        }
        println("The hardest card is \"${hardQuestions.joinToString()}\". You have ${wrongAnswers.max()} errors answering it.")
        logList.add("The hardest card is \"${hardQuestions.joinToString()}\". You have ${wrongAnswers.max()} errors answering it.")
    } else if (wrongAnswers.max() != 0 && wrongAnswers.filter { it == wrongAnswers.max() }.size > 1) {
        for (i in wrongAnswers.indices) {
            if (wrongAnswers[i] == wrongAnswers.max()) {
                hardQuestions.add(j, cardsList[i])
                j++
            }
        }
        println("The hardest cards are \"${hardQuestions.joinToString("\", \"")}\". You have ${wrongAnswers.max()} errors answering them.")
        logList.add("The hardest cards are \"${hardQuestions.joinToString("\", \"")}\". You have ${wrongAnswers.max()} errors answering them.")
    }
}


fun exsport(cardsList: MutableList<String>) {
    println("File name:")
    logList.add("File name:")
    val fileName = scanner.nextLine()
    logList.add(fileName)
    val myFile = File(fileName)
    val content = cardsList.joinToString("|:|") + "|:|" + "ANSWERZ" + "|:|" + wrongAnswers.joinToString("|:|")
    myFile.writeText(content, charset = UTF_8)
    val numberOfCards = cardsList.size / 2
    //if (numberOfCards == 1) println("1 card has been saved.")
    println("$numberOfCards cards have been saved.")
    println()
    logList.add("$numberOfCards cards have been saved.")
}

fun ask(cardsList: MutableList<String>) {
    println("How many times to ask?")
    logList.add("How many times to ask?")
    val numberOfCards: Int? = scanner.nextLine().toIntOrNull()
    logList.add(numberOfCards.toString())
    if (numberOfCards == null) {
        println("Цифра, Джонни")
        return
    }
    repeat(numberOfCards) {
        val card = randomQuestion(cardsList)
        val correctAnswer = getCorrectAnswer(cardsList, card)
        println("Print the definition of \"$card\":")
        logList.add("Print the definition of \"$card\":")
        val userAnswer = scanner.nextLine()
        logList.add(userAnswer)
        var checkForRepeat = doublesInQuestions(cardsList, userAnswer, QorA.ANSWER.divleft)
        if (checkForRepeat) {
            if (correctAnswer == userAnswer) {
                println("Correct answer.")
                logList.add("Correct answer.")
            } else {
                val questionYouAnswer = getCorrectQuestion(cardsList, userAnswer)
                wrongAnswers[cardsList.indexOf(card)] ++
                println("Wrong answer. The correct one is \"$correctAnswer\", you've just written the definition of \"$questionYouAnswer\".")
                println()
                logList.add("Wrong answer. The correct one is \"$correctAnswer\", you've just written the definition of \"$questionYouAnswer\".")
            }
        } else {
            wrongAnswers[cardsList.indexOf(card)] ++
            println("Wrong answer. The correct one is \"$correctAnswer\".")
            println()
            logList.add("Wrong answer. The correct one is \"$correctAnswer\".")
        }
    }

}

fun import(cardsList: MutableList<String>) {
    println("File name:")
    logList.add("File name:")
    val fileName = scanner.nextLine()
    logList.add(fileName)

    try {
        val lines1 = File(fileName).readText().split("|:|").toMutableList()
        val indexWeNeed = lines1.indexOf("ANSWERZ")
        val lines = lines1.slice(0..indexWeNeed-1) as MutableList
        val answerStatistic = lines1.slice(indexWeNeed + 1 .. lines1.lastIndex)

        for (i in answerStatistic.indices) {
            wrongAnswers.add(answerStatistic[i].toInt())
        }

        removeDoubleLines(cardsList, lines)



        cardsList.addAll(lines)
        println("${lines.size / 2} cards have been loaded.")
        println()
        logList.add("${lines.size / 2} cards have been loaded.")
    } catch (e: FileNotFoundException) {
        println("File not found.")
        println()
        logList.add("File not found.")
    }

}

fun removeDoubleLines(listOfQuestions: MutableList<String>, listFromFile: MutableList<String>) {
    for (question in listFromFile.filterIndexed { i, _ -> i % 2 == QorA.QUESTION.divleft}) {
        if (listOfQuestions.contains(question) ) {
            val indexOfCard = listOfQuestions.indexOf(question)
            listOfQuestions.removeAt(indexOfCard + 1)
            listOfQuestions.removeAt(indexOfCard)
            //println("index of card $indexOfCard") // удалить!!
            wrongAnswers.removeAt(indexOfCard + 1)
            wrongAnswers.removeAt(indexOfCard)
        }
    }
    for (answer in listFromFile.filterIndexed { i, _ -> i % 2 == QorA.ANSWER.divleft}) {
        if (listOfQuestions.contains(answer) ) {
            val indexOfCard = listOfQuestions.indexOf(answer)
            listOfQuestions.removeAt(indexOfCard)
            listOfQuestions.removeAt(indexOfCard - 1)
            //println("index of card $indexOfCard") // удалить!!
            wrongAnswers.removeAt(indexOfCard)
            wrongAnswers.removeAt(indexOfCard - 1)
        }
    }
}

fun remove(cardsList: MutableList<String>) {
    println("The card:")
    logList.add("The card:")
    val card = scanner.nextLine()
    logList.add(card)
    var checkForRepeat = doublesInQuestions(cardsList, card, QorA.QUESTION.divleft)
    if (checkForRepeat) {
        val indexOfCard = cardsList.indexOf(card)
        cardsList.removeAt(indexOfCard + 1)
        cardsList.removeAt(indexOfCard)
        wrongAnswers.removeAt(indexOfCard + 1)
        wrongAnswers.removeAt(indexOfCard)
        println("The card has been removed.")
        println()
        logList.add("The card has been removed.")
    } else {
        println("Can't remove \"$card\": there is no such card.")
        println()
        logList.add("Can't remove \"$card\": there is no such card.")
    }
}

fun add(cardsList: MutableList<String>) {
    var checkForRepeat = true
    println("The card:")
    logList.add("The card:")
    var card = scanner.nextLine()
    logList.add(card)
    checkForRepeat = doublesInQuestions(cardsList, card, QorA.QUESTION.divleft)
    if (checkForRepeat) {
        //do {
        println("The card \"$card\" already exists.")
        println()
        logList.add("The card \"$card\" already exists.")
        return
        //    card = scanner.nextLine()
        //   checkForRepeat = doublesInQuestions(cardsList, card, QorA.QUESTION.divleft)
        //} while (checkForRepeat)
    }


    println("The definition of the card:")
    logList.add("The definition of the card:")
    var definition = scanner.nextLine()
    logList.add(definition)
    checkForRepeat = doublesInQuestions(cardsList, definition, QorA.ANSWER.divleft)
    if (checkForRepeat) {
        //do {
        println("The definition \"$definition\" already exists.")
        println()
        logList.add("The definition \"$definition\" already exists.")
        return
        //   definition = scanner.nextLine()
        //    checkForRepeat = doublesInQuestions(cardsList, definition, QorA.ANSWER.divleft)
        //} while (checkForRepeat)
    }
    cardsList.add(card)
    cardsList.add(definition)
    wrongAnswers.add(0)
    wrongAnswers.add(0)
    println("The pair (\"$card\":\"$definition\") has been added.")
    println()
    logList.add("The pair (\"$card\":\"$definition\") has been added.")
}

fun doublesInQuestions(listOfQuestions: MutableList<String>, question: String, qis0Ais1: Int): Boolean {
    val answerNumbers = listOfQuestions.filterIndexed {i, v -> i % 2 == qis0Ais1}.contains(question)
    return answerNumbers
}

fun randomQuestion (listOfQuestions: MutableList<String>): String {
    val onlyQuestions = listOfQuestions.filterIndexed {i, v -> i % 2 == QorA.QUESTION.divleft}
    return onlyQuestions.random()
}

fun getCorrectAnswer(listOfQuestions: MutableList<String>, question: String): String {
    val indexOfQuestion = listOfQuestions.indexOf(question)
    val answer = listOfQuestions[indexOfQuestion + 1]
    return answer
}

fun getCorrectQuestion(listOfQuestions: MutableList<String>, answer: String): String {
    val indexOfQuestion = listOfQuestions.indexOf(answer)
    val question = listOfQuestions[indexOfQuestion - 1]
    return question
}




enum class QorA(val divleft: Int) {
    QUESTION(0),
    ANSWER(1)
}

fun saveLog () {
    println("File name:")
    logList.add("File name:")
    val fileName = scanner.nextLine()
    logList.add(fileName)
    File(fileName).writeText(logList.joinToString(", "))
    println("The log has been saved.")
    logList.add("The log has been saved.")
}

fun importFomFile(cardsList: MutableList<String>, fileName:String) {
    //println("File name:")
    //logList.add("File name:")
    //val fileName = scanner.nextLine()
    //logList.add(fileName)

    try {
        val lines1 = File(fileName).readText().split("|:|").toMutableList()
        val indexWeNeed = lines1.indexOf("ANSWERZ")
        val lines = lines1.slice(0..indexWeNeed-1) as MutableList
        val answerStatistic = lines1.slice(indexWeNeed + 1 .. lines1.lastIndex)

        for (i in answerStatistic.indices) {
            wrongAnswers.add(answerStatistic[i].toInt())
        }

        //removeDoubleLines(cardsList, lines)



        cardsList.addAll(lines)
        println("${lines.size / 2} cards have been loaded.")
        println()
        logList.add("${lines.size / 2} cards have been loaded.")
    } catch (e: FileNotFoundException) {
        println("File not found.")
        println()
        logList.add("File not found.")
    }

}


fun exsportToFile(cardsList: MutableList<String>, fileName: String) {
    //println("File name:")
    //logList.add("File name:")
    //val fileName = scanner.nextLine()
    //logList.add(fileName)
    val myFile = File(fileName)
    val content = cardsList.joinToString("|:|") + "|:|" + "ANSWERZ" + "|:|" + wrongAnswers.joinToString("|:|")
    myFile.writeText(content, charset = UTF_8)
    val numberOfCards = cardsList.size / 2
    //if (numberOfCards == 1) println("1 card has been saved.")
    println("$numberOfCards cards have been saved.")
    //println()
    logList.add("$numberOfCards cards have been saved.")
}
