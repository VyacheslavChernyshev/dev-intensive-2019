package ru.skillbranch.devintensive.utils

object Utils {
    private var translitTable = mutableMapOf(
        "а" to "a",
        "б" to "b",
        "в" to "v",
        "г" to "g",
        "д" to "d",
        "е" to "e",
        "ё" to "e",
        "ж" to "zh",
        "з" to "z",
        "и" to "i",
        "й" to "i",
        "к" to "k",
        "л" to "l",
        "м" to "m",
        "н" to "n",
        "о" to "o",
        "п" to "p",
        "р" to "r",
        "с" to "s",
        "т" to "t",
        "у" to "u",
        "ф" to "f",
        "х" to "h",
        "ц" to "c",
        "ч" to "ch",
        "ш" to "sh",
        "щ" to "sh'",
        "ъ" to "",
        "ы" to "i",
        "ь" to "",
        "э" to "e",
        "ю" to "yu",
        "я" to "ya"
    )

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.trim()?.split(" ")
        val firstName = parts?.getOrNullOrEmptyNull(0)
        val lastName = parts?.getOrNullOrEmptyNull(1)
        return Pair(firstName, lastName)
    }

    private fun List<String>.getOrNullOrEmptyNull(index: Int): String? {
        return when (this.getOrNull(index)) {
            "" -> null
            else -> this.getOrNull(index)
        }
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val firstInitials = firstName?.trim()?.getOrNull(0)?.toUpperCase()?.toString()
        val secondInitial = lastName?.trim()?.getOrNull(0)?.toUpperCase()?.toString()
        return when {
            secondInitial == null -> firstInitials
            firstInitials == null -> secondInitial
            else -> firstInitials + secondInitial
        }
    }

    fun transliteration(payload: String, divider: String = " "): String? {
        translitTable[" "] = divider
        return buildString {
            payload.forEach {
                append(
                    when {
                        it.isUpperCase() -> translitTable[it.toLowerCase().toString()]?.toUpperCase() ?: it.toUpperCase()
                        else -> translitTable[it.toLowerCase().toString()] ?: it.toString()
                    }
                )
            }
        }
    }
}