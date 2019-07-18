package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        if (question != Question.IDLE && !question.validation.matches(answer)) {
            return "${question.hint}\n${question.question}" to status.color
        }

        val nextQuestion = question.nextQuestion()
        val nextStatus = status.nextStatus()
        val text = if (question.answers.contains(answer.toLowerCase()) || question == Question.IDLE) {
            question = nextQuestion
            if (nextQuestion == Question.IDLE) {
                "Отлично - ты справился\nНа этом все, вопросов больше нет"
            } else {
                "Отлично - ты справился\n${nextQuestion.question}"
            }
        } else {
            if (status == Status.CRITICAL) {
                question = Question.NAME
                status = Status.NORMAL
                "Это неправильный ответ. Давай все по новой\n${question.question}"
            } else {
                status = nextStatus
                "Это неправильный ответ\n${question.question}"
            }
        }

        return text to status.color
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

    enum class Question(val question: String, val answers: List<String>, val hint: String, val validation: Regex) {
        NAME(
            "Как меня зовут?",
            listOf("бендер", "bender"),
            "Имя должно начинаться с заглавной буквы",
            Regex("[A-ZА-Я]\\w+")
        ) {
            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION(
            "Назови мою профессию?",
            listOf("сгибальщик", "bender"),
            "Профессия должна начинаться со строчной буквы",
            Regex("[a-zа-я]\\w+")
        ) {
            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL(
            "Из чего я сделан?",
            listOf("металл", "дерево", "metal", "iron", "wood"),
            "Материал не должен содержать цифр",
            Regex("\\D+")
        ) {
            override fun nextQuestion(): Question = BDAY
        },
        BDAY(
            "Когда меня создали?",
            listOf("2993"),
            "Год моего рождения должен содержать только цифры",
            Regex("\\d+")
        ) {
            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL(
            "Мой серийный номер?",
            listOf("2716057"),
            "Серийный номер содержит только цифры, и их 7",
            Regex("\\d{7}")
        ) {
            override fun nextQuestion(): Question = IDLE
        },
        IDLE(
            "На этом все, вопросов больше нет",
            listOf(),
            "",
            Regex("\\w+")
        ) {
            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question
    }
}