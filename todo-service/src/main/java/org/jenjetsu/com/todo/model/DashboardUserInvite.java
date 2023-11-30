package org.jenjetsu.com.todo.model;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_dashboard_user_invite")
@Builder
public class DashboardUserInvite {
 
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID dashboardUserInviteId;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "dashboard_id")
    private Dashboard dashboard;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @Column(name = "expiration_date", nullable = false)
    private Timestamp expirationDate;

}
