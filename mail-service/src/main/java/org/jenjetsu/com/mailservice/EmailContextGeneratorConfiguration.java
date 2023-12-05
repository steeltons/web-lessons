package org.jenjetsu.com.mailservice;

import static java.lang.String.format;
import java.util.Map;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.function.Function;

import org.jenjetsu.com.mailservice.EmailContext.EmailContextBuilder;
import static org.jenjetsu.com.mailservice.EmailContext.builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailContextGeneratorConfiguration {

    private final String FROM_EMAIL = "${spring.mail.username}";
    private final String LINK_EXPIRATION_TIME_KEY = "expirationTime";

    @Bean("TASK_NOTIFICATION")
    public Function<MailDTO, EmailContext> taskNotificationContext() {
        return (dto) -> this.abstractEmailContextBuilder(dto)
                            .subject("Task notification")
                            .template("taskNotificationShablon")
                            .build();
    }

    @Bean("TASK_INVITE")
    public Function<MailDTO, EmailContext> taskInviteContext() {
        return (dto) -> this.abstractEmailContextBuilder(dto)
                            .subject("Task invite")
                            .template("taskInviteShablon")
                            .build();
                            
    }

    @Bean("DASHBOARD_INVITE")
    public Function<MailDTO, EmailContext> dashboardInviteContext() {
        return (dto) -> {
            if(!dto.messageParams().containsKey(LINK_EXPIRATION_TIME_KEY)) {
               throw new IllegalArgumentException("No property linkExpirationTime in dto"); 
            }
            Map<String, Object> params = dto.messageParams();
            Long linkExpirationMillis = Long.valueOf(params.get(LINK_EXPIRATION_TIME_KEY).toString());
            String expirationHours = format("%d hours", MILLISECONDS.toHours(linkExpirationMillis));
            params.put(LINK_EXPIRATION_TIME_KEY, expirationHours);
            return this.abstractEmailContextBuilder(dto)
                       .subject("Dashboard invite")
                       .template("dashboardInviteShablon")
                       .build();
        };
    }

    private EmailContextBuilder abstractEmailContextBuilder(MailDTO dto) {
        return builder().to(dto.to())
                        .from(FROM_EMAIL)
                        .context(dto.messageParams());
    } 
}
