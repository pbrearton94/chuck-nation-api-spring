package com.bezkoder.spring.jpa.h2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.jpa.h2.model.Joke;
import com.bezkoder.spring.jpa.h2.repository.JokeRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class JokeTutorial {

	@Autowired
	JokeRepository jokeRepository;

	@GetMapping("/jokes")
	public ResponseEntity<List<Joke>> getAllJokes(@RequestParam(required = false) String title) {
		try {
			List<Joke> jokes = new ArrayList<Joke>();

			if (title == null)
				jokeRepository.findAll().forEach(jokes::add);
			else
				jokeRepository.findByTitleContaining(title).forEach(jokes::add);

			if (jokes.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(jokes, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/jokes/{id}")
	public ResponseEntity<Joke> getJokeById(@PathVariable("id") long id) {
		Optional<Joke> jokeData = jokeRepository.findById(id);

		if (jokeData.isPresent()) {
			return new ResponseEntity<>(jokeData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/jokes")
	public ResponseEntity<Joke> createJoke(@RequestBody Joke tutorial) {
		try {
			Joke _joke = jokeRepository
					.save(new Joke(tutorial.getTitle(), tutorial.getDescription(), false));
			return new ResponseEntity<>(_joke, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/jokes/{id}")
	public ResponseEntity<Joke> updateJoke(@PathVariable("id") long id, @RequestBody Joke tutorial) {
		Optional<Joke> jokeData = jokeRepository.findById(id);

		if (jokeData.isPresent()) {
			Joke _joke = jokeData.get();
			_joke.setTitle(tutorial.getTitle());
			_joke.setDescription(tutorial.getDescription());
			_joke.setPublished(tutorial.isPublished());
			return new ResponseEntity<>(jokeRepository.save(_joke), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/jokes/{id}")
	public ResponseEntity<HttpStatus> deleteJoke(@PathVariable("id") long id) {
		try {
			jokeRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/jokes")
	public ResponseEntity<HttpStatus> deleteAllJokes() {
		try {
			jokeRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/jokes/published")
	public ResponseEntity<List<Joke>> findByPublished() {
		try {
			List<Joke> jokes = jokeRepository.findByPublished(true);

			if (jokes.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(jokes, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
