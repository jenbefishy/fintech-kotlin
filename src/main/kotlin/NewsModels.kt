import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coords(
    val lat: Double,
    val lon: Double,
)

@Serializable
data class Place(
    val id: Int,
    val title: String,
    val slug: String,
    val address: String,
    val phone: String,
    @SerialName("is_stub")
    val isStub: Boolean,
    @SerialName("site_url")
    val siteUrl: String,
    val coords: Coords,
    val subway: String,
    @SerialName("is_closed")
    val isClosed: Boolean,
    val location: String,
)

@Serializable
data class News(
    val id: Int,
    val title: String,
    val place: Place?,
    val description: String,
    @SerialName("site_url")
    val siteUrl: String,
    @SerialName("favorites_count")
    val favoritesCount: Int,
    @SerialName("comments_count")
    val commentsCount: Int,
    @SerialName("publication_date")
    val publicationDate: Long,
) {
    val rating: Double by lazy {
        1.0 / (1 + kotlin.math.exp(-(favoritesCount.toDouble() / (commentsCount + 1).toDouble())))
    }
}

@Serializable
data class NewsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<News>,
)
