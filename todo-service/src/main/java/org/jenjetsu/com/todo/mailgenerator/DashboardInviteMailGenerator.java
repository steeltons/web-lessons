package org.jenjetsu.com.todo.mailgenerator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.jenjetsu.com.todo.dto.MailDTO;
import org.jenjetsu.com.todo.model.DashboardUserInvite;
import org.jenjetsu.com.todo.model.MailMessageType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class DashboardInviteMailGenerator implements MailDtoGenerator<DashboardUserInvite> {

    private final Duration INVITE_EXPIRATION_TIME = null;

    @Override
    public MailDTO generateMail(DashboardUserInvite from) {
        Map<String, Object> params = new HashMap<>();
        params.put("receiverUsername", from.getReceiver().getEmail());
        params.put("creatorUsername", from.getInviter().getUsername());
        params.put("dashboardName", from.getDashboard().getName());
        String inviteLink = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(7777)
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
