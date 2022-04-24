package com.bezkoder.spring.jpa.h2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bezkoder.spring.jpa.h2.model.Joke;

public interface JokeRepository extends JpaRepository<Joke, Long> {
  List<Joke> findByPublished(boolean published);

  List<Joke> findByTitleContaining(String title);
}
