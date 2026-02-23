package it.alessandrohan.pollsystem.repository;

import it.alessandrohan.pollsystem.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    boolean existsByIdAndPollId(Long optionId, Long pollId);

}
