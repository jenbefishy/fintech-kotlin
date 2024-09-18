class NewsDslBuilder {
    private val stringBuilder = StringBuilder()

    fun header(
        level: Int,
        content: () -> String,
    ) {
        require(level in 1..6) { "Header level must be between 1 and 6" }
        stringBuilder.append("<h$level>${content()}</h$level>\n")
    }

    fun text(content: () -> String) {
        stringBuilder.append("${content()}\n")
    }

    fun bold(content: String) = "<b>$content</b>\n"

    fun link(
        link: String,
        text: String,
    ) = "<a href=\"$link\">$text</a>"

    fun underlined(content: String) = "<u>$content</u>"

    fun build(): String = stringBuilder.toString()
}

fun newsDsl(builder: NewsDslBuilder.() -> Unit): String {
    val dslBuilder = NewsDslBuilder().apply(builder)
    return dslBuilder.build()
}
