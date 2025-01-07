package com.liro.usersservice.services;

import com.liro.usersservice.domain.enums.State;
import com.liro.usersservice.domain.model.UserInvite;
import com.liro.usersservice.persistance.UserInviteRepository;
import com.liro.usersservice.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledService {

    @Autowired
    private UserInviteRepository userInviteRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void deleteInvite() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        List<UserInvite> oldInvites = userInviteRepository.findBycreatedAtBefore(twoWeeksAgo);

        for (UserInvite oldInvite : oldInvites) {
            oldInvite.getUser().setState(State.LOCAL);
            userRepository.save(oldInvite.getUser());
        }

        userInviteRepository.deleteAll(oldInvites);



        System.out.println("Registros eliminados: " + oldInvites.size());

    }

}
