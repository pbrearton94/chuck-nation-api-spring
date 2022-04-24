package com.chucknation.spring.jpa.h2;

import java.util.List;

import com.chucknation.spring.jpa.h2.model.Joke;
import com.chucknation.spring.jpa.h2.repository.JokeRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JPAUnitTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    JokeRepository repository;

    @Test  
    public void shouldFindNoJokesIfRepositoryIsEmpty(){
        List<Joke> jokes = repository.findAll();
        assertThat(jokes).isEmpty();
    }

    @Test
    public void shouldStoreAJoke(){
        Joke joke = repository.save(new Joke("Joke title", "Joke description", true));
        assertThat(joke).hasFieldOrPropertyWithValue("title", "Joke title");
        assertThat(joke).hasFieldOrPropertyWithValue("description", "Joke description");
        assertThat(joke).hasFieldOrPropertyWithValue("published", true);
    }

    @Test
    public void shouldFindAllJokes(){
        Joke joke1 = new Joke("Joke#1", "Desc#1", true);
        entityManager.persist(joke1);
        Joke joke2 = new Joke("Joke#2", "Desc#2", true);
        entityManager.persist(joke2);
        Joke joke3 = new Joke("Joke#3", "Desc#3", true);
        entityManager.persist(joke3);
        Joke foundTutorial = repository.findById(joke2.getId()).get();
        assertThat(foundTutorial).isEqualTo(joke2);
    }

    @Test
    public void shouldFindPublishedJokes(){
        Joke joke1 = new Joke("Joke#1", "Desc#1", true);
        entityManager.persist(joke1);
        Joke joke2 = new Joke("Joke#2", "Desc#2", true);
        entityManager.persist(joke2);
        Joke joke3 = new Joke("Joke#3", "Desc#3", true);
        entityManager.persist(joke3);
        List<Joke> jokes = repository.findByPublished(true);
        assertThat(jokes).hasSize(3).contains(joke1, joke2, joke3);
    }

    @Test
    public void shouldFindJokesByTitleContainingString(){
        Joke joke1 = new Joke("Joke#1", "Desc#1", true);
        entityManager.persist(joke1);
        Joke joke2 = new Joke("Joke#2", "Desc#2", false);
        entityManager.persist(joke2);
        Joke joke3 = new Joke("Joke#3", "Desc#3", true);
        entityManager.persist(joke3);
        List<Joke> jokes = repository.findByTitleContaining("#1");
        assertThat(jokes).hasSize(1).contains(joke1);
    }

    @Test
    public void shouldUpdateJokeById(){
        Joke joke1 = new Joke("Joke#1", "Desc#1", true);
        entityManager.persist(joke1);
        Joke joke2 = new Joke("Joke#2", "Desc#2", true);
        entityManager.persist(joke2);
        Joke joke3 = new Joke("Joke#3", "Desc#3", true);
        entityManager.persist(joke3);
        Joke updatedJoke = new Joke("updated joke#2", "updated desc#2", false);
        Joke joke = repository.findById(joke2.getId()).get();

        joke.setTitle(updatedJoke.getTitle());
        joke.setDescription(updatedJoke.getDescription());
        joke.setPublished(updatedJoke.isPublished());
        repository.save(joke);
        Joke checkJoke = repository.findById(joke2.getId()).get();

        //assertThat(checkJoke.getId()).isEqualTo(updatedJoke.getId());
        assertThat(checkJoke.getTitle()).isEqualTo(updatedJoke.getTitle());
        assertThat(checkJoke.getDescription()).isEqualTo(updatedJoke.getDescription());
        assertThat(checkJoke.isPublished()).isEqualTo(updatedJoke.isPublished());
    }

    @Test
    public void shouldDeleteJokeById(){
        Joke joke1 = new Joke("Joke#1", "Desc#1", true);
        entityManager.persist(joke1);
        Joke joke2 = new Joke("Joke#2", "Desc#2", true);
        entityManager.persist(joke2);
        Joke joke3 = new Joke("Joke#3", "Desc#3", true);
        entityManager.persist(joke3);
        repository.deleteById(joke2.getId());
        List<Joke> jokes = repository.findAll();
        assertThat(jokes).hasSize(2).contains(joke1, joke3);
    }

    @Test
    public void shouldDeleteAllJokes(){
        Joke joke1 = new Joke("Joke#1", "Desc#1", true);
        entityManager.persist(joke1);
        Joke joke2 = new Joke("Joke#2", "Desc#2", true);
        entityManager.persist(joke2);
        Joke joke3 = new Joke("Joke#3", "Desc#3", true);
        entityManager.persist(joke3);
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();
    }

}
