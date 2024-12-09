package xast.spring.taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemTaskRepository inMemTaskRepository;

    @AfterEach
    void tearDown() {
        this.inMemTaskRepository.getTasks().clear();
    }


    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/tasks");
        this.inMemTaskRepository.getTasks()
                .addAll(List.of(new Task(UUID.fromString("71117396-8694-11ed-9ef6-77042ee83937"),
                        "Первая задача", false),
                        new Task(UUID.fromString("7172d834-8694-11ed-8669-d7b17d45fba8"),
                                "Вторая задача", true)));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                        "id": "71117396-8694-11ed-9ef6-77042ee83937",
                                        "details": "Первая задача",
                                        "completed": false
                                    },
                                    {
                                        "id": "7172d834-8694-11ed-8669-d7b17d45fba8",
                                        "details": "Вторая задача",
                                        "completed": true
                                    }
                                ]
                                """)
                );
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details": "Третья задача"
                        }
                        """);
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                  {
                                      "details": "Третья задача",
                                      "completed": false
                                  }
                                  """),
                        jsonPath("$.id").exists()
                );
        assertEquals(1, this.inMemTaskRepository.getTasks().size());
        final var task = this.inMemTaskRepository.getTasks().get(0);
        assertNotNull(task.id());
        assertEquals("Третья задача", task.details());
        assertFalse(task.completed());
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "ru")
                .content("""
                        {
                            "details": null
                        }
                        """);
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                  {
                                      "errors": ["Описание задачи должно быть указано"]
                                  }
                                  """, true)
                        );
        assertTrue(this.inMemTaskRepository.getTasks().isEmpty());
    }
}