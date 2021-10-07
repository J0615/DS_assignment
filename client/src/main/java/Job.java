import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Job extends Thread {
    private int startSkierID;
    private int endSkierID;
    private int startTime;
    private int endTime;
    private String ip;
    private int port;
    private int totalRequests;
    private int numSkiLifts;
    public int succConnect;
    public int failedConnect;
    public ArrayList<Long> latencies;


    public Job(int a, int b, int c, int d, String ip, int port, int totalRequests, int numSkiLifts) {
        this.startSkierID = a;
        this.endSkierID = b;
        this.startTime = c;
        this.endTime = d;
        this.ip = ip;
        this.port = port;
        this.totalRequests = totalRequests;
        this.numSkiLifts = numSkiLifts;
        this.succConnect = 0;
        this.failedConnect = 0;
        this.latencies = new ArrayList<>();
    }

    public int postConnect(String stringurl, int liftID, int t) {

        try {
            long start = System.currentTimeMillis();
//            System.out.println(stringurl);
            URL url = new URL(stringurl);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            String jsonInputString = String.format("{\"liftID\": %o, \"time\": %o}", liftID, t);
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
//            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
//            osw.write(jsonInputString);
//            osw.flush();
//            osw.close();
//            os.close();
            con.connect();
            long end = System.currentTimeMillis();
            this.latencies.add(end-start);
            int code = con.getResponseCode();
            return code;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return 400;
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }
    }
    public void run() {
        System.out.println("This code is running in a thread");
        for (int i = 0; i < totalRequests; i++) {
            int skierID = ThreadLocalRandom.current().nextInt(startSkierID, endSkierID + 1);
            int liftID = ThreadLocalRandom.current().nextInt(5, numSkiLifts + 1);
            int t = ThreadLocalRandom.current().nextInt(startTime, endTime + 1);
            String stringurl = String.format("http://%s:%d/helloworld_war/skiers/12/seasons/2019/days/1/skiers/%o", ip, port, skierID);
            int code = postConnect(stringurl, liftID, t);
//            System.out.println("Code:" + code);
            int retry = 1;
            while (retry <= 5 || code != 201) {
                code = postConnect(stringurl, liftID, t);
                retry++;
            }

            if (code == 201) {
                this.succConnect++;
            } else {
                this.failedConnect++;
            }
            System.out.println("request:"+i);
        }
        System.out.println("This thread is reaching the end!");
    }
}
