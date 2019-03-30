package com.worker.common;

import com.amazonaws.services.sqs.model.Message;

public class common {

    //    SQS message types
    public static final String new_task = "new task";
    public static final String done_task = "done task";
    public static final String new_pdf_task = "new pdf task";
    public static final String done_pdf_task = "done pdf task";
    public static final String terminate_task = "terminate";
    //    consumer types
    public static String manager_main_thread_consumer = "MANAGER-MAIN_THREAD";
    public static String client_consumer = "CLIENT";
    public static String worker_consumer = "WORKER";


    //    queues url
    public static String clients_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/clients-queue.fifo";
    public static String manager_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/manager-queue.fifo";
    public static String worker_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/worker-queue.fifo";
    public static String worker_to_manager_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/manager-queue-from-worker.fifo";

    public static String[] parse_body(Message message) {
        String body = message.getBody();
        return body.split("\t");
    }
}
