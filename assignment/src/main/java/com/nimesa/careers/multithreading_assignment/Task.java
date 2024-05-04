package com.nimesa.careers.multithreading_assignment;
import java.util.concurrent.Callable;

public class Task implements Callable<TaskResponse> {
    private final TaskRequest taskRequest;
    Task(TaskRequest taskRequest){
        this.taskRequest = taskRequest;
    }

    public TaskResponse run() throws InterruptedException {
        System.out.println(taskRequest.toString());
        int size = taskRequest.getSize();
        Thread.sleep(size*1000);
        return new TaskResponse(taskRequest.getId(),Status.SUCCESS);
    }

    @Override
    public TaskResponse call() throws Exception {
       // System.out.println(taskRequest.toString());
        int size = taskRequest.getSize();
        Thread.sleep(size*1000);
        return new TaskResponse(taskRequest.getId(),Status.SUCCESS);
    }

}
