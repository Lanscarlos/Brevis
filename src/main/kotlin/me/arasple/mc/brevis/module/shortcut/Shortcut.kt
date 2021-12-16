package me.arasple.mc.brevis.module.shortcut

import me.arasple.mc.brevis.api.BrevisAPI
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author Arasple
 * @date 2021/2/25 11:44
 */
data class Shortcut(
    val global: Boolean = false,
    val courses: List<Track>,
    private val reaction: String
) {

    fun isGlobal(): Boolean {
        return global
    }

    fun match(tracks: List<Track>): Boolean {
        if (tracks.size != courses.size) {
            return false
        }
        tracks.forEachIndexed { index, track ->
            val course = courses[index]
            if (course != track) {
                return false
            }
        }
        return true
    }

    fun react(player: Player): CompletableFuture<Any?> {
        return BrevisAPI.eval(player, reaction)
    }

}