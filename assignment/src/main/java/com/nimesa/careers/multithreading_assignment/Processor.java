package com.nimesa.careers.multithreading_assignment;

import java.util.*;
import java.util.concurrent.*;

/**
 * The Processor class represents a task processor that executes a queue of task requests.
 * It uses a priority queue to prioritize the tasks based on their priority.
 * Each task is executed by a separate thread using an executor service.
 * The Processor class also keeps track of the task responses and shuts down the executor services when all tasks are completed.
 */
public class Processor {

    private final Map<UserTaskType, ExecutorService> userTaskTypeExecutors = new ConcurrentHashMap<>();
    private final Queue<TaskRequest> queue;

    Processor(TaskRequest taskRequest) {
        this();
        queue.offer(taskRequest);
    }

    Processor(List<TaskRequest> taskRequests) {
        this();
        for (TaskRequest request : taskRequests) {
            queue.offer(request);
        }
    }

    Processor() {
        this.queue = new PriorityBlockingQueue<>(11, Comparator.comparing(TaskRequest::getPriority).reversed());
    }

    public List<TaskResponse> execute() throws InterruptedException {
        List<Future<TaskResponse>> futures = new ArrayList<>();
        for (TaskRequest taskRequest : queue) {
            UserTaskType userTaskType = new UserTaskType(taskRequest.getSubmittedBy(), taskRequest.getType());
            ExecutorService executor = userTaskTypeExecutors.computeIfAbsent(userTaskType,
                    k -> Executors.newSingleThreadExecutor());
            System.out.println("Starting Task " + taskRequest.getId());
            Callable<TaskResponse> callable = new Task(taskRequest);
            Future<TaskResponse> future = executor.submit(callable);
            futures.add(future);
        }

        List<TaskResponse> taskResponses = new ArrayList<>();
        for (Future<TaskResponse> future : futures) {
            try {
                TaskResponse response = future.get();
                System.out.println("Completed Task " + response.getId() + " With Status " + response.getStatus());
                taskResponses.add(response);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        userTaskTypeExecutors.values().forEach(ExecutorService::shutdown);

        return taskResponses;
    }

    private static class UserTaskType {
        private final String userId;
        private final TaskType taskType;

        UserTaskType(String userId, TaskType taskType) {
            this.userId = userId;
            this.taskType = taskType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            UserTaskType that = (UserTaskType) obj;
            return Objects.equals(userId, that.userId) &&
                    Objects.equals(taskType, that.taskType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, taskType);
        }
    }
}