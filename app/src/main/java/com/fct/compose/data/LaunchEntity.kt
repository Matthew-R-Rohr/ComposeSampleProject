package com.fct.compose.data

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fct.compose.R
import com.fct.compose.extensions.toDateFromUnix
import com.fct.compose.extensions.toMonthDayYearString
import kotlinx.parcelize.Parcelize

/**
 * Launch Entity Model
 */
@Parcelize
@Entity(tableName = "launch_entity")
data class LaunchEntity(
    @PrimaryKey(autoGenerate = false) val flightNumber: Int,
    val name: String,
    val missionId: String,
    val dateUnix: Long,
    val details: String?,
    val upcoming: Boolean,
    val success: Boolean?,
    val rocketId: String?,
    val missionPatchLarge: String?,
    val missionPatchSmall: String?,
    val redditUrl: String?,
    val articleUrl: String?,
    val wikiUrl: String?,
    val webCastUrl: String?,
    val flickrImages: List<String>? = emptyList(),
) : Parcelable

/**
 * Generate a success message if this launch was in the past or a future message
 */
fun LaunchEntity.generateSuccessMessage(context: Context): String = when {
    this.upcoming -> context.getString(R.string.launch_upcoming)
    this.success == true -> context.getString(R.string.launch_success)
    else -> context.getString(R.string.launch_fail)
}

/**
 * Returns true if this [LaunchEntity] has an url link
 */
fun LaunchEntity.hasUrlLinks() = !redditUrl.isNullOrEmpty() || !articleUrl.isNullOrEmpty() && !wikiUrl.isNullOrEmpty() && !webCastUrl.isNullOrEmpty()

/**
 * Generate a meta data string for this [LaunchEntity]
 *
 * Example: Flight 123 • Successful • 09/29/2013
 */
fun LaunchEntity.generateMetaDataString(context: Context): String {

    // flight number
    val flight = context.getString(R.string.details_flight_number, this.flightNumber)

    // if this was a successful or future launch
    val success = this.generateSuccessMessage(context)

    // date of launch
    val date = this.dateUnix
        .toDateFromUnix()
        .toMonthDayYearString()

    return listOf(flight, success, date)
        .joinToString(" • ")
}

/**
 * Generates a sample object to be used when building out compose UI
 */
fun getComposePreviewLaunchEntity(name: String = "Starlink Launch") = LaunchEntity(
    flightNumber = -1,
    name = name,
    missionId = "1234567",
    dateUnix = 1612137600,
    details = "Axiom Mission 1 (or Ax-1) is a planned SpaceX Crew Dragon mission to the International Space Station (ISS), operated by SpaceX on behalf of Axiom Space. The flight will launch no earlier than 31 March 2022 and send four people to the ISS for an eight-day stay",
    upcoming = false,
    success = true,
    rocketId = "54321",
    missionPatchLarge = "https://imgur.com/573IfGk.png",
    missionPatchSmall = "https://imgur.com/BrW201S.png",
    redditUrl = "https://www.reddit.com/r/spacex/comments/jhu37i/starlink_general_discussion_and_deployment_thread/",
    articleUrl = "https://spaceflightnow.com/2021/01/08/spacex-deploys-turkish-satellite-in-first-launch-of-2021/",
    wikiUrl = "https://en.wikipedia.org/wiki/T%C3%BCrksat_5A",
    webCastUrl = "https://youtu.be/9I0UYXVqIn8",
    flickrImages = listOf(
        "https://live.staticflickr.com/65535/50814482042_476d87b020_o.jpg",
        "https://live.staticflickr.com/65535/50813630408_d98c2215f8_o.jpg",
        "https://live.staticflickr.com/65535/50814379121_8834b5362d_o.jpg",
        "https://live.staticflickr.com/65535/50814379056_f032a23955_o.jpg"
    )
)

/**
 * Convert a single [LaunchEntity] into a List
 */
fun LaunchEntity.asList(): List<LaunchEntity> = listOf(this)