package com.moath.wordlebot.repository;


import com.moath.wordlebot.model.Player;
import com.moath.wordlebot.model.WordleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WordleResultRepository extends JpaRepository<WordleResult, Long> {

    @Query("SELECT w FROM WordleResult w WHERE CAST(w.date AS date) = CAST(:date AS date)")
    List<WordleResult> findByDateOnly(@Param("date") LocalDateTime date);

    @Query("SELECT w FROM WordleResult w WHERE w.player = :player AND " +
            "CAST(w.date AS date) = CAST(:date AS date)")
    List<WordleResult> findAllByPlayerAndDateOnly(
            @Param("player") Player player,
            @Param("date") LocalDateTime date
    );
}