package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.model.Dashboard;
import org.jenjetsu.com.todo.model.DashboardUserInvite;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.repository.DashboardUserInviteRepository;
import org.jenjetsu.com.todo.repository.UserRepository;
import org.jenjetsu.com.todo.service.DashboardUserInviteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DashboardUserInviteServiceImpl extends SimpleJpaService<DashboardUserInvite, UUID> 
                                            implements DashboardUserInviteService{
    
    private final DashboardUserInviteRepository inviteRep;
    private final UserRepository userRep;
    private final DashboardRepository dashboardRep;

    public DashboardUserInviteServiceImpl(DashboardUserInviteRepository inviteRep,
                                          UserRepository userRep,
                                          DashboardRepository dashboardRep) {
        super(DashboardUserInvite.class);
        this.inviteRep = inviteRep;
        this.userRep = userRep;
        this.dashboardRep = dashboardRep;
    }

    @Override
    protected DashboardUserInvite createEntity(DashboardUserInvite raw) {
        User receiver = raw.getReceiver();
        if (receiver.getUserId() == null) {
            if (receiver.getUsername() == null && receiver.getEmail() == null) {
                throw new EntityValidateException("Request body doesn't contains user_id, username or email");
            }
            Optional<User> optionalUser = Optional.empty();
            if(receiver.getUsername() != null) {
                optionalUser = this.userRep.findByUsername(receiver.getUsername());
            }
            if(optionalUser.isEmpty() && receiver.getEmail() != null) {
                optionalUser = this.userRep.findByEmail(receiver.getEmail());
            }
            if(optionalUser.isEmpty()) {
                throw new EntityNotFoundException(format("User with username %s and email %s not found",
                                                         receiver.getUsername(),
                                                         receiver.getEmail()
                                                        ));
            }
            receiver = optionalUser.get();
        }
        if(this.dashboardRep.isUserInDashboard(raw.getDashboard().getDashboardId(), receiver.getUserId())) {
            throw new EntityValidateException(format("User %s with id %s already in dashboard",
                                                     receiver.getUsername(), receiver.getUserId()
                                                    )); 
        }
        raw.setReceiver(receiver);
        return super.createEntity(raw);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void completeInvite(UUID inviteId) {
        DashboardUserInvite invite = this.readById(inviteId);
        if(Instant.now().isBefore(invite.getExpirationDate().toInstant())) {
            Dashboard dashboard = invite.getDashboard();
            if(dashboard.getIsDeleted()) {
                throw new EntityValidateException(format("Dashboard %s marked as deleted", dashboard.getDashboardId()));
            }
            this.dashboardRep.addUserToDashboard(dashboard.getDashboardId(), invite.getReceiver().getUserId());
            this.inviteRep.deleteById(inviteId);
        } else {
            throw new EntityValidateException(format("Invite %s is expired", inviteId));
        }
    }
}
