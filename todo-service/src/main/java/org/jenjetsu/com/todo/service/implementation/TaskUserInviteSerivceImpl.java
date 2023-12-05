package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.broker.sender.MailMessageSender;
import org.jenjetsu.com.todo.dto.MailDTO;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.mailgenerator.MailDtoGenerator;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskUserInvite;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.repository.TaskUserInviteRepository;
import org.jenjetsu.com.todo.repository.UserRepository;
import org.jenjetsu.com.todo.service.TaskUserInviteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskUserInviteSerivceImpl extends SimpleJpaService<TaskUserInvite, UUID>
                                       implements TaskUserInviteService{
    
    private final TaskUserInviteRepository taskInviteRep;
    private final TaskRepository taskRep;
    private final UserRepository userRep;
    private final MailMessageSender messageSender;
    private final MailDtoGenerator<TaskUserInvite> mailGenerator;

    public TaskUserInviteSerivceImpl(TaskUserInviteRepository taskInviteRep,
                                     TaskRepository taskRep,
                                     MailMessageSender messageSender,
                                     MailDtoGenerator<TaskUserInvite> mailGenerator,
                                     UserRepository userRep) {
        super(TaskUserInvite.class, taskInviteRep);
        this.taskInviteRep = taskInviteRep;
        this.taskRep = taskRep;
        this.userRep = userRep;
        this.messageSender = messageSender;
        this.mailGenerator = mailGenerator;
    }

    @Override
    protected TaskUserInvite createEntity(TaskUserInvite raw) {
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
        if (this.taskRep.isUserInTask(receiver.getUserId(), raw.getTask().getTaskId())) {
            throw new EntityValidateException(format("User %s with id %s already in task",
                                                     receiver.getUsername(), receiver.getUserId()
                                                    )); 
        }
        raw.setReceiver(receiver);
        raw = super.createEntity(raw);
        raw.setInviter(this.userRep.findById(raw.getInviter().getUserId()).get());
        raw.setTask(this.taskRep.findById(raw.getTask().getTaskId()).get());
        MailDTO mail = this.mailGenerator.generateMail(raw);
        this.messageSender.sendMailMessage(mail);
        return raw;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void accpetInvite(UUID inviteId) {
        TaskUserInvite invite = this.readById(inviteId);
        Task task = invite.getTask();
        if(!task.getDeleted()) {
            UUID receiverId = invite.getReceiver().getUserId();
            UUID taskId = invite.getTask().getTaskId();
            UUID dashboardId = invite.getTask().getDashboard().getDashboardId();
            this.taskRep.addUserToTask(receiverId, taskId, dashboardId);
            this.taskInviteRep.deleteById(inviteId);
        } else {
            throw new EntityValidateException(format("Task %s marked as deleted", task.getTaskId()));
        }
    }


    @Override
    public void declineInvite(UUID inviteId) {
        if(this.existsById(inviteId)) {
            this.taskInviteRep.deleteById(inviteId);
        } else {
            throw new EntityNotFoundException(format("TaskUserInvite with id %s not exists", inviteId));
        }
    }


}
