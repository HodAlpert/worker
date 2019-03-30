package com.worker;

import com.worker.Threads.MainThread;
import com.worker.common.init;
import com.worker.managers.PDFManager;

public class Main {
    public static void main(String[] args) {
        init.main();
//        PDFManager p = new PDFManager();
//        System.out.println(p.get_first_page_image("http://www.crcweb.org/Passover/cRcPassoverGuide13_FINAL%20-%20WITHOUT%20ADS.pdf"));
        try {
            new MainThread().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
