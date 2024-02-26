//import java.util.Comparator;
//import java.util.concurrent.PriorityBlockingQueue;
//
//public class MyHost extends Host {
//
//    PriorityBlockingQueue<Task> queue;
//    Task current_task;
//
//    boolean is_running;
//
//    long start_time;
//    long end_time;
//
//    public MyHost() {
//        super();
//        // initializing priority queue
//        queue = new PriorityBlockingQueue<Task>(10, (t1, t2) -> {
//            if (t1.getPriority() != t2.getPriority()) {
//                // higher priority first
//                return Integer.compare(t2.getPriority(), t1.getPriority());
//            }
//            // if priorities are equal, earlier start time first
//            return Integer.compare(t1.getStart(), t2.getStart());
//        });
//        current_task = null;
//        is_running = true;
//        start_time = 0;
//        end_time = 0;
//    }
//
//
//    @Override
//    public void run() {
//        // until shutdown is called
//        while (is_running) {
//            // if there are tasks in the queue
//            if (!queue.isEmpty()) {
//                // if there is no current task
//                if (current_task == null) {
//                    // get the first task from the queue
//                    current_task = queue.peek();
//
//                    try {
//                        // retain at what time the task started
//                        start_time = System.currentTimeMillis();
//                        // sleep for the duration of the task
//                        this.sleep(current_task.getLeft());
//                        // if the sleep ended normally, the task is finished, so
//                        // remove it from the queue
//                        queue.remove(current_task);
//                        current_task.finish();
//                        current_task = null;
//                    } catch (InterruptedException e) {
//                        // if the sleep was interrupted, the task is preempted,
//                        // so I need to update the left time of the task
//                        end_time = System.currentTimeMillis();
//                        current_task.setLeft(current_task.getLeft() - (end_time - start_time));
//                        // if the task is finished, remove it from the queue
//                        if (current_task.getLeft() <= 0) {
//                            current_task.finish();
//                            queue.remove(current_task);
//                        }
//
//                        current_task = null;
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void addTask(Task task) {
//        // add the new task to the queue
//        queue.add(task);
//        // if there is a current task
//        if (current_task != null) {
//            // verify if the new task has higher priority than the current task,
//            // just if the current task is preemptive
//            if (current_task.isPreemptible() && task.getPriority() > current_task.getPriority()) {
//                // interrupt the current task
//                this.interrupt();
//            }
//        }
//    }
//
//    @Override
//    public int getQueueSize() {
//        return queue.size();
//    }
//
//    @Override
//    public long getWorkLeft() {
//
//        long work_left = 0;
//
//        // go through all the tasks in the queue
//        for (Task task : queue) {
//            if (task != current_task) {
//                work_left += task.getLeft();
//            } else { // if the task is the current task
//                // calculate the time left until the task finishes
//                end_time = System.currentTimeMillis();
//                work_left += task.getLeft() - (end_time - start_time);
//            }
//
//        }
//        return work_left;
//    }
//
//    @Override
//    public void shutdown() {
//        // if the shutdown is called, set the variable to
//        // false, so the thread to stop running
//        is_running = false;
//    }
//}

/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    BlockingQueue<Task> blockingQueue = new PriorityBlockingQueue<>(1, new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getPriority() < o2.getPriority() || o1.getPriority() > o2.getPriority()) {
                return Integer.compare(o2.getPriority(), o1.getPriority());
            } else {
                return Integer.compare(o1.getStart(), o2.getStart());
            }
        }
    });
    private Task currTask = null;
    private boolean go = true;

    private long beforeSleep = 0;
    private long afterSleep = 0;

    @Override
    public void run() {
        while (go) {
            if (!blockingQueue.isEmpty()) {
                try {
                    currTask = blockingQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    beforeSleep = System.currentTimeMillis();
                    this.sleep(currTask.getLeft());

                    currTask.finish();
                    currTask = null;

                } catch (InterruptedException e) {
                    afterSleep = System.currentTimeMillis();
                    currTask.setLeft(currTask.getLeft() - afterSleep + beforeSleep);

                    if (currTask.getLeft() <= 0) {
                        currTask.finish();
                        currTask = null;
                    } else {
                        blockingQueue.add(currTask);
                    }
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        blockingQueue.add(task);

        if (currTask != null && currTask.isPreemptible()
                && task.getPriority() > currTask.getPriority()) {
            this.interrupt();
        }
    }

    @Override
    public int getQueueSize() {
        if (currTask == null) {
            return blockingQueue.size();
        } else {
            return blockingQueue.size() + 1;
        }
    }

    @Override
    public long getWorkLeft() {
        long lwl = 0;

        for (Task task : blockingQueue) {
            lwl += task.getLeft();
        }

        if (currTask != null) {
            long timeSlept = currTask.getLeft() - System.currentTimeMillis() + beforeSleep;
            lwl += timeSlept;
        }

        return Math.round((float) lwl / 1000);
    }

    @Override
    public void shutdown() {
        go = false;
    }
}