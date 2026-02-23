package it.alessandrohan.pollsystem.repository;

import it.alessandrohan.pollsystem.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
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

}
