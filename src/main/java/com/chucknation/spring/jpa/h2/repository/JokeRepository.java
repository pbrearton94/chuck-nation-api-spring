package com.chucknation.spring.jpa.h2.repository;

import java.util.List;

import com.chucknation.spring.jpa.h2.model.Joke;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeRepository extends JpaRepository<Joke, Long> {
  List<Joke> findByPublished(boolean published);

  List<Joke> findByTitleContaining(String title);
}
