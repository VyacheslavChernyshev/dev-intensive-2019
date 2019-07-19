package ru.skillbranch.devintensive.models

import java.util.regex.Pattern

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {
    var failsCount: Int = 0

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val (isValid, validateError) = question.validateAnswer(answer)
        return if (!isValid) "$validateError\n${question.question}" to status.color
        else
            return if (question.answers.contains(answer.toLowerCase())) {
                question = question.nextQuestion()
                "Отлично - ты справился\n${question.question}" to status.color
            } else {
                status = status.nextStatus()
                failsCount++
                if (failsCount <= 3)
                    "Это неправильный ответ\n${question.question}" to status.color
                else {
                    status = Status.NORMAL
                    question = Question.NAME
                    failsCount = 0
                    "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                }
            }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf<String>("bender", "бендер")) {
            override fun nextQuestion() = PROFESSION
            override fun validateAnswer(answer: String): Pair<Boolean, String> {
                return Pattern.compile("^[A-ZА-Я].*").matcher(answer).matches() to
                        "Имя должно начинаться с заглавной буквы"
            }
        },
        PROFESSION("Назови мою профессию?", listOf<String>("bender", "сгибальщик")) {
            override fun nextQuestion() = MATERIAL
            override fun validateAnswer(answer: String): Pair<Boolean, String> {
                return Pattern.compile("^[a-zа-я].*").matcher(answer).matches() to
                        "Профессия должна начинаться со строчной буквы"
            }
        },
        MATERIAL("Из чего я сделан?", listOf<String>("metal", "iron", "wood", "металл", "дерево")) {
            override fun nextQuestion() = BDAY
            override fun validateAnswer(answer: String): Pair<Boolean, String> {
                return Pattern.compile("^[^0-9]*$").matcher(answer).matches() to
                        "Материал не должен содержать цифр"
            }
        },
        BDAY("Когда меня создали?", listOf<String>("2993")) {
            override fun nextQuestion() = SERIAL
            override fun validateAnswer(answer: String): Pair<Boolean, String> {
                return Pattern.compile("^[0-9]+\$").matcher(answer).matches() to
                        "Год моего рождения должен содержать только цифры"
            }
        },
        SERIAL("Мой серийный номер?", listOf<String>("2716057")) {
            override fun nextQuestion() = IDLE
            override fun validateAnswer(answer: String): Pair<Boolean, String> {
                return Pattern.compile("^[0-9]{7}\$").matcher(answer).matches() to
                        "Серийный номер содержит только цифры, и их 7"
            }
        },
        IDLE("На этом все, вопросов больше нет", listOf<String>()) {
            override fun nextQuestion() = IDLE
            override fun validateAnswer(answer: String): Pair<Boolean, String> = true to ""
        };

        abstract fun nextQuestion(): Question
        abstract fun validateAnswer(answer: String): Pair<Boolean, String>
    }
}