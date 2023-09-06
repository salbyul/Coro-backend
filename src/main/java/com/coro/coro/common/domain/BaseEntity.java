package com.coro.coro.common.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {

    @Column(name = "generated_date_time", updatable = false)
    private LocalDateTime generatedDateTime;
    @Column(name = "modified_date_time")
    private LocalDateTime modifiedDateTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.generatedDateTime = now;
        this.modifiedDateTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDateTime = LocalDateTime.now();
    }
}
