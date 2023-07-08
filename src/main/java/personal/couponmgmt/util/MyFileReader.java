package personal.couponmgmt.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import personal.couponmgmt.exception.CouponManagementException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MyFileReader {

    private static final Logger LOG = LogManager.getLogger(MyFileReader.class);

    public static void main(String args[]) throws CouponManagementException {
        System.out.println("hello");
        LOG.info("info level logging print");
        LOG.debug("debug level logging print");
        File directory = new File("./");
        System.out.println(directory.getAbsolutePath());
        File file = new File("./resources/couponcodes.txt");
        System.out.println(file.getAbsolutePath());
        URL url = MyFileReader.class.getClassLoader().getResource("couponcodes.txt");
        File f = new File(url.getPath());
        System.out.println(f.getAbsolutePath());
        List<String> couponCodes = readFile("couponcodes.txt");
        System.out.println(couponCodes.size());
        for (String c : couponCodes) {
            System.out.println(c);
            var a = c.split(",");
            int cid = Integer.valueOf(a[0]);
            System.out.println(cid);
        }


    }

    public static List<String> readFile(String filename) throws CouponManagementException {

        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(MyFileReader.class.getClassLoader().getResourceAsStream(filename)));
            String line = "";
            while ((line = bf.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new CouponManagementException("FILE_READER_EXCEPTION", e.getMessage(), e);
        }
        return lines;
    }
}
