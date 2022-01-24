package mnsky;

import mnsky.task.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskListTest {
    class StorageStub extends Storage {
        public StorageStub() {
            super("");
        }

        @Override
        public ArrayList<String> readFromDataFile() {
            return new ArrayList<>();
        }
    }

    class UiStub extends Ui {
        public UiStub() {
            super();
        }
    }

    @Test
    public void testAddTask() {
        TaskList taskList = new TaskList(new UiStub(), new StorageStub());
        Task newTask = taskList.addTask("ggg ffff hh");

        assertEquals(newTask.getName(), "ggg ffff hh");
        assertEquals(taskList.getTaskList().size(), 1);
        assertEquals(taskList.getTaskList().get(0), newTask);
    }
}
