package me.liuli.falcon.other;

import cn.nukkit.scheduler.Task;
import me.liuli.falcon.manage.CheckManager;

public class UpdateTask extends Task {
    private final CheckManager checkManager;

    public UpdateTask(CheckManager checkManager){
        this.checkManager=checkManager;
    }

    @Override
    public void onRun(int tick) {
        checkManager.handleUpdate();
    }
}
