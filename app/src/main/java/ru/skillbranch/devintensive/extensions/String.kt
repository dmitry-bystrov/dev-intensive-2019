package ru.skillbranch.devintensive.extensions

fun String.truncate(count: Int = 16): String {
    return if (this.trim().length <= count + 1)
        this.trim()
    else
        this.trim().substring(0, count + 1).trim() + "..."
}

fun String.stripHtml(): String {
    return this.replace(Regex("<[^<]*?>|&#\\d+;|&amp;|&lt;|&gt;|&quot;"), "")
        .replace(Regex("[^\\S\\r\\n]+"), " ")
}