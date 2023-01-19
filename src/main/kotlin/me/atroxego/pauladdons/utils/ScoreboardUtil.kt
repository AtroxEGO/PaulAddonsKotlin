package me.atroxego.pauladdons.utils

import PaulAddons.Companion.mc
import me.atroxego.pauladdons.utils.Utils.stripControlCodes
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam

object ScoreboardUtil {
    @JvmStatic
    fun cleanSB(scoreboard: String): String {
        return scoreboard.stripControlCodes().toCharArray().filter { it.code in 32..126 }.joinToString(separator = "")
    }

    var sidebarLines: List<String> = emptyList()

    fun fetchScoreboardLines(): List<String> {
        val scoreboard = mc.theWorld?.scoreboard ?: return emptyList()
        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return emptyList()
        val scores = scoreboard.getSortedScores(objective).filter { input: Score? ->
            input != null && input.playerName != null && !input.playerName
                .startsWith("#")
        }.take(15)
        return scores.map {
            ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(it.playerName), it.playerName)
        }.asReversed()
    }
}