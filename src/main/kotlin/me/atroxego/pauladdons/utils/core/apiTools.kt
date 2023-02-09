package me.atroxego.pauladdons.utils.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRelease(
    @SerialName("tag_name")
    val tagName: String,
    val uploader: String,
    val body: String
)
@Serializable
data class GithubAsset(
    val name: String,
    @SerialName("browser_download_url")
    val downloadUrl: String,
    val size: Long,
    @SerialName("download_count")
    val downloadCount: Long,
    val uploader: GithubUser
)
@Serializable
data class GithubUser(
    @SerialName("login")
    val username: String
)