package xast.spring.taskmanager;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemTaskRepository implements TaskRepository {

    private final List<Task> tasks = new LinkedList<>();

    @Override
    public List<Task> findAll() {
        return this.tasks;
    }

    @Override
    public void save(Task task) {
        this.tasks.add(task);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return this.tasks.stream()
                .filter(task -> task.id().equals(id))
                .findFirst();
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
