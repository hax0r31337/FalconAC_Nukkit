package me.liuli.melhor.other

import cn.nukkit.scheduler.Task
import me.liuli.melhor.manage.CheckManager

class UpdateTask(private val checkManager: CheckManager) : Task() {
    override fun onRun(tick: Int) {
        checkManager.handleUpdate()
    }
}