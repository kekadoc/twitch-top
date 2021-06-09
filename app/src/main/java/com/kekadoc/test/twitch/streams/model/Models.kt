package com.kekadoc.test.twitch.streams.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Feedback(val stars: Int, val message: String)

@Serializable
data class TwitchTopResponse(
    @SerializedName("_total")
    val total: Long = 0,
    @SerializedName("top")
    val top: List<TwitchTopResponseElement> = emptyList()

)

@Serializable
data class TwitchTopResponseElement(
    @SerializedName("channels")
    val channels: Int = 0,
    @SerializedName("viewers")
    val viewers: Int = 0,
    @SerializedName("game")
    val game: Game
)

@Serializable
data class Game(
    @SerializedName("_id")
    val id: Long,
    @SerializedName("box")
    val box: Box,
    @SerializedName("giantbomb_id")
    val giantbomb_id: Int,
    @SerializedName("logo")
    val logo: Logo,
    @SerializedName("name")
    val name: String
)

@Serializable
data class Logo(
    @SerializedName("large")
    val large: String,
    @SerializedName("medium")
    val medium: String,
    @SerializedName("small")
    val small: String,
    @SerializedName("template")
    val template: String,
)

@Serializable
data class Box(
    @SerializedName("large")
    val large: String,
    @SerializedName("medium")
    val medium: String,
    @SerializedName("small")
    val small: String,
    @SerializedName("template")
    val template: String,
)