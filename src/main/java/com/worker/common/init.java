package com.worker.common;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class init {
    public static String log_name = "my_logger";
    public static String bucket_name = "123456789-hod-application-bucket";
    public static Logger logger = Logger.getLogger(log_name);


    public static void main(){
        FileHandler handler = null;
        try {
            handler = new FileHandler("/home/ec2-user/log");
            handler.setLevel(Level.FINER);
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "an exception aws thrown", e);
        }
        logger.setLevel(Level.FINER);
        logger.info("STARTING WORKER");
//        logger.setUseParentHandlers(false);

    }
}
