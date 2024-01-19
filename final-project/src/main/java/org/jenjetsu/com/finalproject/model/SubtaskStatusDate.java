package org.jenjetsu.com.finalproject.model;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "t_subtask_status_date")
public class SubtaskStatusDate {

    @EmbeddedId
    private SubtaskStatusDateKey keyId;
    @Getter @Setter
    private Date date;

    public SubtaskStatusDate() {
        this.keyId = new SubtaskStatusDateKey();
        this.date = null;
    }

    public SubtaskStatusDate(Subtask subtask, SubtaskStatus subtaskStatus, Date date) {
        this.keyId = new SubtaskStatusDateKey(subtask, subtaskStatus);
        this.date = date;
    }

    @Getter @Setter @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public class SubtaskStatusDateKey implements Serializable{
        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "subtask_id")
        private Subtask subtask;
        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "subtask_status_id")
        private SubtaskStatus subtaskStatus; 
    }

    public void setSubtask(Subtask subtask) {
        this.keyId.subtask = subtask;
    }

    public void setSubtaskStatus(SubtaskStatus subtaskStatus) {
        this.keyId.subtaskStatus = subtaskStatus;
    }

    public Subtask getSubtask() {
        return this.keyId.subtask;
    }

    public SubtaskStatus getSubtaskStatus() {
        return this.keyId.subtaskStatus;
    }

    public static SubtaskStatusDateBuilder builder() {
        return new SubtaskStatusDateBuilder();
    }

    public static class SubtaskStatusDateBuilder {
        private Subtask subtask;
        private SubtaskStatus subtaskStatus;
        private Date date;

        public SubtaskStatusDateBuilder subtask(Subtask subtask) {
            this.subtask = subtask;
            return this;
        }

        public SubtaskStatusDateBuilder subtaskStatus(SubtaskStatus subtaskStatus) {
            this.subtaskStatus = subtaskStatus;
            return this;
        }

        public SubtaskStatusDateBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public SubtaskStatusDate build() {
            return new SubtaskStatusDate(this.subtask, this.subtaskStatus, this.date);
        }
    }
}
