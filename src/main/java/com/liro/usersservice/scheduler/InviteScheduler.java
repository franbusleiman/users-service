package com.liro.usersservice.scheduler;

import com.liro.usersservice.services.ScheduledService;
import com.liro.usersservice.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InviteScheduler {

    @Autowired
    ScheduledService scheduledService;

    @Scheduled(cron = "0 * * * * ?")
    public void scheduleDeleteOldInvites() {
        System.out.println("Iniciando eliminación de registros antiguos...");
        scheduledService.deleteInvite();
        System.out.println("Eliminación completada.");
    }

}
