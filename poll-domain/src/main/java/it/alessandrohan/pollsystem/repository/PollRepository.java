package it.alessandrohan.pollsystem.repository;

import it.alessandrohan.pollsystem.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    @Override
    @EntityGraph(attributePaths = "owner")
    Page<Poll> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "owner")
    Page<Poll> findByQuestionContainingIgnoreCase(String question, Pageable pageable);

    @EntityGraph(attributePaths = "owner")
    Optional<Poll> findWithOwnerById(Long id);

    @Modifying
    @Query(value = """
            UPDATE polls
            SET updated_at = now()
            WHERE id = :pollId
                AND status = 'ACTIVE'
                AND expires_at > now()
            """, nativeQuery = true)
    int casVoteAllowed(@Param("pollId") Long pollId);

    @Query(value = """
                SELECT owner_id FROM polls
                WHERE id = :pollId
            """, nativeQuery = true)
    Long findOwnerId(@Param("pollId") Long pollId);

    @Query(value = """
            SELECT id
            FROM polls
            WHERE status = 'ACTIVE'
                AND expires_at <= now()
            ORDER BY expires_at ASC, id ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> findDuePollIds(@Param("limit") int limit);

    @Modifying
    @Query(value = """
            UPDATE polls
            SET status = 'EXPIRED',
                updated_at = now()
            WHERE id = :pollId
                AND status = 'ACTIVE'
                AND expires_at <= now()
            """, nativeQuery = true)
    int expireIfActiveAndDue(@Param("pollId") Long pollId);

    @Query(value = """
            SELECT id
            FROM polls
            WHERE status = 'EXPIRED'
              AND winner_notified_at IS NULL
            ORDER BY expires_at ASC, id ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> findExpiredNotNotifiedPollIds(@Param("limit") int limit);

    @EntityGraph(attributePaths = {"owner", "winnerOption"})
    Optional<Poll> findWithOwnerAndWinnerOptionById(Long id);

    @Modifying
    @Query(value = """
            UPDATE polls
            SET winner_notified_at = now(), 
                updated_at = now()
            WHERE id = :pollId
                AND winner_notified_at IS NULL
            """, nativeQuery = true)
    int markWinnerNotified(@Param("pollId") Long pollId);

}
