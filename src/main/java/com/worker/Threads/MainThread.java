package com.worker.Threads;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.sqs.model.Message;
import com.worker.common.MissionTypes;
import com.worker.common.common;
import com.worker.managers.PDFManager;
import com.worker.managers.S3Manager;
import com.worker.managers.SQSManager;

import java.util.concurrent.Callable;

import static com.worker.common.init.*;

public class MainThread implements Callable {
    private SQSManager sqs = new SQSManager();
    private PDFManager pdf = new PDFManager();
    private S3Manager s3 = new S3Manager();

    @Override
    public Object call() throws Exception {
        logger.info("handeling messages");
        Message message = null;
        while (true) {
            while (message == null) {
                logger.info("getting message");
                message = sqs.recieve_message(common.worker_queue_url, null, common.worker_consumer);
            }
            logger.info("handeling message");
            handle_message(message);
            logger.info("deleting message");
            sqs.delete_message(common.worker_queue_url, message);
            logger.info("setting message to null");
            message = null;
        }
    }

    /**
     * handles files as described in the assignments
     *
     * @param message message to handle
     */
    private void handle_message(Message message) {
        String[] body_splited = common.parse_body(message);
        String client_id = body_splited[1];
        String task_type = body_splited[2];
        String old_file_url = body_splited[3];
        String new_file_path;
        logger.config("handling message: " + message.getBody());
        if (task_type.equals(MissionTypes.ToText.name()))
            new_file_path = pdf.get_first_page_text(old_file_url);
        else if (task_type.equals(MissionTypes.ToImage.name()))
            new_file_path = pdf.get_first_page_image(old_file_url);
        else
            new_file_path = pdf.get_first_page_html(old_file_url);
        String new_file_url;
        if (!new_file_path.startsWith("ERROR")) {
            logger.info("uploading file " + new_file_path);
            String file_key = s3.upload_object(new_file_path);
            new_file_url = s3.get_object_url(file_key);
        } else
            new_file_url = new_file_path;
        sqs.send_message(common.worker_to_manager_queue_url, String.format("%s\t%s\t%s\t%s\t%s",
                common.done_pdf_task,
                client_id,
                task_type,
                old_file_url,
                new_file_url), client_id, common.manager_main_thread_consumer);
    }
}
