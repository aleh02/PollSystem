package it.alessandrohan.pollsystem.repository;

public interface WinnerOption {
    Long getOptionId();

    String getOptionMessage();

    long getVotesCount();
}
