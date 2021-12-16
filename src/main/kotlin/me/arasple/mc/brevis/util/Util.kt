package me.arasple.mc.brevis.util

import me.arasple.mc.brevis.module.shortcut.Session
import org.bukkit.entity.Player
import java.io.File

/**
 * @author Arasple
 * @date 2021/2/25 12:02
 */
fun Player.getSession(): Session {
    return Session.get(this)
}

/**
 * 过滤有效称号文件
 *
 * @author Arasple
 * */
fun getFiles(file : File, filter : String = "#") : List<File> {
    return mutableListOf<File>().apply {
        if(file.isDirectory) {
            file.listFiles()?.forEach {
                addAll(getFiles(it))
            }
        } else if (!file.name.startsWith(filter)) {
            add(file)
        }
    }
}