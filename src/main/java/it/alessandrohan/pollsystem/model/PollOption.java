package it.alessandrohan.pollsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "poll_options",
        indexes = {
                @Index(name = "idx_poll_options_poll_id", columnList = "poll_id")
        })
@NoArgsConstructor
public class PollOption extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @NotBlank
    @Column(name = "message", nullable = false, length = 200)
    private String message;
}
