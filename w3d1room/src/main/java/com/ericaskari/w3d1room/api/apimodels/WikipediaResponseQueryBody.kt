data class WikipediaResponseQueryBody(
    val query: WikipediaResponseQuery,
) {
}

data class WikipediaResponseQuery(
    val searchinfo: WikipediaResponseSearchInfo,
) {
}

data class WikipediaResponseSearchInfo(
    val totalhits: Int,
) {
}
