package it.alessandrohan.pollsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "votes",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_votes_poll_user", columnNames = {"poll_id", "user_id"})
        },
        indexes = {
            @Index(name = "idx_votes_poll_option", columnList = "poll_id, option_id")
        })
public class Vote extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private PollOption pollOption;

    @NotNull
    @Column(name = "voted_at", nullable = false)
    private Instant votedAt;

    @PrePersist
    protected void onVoteCreate() {
        this.votedAt = Instant.now();
    }
}
