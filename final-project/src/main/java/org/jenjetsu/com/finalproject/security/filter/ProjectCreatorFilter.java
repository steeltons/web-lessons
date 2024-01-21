package org.jenjetsu.com.finalproject.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.IOUtils;
import org.jenjetsu.com.finalproject.config.HttpRequestWrapper;
import org.jenjetsu.com.finalproject.repository.ProjectRepository;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectCreatorFilter extends OncePerRequestFilter {

    private final String CREATOR_AUTHORITY = "ROLE_CREATOR";
    private final String MEMBER_AUTHORITY = "ROLE_MEMBER";

    SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProjectRepository projectRep;
    private final TaskRepository taskRep;
    private final SubtaskRepository subtaskRep;

    private final ConcurrentMap<Entry<UUID, UUID>, List<GrantedAuthority>> userProjectAuthority = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {
        Authentication auth = strategy.getContext().getAuthentication();
        if (auth != null) {
            HttpRequestWrapper wrapper = new HttpRequestWrapper(request);
            Collection<GrantedAuthority> authorities = this.getAuthority(auth, wrapper);
            auth.getAuthorities().forEach(authorities::add);
            BearerTokenAuthentication token = (BearerTokenAuthentication) auth;
            BearerTokenAuthentication newToken = new BearerTokenAuthentication(token.getToken(), authorities);
            newToken.setAuthenticated(true);
            newToken.setUserId(token.getUserId());
            strategy.getContext().setAuthentication(newToken);
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private List<GrantedAuthority> getAuthority(Authentication auth, HttpServletRequest req) throws IOException{
        List<GrantedAuthority> list = new ArrayList<>();
        if (isUserAdmin(auth)) {
            list.add(new SimpleGrantedAuthority(CREATOR_AUTHORITY));
            list.add(new SimpleGrantedAuthority(MEMBER_AUTHORITY));
            return list;
        } 
        UUID projectId = this.readProjectIdFromRequest(req);
        if(projectId != null) {
            UUID userId = ((BearerTokenAuthentication) auth).getUserId();
            Entry<UUID, UUID> key = Map.entry(userId, projectId);
            if (this.userProjectAuthority.containsKey(key)) {
                if (this.projectRep.isUserProjectCreator(userId, projectId)) {
                    list.add(new SimpleGrantedAuthority(CREATOR_AUTHORITY));
                    list.add(new SimpleGrantedAuthority(MEMBER_AUTHORITY));
                } else if (this.projectRep.isUserInProject(userId, projectId)) {
                    list.add(new SimpleGrantedAuthority(MEMBER_AUTHORITY));
                }
            } 
            if (!list.isEmpty()) {
                this.userProjectAuthority.put(key, list);
            }
        } 
        return list;
    }  
    
    private UUID readProjectIdFromRequest(HttpServletRequest req) throws IOException{
        JsonNode requestBody = objectMapper.readTree(IOUtils.toByteArray(req.getInputStream()));
        UUID projectId = null;
        if (requestBody.has("project_id")) {
            projectId = UUID.fromString(requestBody.get("project_id").asText());
        } else if (requestBody.has("task_id")) {
            UUID taskId = UUID.fromString(requestBody.get("task_id").asText());
            projectId = this.taskRep.findProjectIdByTaskId(taskId);
        } else if (requestBody.has("subtask_id")) {
            UUID subtaskId = UUID.fromString(requestBody.get("subtask_id").asText());
            projectId = this.subtaskRep.findProjectIdBySubtaskId(subtaskId);
        }
        return projectId;
    }
    
    private boolean isUserAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter((role) -> role.equals("ROLE_MODERATOR"))
                    .findFirst().isPresent();
    }
}
