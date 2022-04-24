package com.chucknation.spring.jpa.h2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.chucknation.spring.jpa.h2.controller.JokeController;
import com.chucknation.spring.jpa.h2.model.Joke;
import com.chucknation.spring.jpa.h2.repository.JokeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(JokeController.class)
public class JokeControllerTests {
    @MockBean
    private JokeRepository jokeRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateJoke() throws Exception {
        Joke joke = new Joke("Joke @WebMvcTest", "Joke description", true);

        mockMvc
                .perform(post("/api/jokes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joke)))
                .andExpect(status().isCreated())
                .andDo(print());

    }

    @Test
    void shouldReturnJokes() throws Exception {
        long id = 1L;
        Joke joke = new Joke(id, "Joke @WebMvcTest", "Joke description", true);
        when(jokeRepository.findById(id)).thenReturn(Optional.of(joke));

        mockMvc
                .perform(get("/api/jokes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id))
                .andExpect(jsonPath("$.title")
                        .value(joke.getTitle()))
                .andExpect(jsonPath("$.description")
                        .value(joke.getDescription()))
                .andExpect(jsonPath("$.published")
                        .value(joke.isPublished()))
                .andDo(print());

    }

    @Test
    void shouldReturnNotFoundJoke() throws Exception {
        long id = 1L;
        when(jokeRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc
                .perform(get("/api/jokes/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnListOfJokes() throws Exception {
        List<Joke> jokes = new ArrayList<>(
                Arrays.asList(new Joke(1, "Spring Boot @WebMvcTest 1", "Description 1", true),
                        new Joke(2, "Spring Boot @WebMvcTest 2", "Description 2", true),
                        new Joke(3, "Spring Boot @WebMvcTest 3", "Description 3", true)));
        when(jokeRepository.findAll()).thenReturn(jokes);
        mockMvc
                .perform(get("/api/jokes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()")
                        .value(jokes.size()))
                .andDo(print());
    }

    @Test
    void shouldReturnListOfJokesWithFilter() throws Exception {
        List<Joke> jokes = new ArrayList<>(
                Arrays.asList(new Joke(1, "Spring Boot @WebMvcTest", "Description 1", true),
                        new Joke(3, "Spring Boot Web MVC", "Description 3", true)));
        String title = "Boot";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);
        when(jokeRepository
                .findByTitleContaining(title))
                .thenReturn(jokes);
        mockMvc.perform(get("/api/jokes")
                .params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()")
                        .value(jokes.size()))
                .andDo(print());
        jokes = Collections.emptyList();
        when(jokeRepository.findByTitleContaining(title))
                .thenReturn(jokes);
        mockMvc.perform(get("/api/jokes")
                .params(paramsMap))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldReturnNoContentWhenFilter() throws Exception {
        String title = "Patrick";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);
        List<Joke> tutorials = Collections.emptyList();

        when(jokeRepository.findByTitleContaining(title))
                .thenReturn(tutorials);
        mockMvc
                .perform(get("/api/jokes")
                        .params(paramsMap))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldUpdateJoke() throws Exception {
        long id = 1L;
        Joke joke = new Joke(id, "Spring Boot @WebMvcTest", "Description", false);
        Joke updatedJoke = new Joke(id, "Updated", "Updated", true);
        when(jokeRepository
                .findById(id))
                .thenReturn(Optional.of(joke));
        when(jokeRepository.save(any(Joke.class)))
                .thenReturn(updatedJoke);
        mockMvc
                .perform(put("/api/jokes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedJoke)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value(updatedJoke.getTitle()))
                .andExpect(jsonPath("$.description")
                        .value(updatedJoke.getDescription()))
                .andExpect(jsonPath("$.published")
                        .value(updatedJoke.isPublished()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundUpdateJoke() throws Exception {
        long id = 1L;
        Joke updatedJoke = new Joke(id, "Updated", "Updated", true);
        when(jokeRepository.findById(id))
                .thenReturn(Optional.empty());
        when(jokeRepository.save(any(Joke.class)))
                .thenReturn(updatedJoke);
        mockMvc
                .perform(put("/api/jokes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedJoke)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldDeleteJoke() throws Exception {
        long id = 1L;
        doNothing().when(jokeRepository).deleteById(id);
        mockMvc.perform(delete("/api/jokes/{id}", id))
                .andExpect(status()
                        .isNoContent())
                .andDo(print());
    }

    @Test
    void shouldDeleteAllJokes() throws Exception {
        doNothing()
                .when(jokeRepository)
                .deleteAll();
        mockMvc
                .perform(delete("/api/jokes"))
                .andExpect(status()
                        .isNoContent())
                .andDo(print());
    }

}
