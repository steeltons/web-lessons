package org.jenjetsu.com.todo.mailgenerator;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.MailDTO;
import org.jenjetsu.com.todo.model.MailMessageType;
import org.jenjetsu.com.todo.model.TaskUserInvite;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

import lombok.SneakyThrows;

@Component
public class TaskInviteMailGenerator implements MailDtoGenerator<TaskUserInvite>  {
        
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @SneakyThrows
    public MailDTO generateMail(TaskUserInvite invite) {
        UriBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        Map<String, Object> params = new HashMap<>();
        params.put("receiverUsername", invite.getReceiver().getUsername());
        params.put("inviterUsername", invite.getInviter().getUsername());
        params.put("taskTitle", invite.getTask().getTitle());
        params.put("dashboardName", invite.getTask().getDashboard().getName());
        params.put("taskInformationLink", this.generateTaskInformationLink(uriBuilder, invite.getTask().getTaskId()));
        String inviteLink = this.generateInviteLink(uriBuilder, invite.getTaskUserInviteId());
        params.put("inviteLink", inviteLink);
        return MailDTO.builder()
                      .to(invite.getReceiver().getEmail())
                      .messageType(MailMessageType.TASK_INVITE)
                      .messageParams(params)
                      .build();
    }
    

    private String generateTaskInformationLink(UriBuilder uriBuilder, UUID taskId) throws MalformedURLException {
        return uriBuilder.path("/api/v1/tasks/" + taskId.toString())
                         .build()
                         .toURL()
                         .toString();
    }

    private String generateInviteLink(UriBuilder uriBuilder, UUID inviteId) throws MalformedURLException {
        return uriBuilder.path("/api/v1/tasks/invite/" + inviteId.toString())
                         .build()
                         .toURL()
                         .toString();
    }
}
