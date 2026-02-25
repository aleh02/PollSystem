package it.alessandrohan.pollsystem.repository;

import it.alessandrohan.pollsystem.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    boolean existsByIdAndPollId(Long optionId, Long pollId);

    List<PollOption> findAllByPollId(Long pollId);

    @Query(value = """
        SELECT v.option_id AS optionId,
            o.message AS optionMessage,
            COUNT(*) AS voteCount
        FROM votes v
        JOIN poll_options o ON o.id = v.option_id
        WHERE v.poll_id = :pollId
        GROUP BY v.option_id, o.message, o.created_at
        ORDER BY voteCount DESC, o.created_at ASC
        LIMIT 1
    """, nativeQuery = true)
    Optional<WinnerOption> findWinnerForPoll(@Param("pollId") Long pollId);

}
