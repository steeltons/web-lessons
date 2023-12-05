package org.jenjetsu.com.todo.mailgenerator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Hibernate;
import org.jenjetsu.com.todo.dto.MailDTO;
import org.jenjetsu.com.todo.model.DashboardUserInvite;
import org.jenjetsu.com.todo.model.MailMessageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class DashboardInviteMailGenerator implements MailDtoGenerator<DashboardUserInvite> {

    private final Duration INVITE_EXPIRATION_TIME;

    public DashboardInviteMailGenerator(@Value("${spring.application.dashboard.invite-expiration-time-hours}") String time) {
        this.INVITE_EXPIRATION_TIME = Duration.ofHours(Long.parseLong(time));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public MailDTO generateMail(DashboardUserInvite from) {
        Map<String, Object> params = new HashMap<>();
        params.put("receiverUsername", from.getReceiver().getUsername());
        params.put("creatorUsername", from.getInviter().getUsername());
        params.put("dashboardName", from.getDashboard().getName());
        String inviteLink = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/api/v1/dashboards/invite/" + from.getDashboardUserInviteId().toString())
            .toUriString();
        params.put("inviteLink", inviteLink);
        params.put("dashboardName", from.getDashboard().getName());
        params.put("expirationTime", INVITE_EXPIRATION_TIME.toMillis());
        return MailDTO.builder()
                      .to(from.getReceiver().getEmail())
                      .messageType(MailMessageType.DASHBOARD_INVITE)
                      .messageParams(params)
                      .build();
    }
    
}
