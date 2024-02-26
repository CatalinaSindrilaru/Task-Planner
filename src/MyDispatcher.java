//import java.util.List;
//
//public class MyDispatcher extends Dispatcher {
//
//    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
//        super(algorithm, hosts);
//    }
//
//    int index_last_node = -1;
//
//    @Override
//    synchronized public void addTask(Task task) {
//
//        if (this.algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
//            // get the index of the appropriate host and
//            // then assign the task to it
//            int index_host = appropriate_host();
//            hosts.get(index_host).addTask(task);
//
//        } else if (this.algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
//            // assign the short tasks to the first host,
//            // the medium tasks to the second host and
//            // the long tasks to the third host
//            if (task.getType() == TaskType.SHORT) {
//                hosts.get(0).addTask(task);
//            } else if (task.getType() == TaskType.MEDIUM) {
//                hosts.get(1).addTask(task);
//            } else if (task.getType() == TaskType.LONG) {
//                hosts.get(2).addTask(task);
//            }
//
//        } else if (this.algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
//            int index_min_queue_host = -1;
//            int min_size = Integer.MAX_VALUE;
//            int i;
//
//            // go through all the host to find the one with the smallest queue
//            for (i = 0; i < hosts.size(); i++) {
//                // get the size of the host
//               int queue_size = hosts.get(i).getQueueSize();
//                // if the size is smaller than the current minimum size
//                // modify the minimum size and the index of the wanted host
//                if (queue_size < min_size) {
//                    min_size = queue_size;
//                    index_min_queue_host = i;
//                } else if (queue_size == min_size) {
//                    // if the size is equal to the current minimum size, save the index
//                    // of the host with the smallest id
//                    long id1 = hosts.get(i).getId();
//                    long id2 = hosts.get(index_min_queue_host).getId();
//                    if (id1 < id2) {
//                        index_min_queue_host = i;
//                    }
//                }
//            }
//
//            // add the task to the host with the smallest queue
//            hosts.get(index_min_queue_host).addTask(task);
//
//        } else if (this.algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
//            // same as above, but instead of the size of the queue,
//            // I use the work left of the host
//            int index_min_work_left_host = -1;
//            double min_work_left = Double.MAX_VALUE;
//            int i;
//
//            for (i = 0; i < hosts.size(); i++) {
//                // convert the work left from milliseconds to seconds
//                // and round it as specified in the requirements
//                double time = Math.round((double)(hosts.get(i).getWorkLeft()) / 1000);
//
//                if (time < min_work_left) {
//                    min_work_left = time;
//                    index_min_work_left_host = i;
//                } else if (time == min_work_left) {
//                    long id1 = hosts.get(i).getId();
//                    long id2 = hosts.get(index_min_work_left_host).getId();
//                    if (id1 < id2) {
//                        index_min_work_left_host = i;
//                    }
//                }
//            }
//
//            hosts.get(index_min_work_left_host).addTask(task);
//        }
//    }
//
//    private int appropriate_host() {
//        // the appropriate host is the next one in the list,
//        // but if the last host was the last in the list,
//        // we started from the first one again
//        index_last_node = (index_last_node + 1) % hosts.size();
//        return index_last_node;
//    }
//}

/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
    private int idHost = 0;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    synchronized public void addTask(Task task) {
        switch(algorithm) {
            case ROUND_ROBIN:
                hosts.get(idHost).addTask(task);
                idHost = (idHost + 1) % hosts.size();
                break;

            case SIZE_INTERVAL_TASK_ASSIGNMENT:
                switch (task.getType()) {
                    case SHORT -> hosts.get(0).addTask(task);
                    case MEDIUM -> hosts.get(1).addTask(task);
                    case LONG -> hosts.get(2).addTask(task);
                }
                break;

            case SHORTEST_QUEUE:
                int minSize = hosts.get(0).getQueueSize();
                int minIndexSQ = 0;

                for (int i = 1; i < hosts.size(); i++) {
                    if (minSize > hosts.get(i).getQueueSize()) {
                        minSize = hosts.get(i).getQueueSize();
                        minIndexSQ = i;
                    } else if (minSize == hosts.get(i).getQueueSize()) {
                        if (hosts.get(minIndexSQ).getId() > hosts.get(i).getId()) {
                            minIndexSQ = i;
                        }
                    }
                }

                hosts.get(minIndexSQ).addTask(task);
                break;

            case LEAST_WORK_LEFT:
                long lwl = hosts.get(0).getWorkLeft();
                int minIndexLWL = 0;

                for (int i = 1; i < hosts.size(); i++) {

                    long wl = hosts.get(i).getWorkLeft();

                    if (lwl > wl) {
                        lwl = wl;
                        minIndexLWL = i;

                    } else if (lwl == wl) {
                        if (hosts.get(minIndexLWL).getId() > hosts.get(i).getId()) {
                            minIndexLWL = i;
                        }
                    }
                }

                hosts.get(minIndexLWL).addTask(task);
                break;
        }
    }
}
