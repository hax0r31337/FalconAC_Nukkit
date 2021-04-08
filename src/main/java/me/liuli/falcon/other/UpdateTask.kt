package me.liuli.falcon.other

import cn.nukkit.scheduler.Task
import me.liuli.falcon.manage.CheckManager

class UpdateTask(private val checkManager: CheckManager) : Task() {
    override fun onRun(tick: Int) {
        checkManager.handleUpdate()
    }
}