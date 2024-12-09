package xast.spring.taskmanager;

import java.util.*;

public interface TaskRepository {

    List<Task> findAll();

    void save(Task task);

    Optional<Task> findById(UUID id);
}
