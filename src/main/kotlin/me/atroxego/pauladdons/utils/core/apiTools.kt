package me.atroxego.pauladdons.utils.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Taken from Skytils under GNU Affero General Public License v3.0
 * Modified
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 * @author Skytils
 */
@Serializable
data class GithubRelease(
    @SerialName("tag_name")
    val tagName: String,
    val uploader: String,
    val body: String
)