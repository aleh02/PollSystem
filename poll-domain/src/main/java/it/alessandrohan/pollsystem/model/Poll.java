package it.alessandrohan.pollsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "polls",
        indexes = {
            @Index(name = "idx_polls_status_expires_at", columnList = "status, expires_at"),
            @Index(name = "idx_polls_status_winner_notified_at", columnList = "status, winner_notified_at")
        })
public class Poll extends BaseEntity {

    @NotBlank
    @Column(name = "question", nullable = false, length = 500)
    private String question;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PollStatus status = PollStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_option_id")
    private PollOption winnerOption;

    @Column(name = "winner_percent", precision = 5, scale = 2)
    private BigDecimal winnerPercent;

    @Column(name = "winner_notified_at")
    private Instant winnerNotifiedAt;
}
