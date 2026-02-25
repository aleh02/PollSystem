package it.alessandrohan.pollsystem.repository;

import it.alessandrohan.pollsystem.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByPollId(Long pollId);

    boolean existsByPollOptionId(Long optionId);

    Optional<Vote> findByPollIdAndUserId(Long pollId, Long userId);

    @Modifying
    @Query(value = """
            INSERT INTO votes (poll_id, user_id, option_id, voted_at, created_at, updated_at)
            VALUES (:pollId, :userId, :optionId, now(), now(), now())
            ON CONFLICT (poll_id, user_id)
            DO UPDATE SET
                option_id = EXCLUDED.option_id,
                voted_at = now(),
                updated_at = now()
        """, nativeQuery = true)
    void upsertVote(@Param("pollId") Long pollId,
                    @Param("userId") Long userId,
                    @Param("optionId") Long optionId);

    long countByPollId(Long pollId);
}