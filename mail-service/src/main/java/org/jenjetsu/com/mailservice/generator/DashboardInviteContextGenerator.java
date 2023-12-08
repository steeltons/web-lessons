package org.jenjetsu.com.mailservice.generator;

import static java.lang.String.format;
import java.util.Map;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.jenjetsu.com.mailservice.model.EmailContext;
import org.jenjetsu.com.mailservice.model.MailDTO;
import org.springframework.stereotype.Component;

@Component("DASHBOARD_INVITE")
public class DashboardInviteContextGenerator extends ContextGenerator{

    private final String LINK_EXPIRATION_TIME_KEY = "expirationTime";

    public DashboardInviteContextGenerator() {
        super("Dashboard invite", "dashboardInvite");
    }
    
    @Override
    public EmailContext generateContext(MailDTO dto) {
        if(!dto.messageParams().containsKey(LINK_EXPIRATION_TIME_KEY)) {
            throw new IllegalArgumentException("No property linkExpirationTime in dto"); 
         }
         Map<String, Object> params = dto.messageParams();
         Long linkExpirationMillis = Long.valueOf(params.get(LINK_EXPIRATION_TIME_KEY).toString());
         String expirationHours = format("%d hours", MILLISECONDS.toHours(linkExpirationMillis));
         params.put(LINK_EXPIRATION_TIME_KEY, expirationHours);
        return super.prepare(dto)
                    .build();
    }
    
}
