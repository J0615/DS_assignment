import java.util.ArrayList;
import java.util.Collections;

public class Client {
    private static int numThreads;
    private static int numSkiers;

    private static int numSkiLifts;
    private static int meanSkiLifts;
    private static String ip;
    private static int port;


    public static void main(String[] args) throws InterruptedException {
        // TODO: consider defaults
        numThreads = Integer.parseInt(args[0]);
        numSkiers = Integer.parseInt(args[1]);
        numSkiLifts = Integer.parseInt(args[2]);
        meanSkiLifts = Integer.parseInt(args[3]);
        ip = args[4];
        port = Integer.parseInt(args[5]);
        for (String str: args) {
            System.out.println(str);
        }

        long start = System.currentTimeMillis() / 1000;

        // Phase 1
        int totalThreads =  numThreads/4;
        int totalRequests = (int) Math.round(meanSkiLifts * 0.2 * (numSkiers/totalThreads));
        System.out.println("totalthreads:" + totalThreads);
        System.out.println("totalRequests:" + totalRequests);

        ArrayList<Job> jobs = getPhaseJobs(totalThreads, 1, 90, totalRequests);

        if (checkprogress(totalThreads, jobs, 0.1)) {
            // Phase 2
            int totalThreads_2 = numThreads;
            int totalRequests_2 = (int) Math.round(meanSkiLifts * 0.6 * (numSkiers/totalThreads));
            System.out.println("totalthreads:" + totalThreads_2);
            System.out.println("totalRequests:" + totalRequests_2);
            ArrayList<Job> jobs_2 = getPhaseJobs(totalThreads_2, 91, 360, totalRequests_2);

            if (checkprogress(totalThreads_2, jobs_2, 0.1)) {
                int totalThreads_3 =  numThreads/4;
                int totalRequests_3 = (int) Math.round(meanSkiLifts * 0.1);
                System.out.println("totalthreads:" + totalThreads_3);
                System.out.println("totalRequests:" + totalRequests_3);
                ArrayList<Job> jobs_3 = getPhaseJobs(totalThreads_3,361, 420, totalRequests_3);

                if (checkprogress(totalThreads, jobs, 1.0) && checkprogress(totalThreads_2, jobs_2, 1.0) && checkprogress(totalThreads_3, jobs_3, 1.0)) {
                    // TODO: print successfull / unsuccesfullu requests
                    int totalSuccConnect = 0;
                    int totalFailedConnect = 0;
                    long totalTime = 0;
                    int maxResponseTime = 0;
                    int totalRequest = 0;
                    ArrayList<Long> latencies = new ArrayList<>();
                    for (Job j : jobs) {
                        System.out.println("failedConnect:" + j.failedConnect);
                        System.out.println("succConnect:" + j.succConnect);
                        totalFailedConnect += j.failedConnect;
                        totalSuccConnect += j.succConnect;
                        latencies.addAll(j.latencies);
                    }
                    for (Job j : jobs_2) {
                        totalFailedConnect += j.failedConnect;
                        totalSuccConnect += j.succConnect;

                        latencies.addAll(j.latencies);
                    }
                    for (Job j : jobs_3) {
                        totalFailedConnect += j.failedConnect;
                        totalSuccConnect += j.succConnect;

                        latencies.addAll(j.latencies);
                    }
                    long end = System.currentTimeMillis() / 1000;
                    long runtime = end - start;
                    long throughput = (totalRequests * totalThreads + totalRequests_2 * totalThreads_2 + totalRequests_3 * totalThreads_3) / runtime;
                    System.out.println("Successful connections:" + totalSuccConnect);
                    System.out.println("Failed connections:" + totalFailedConnect);
                    System.out.println("Wall time:" +runtime);
                    System.out.println("Throughput: " + throughput);


                    double sum = 0;
                    for(int i=0; i < latencies.size(); i ++){
                        sum += latencies.get(i);
                    }
                    int meanResponseTime = (int) Math.round(sum / latencies.size());
                    Collections.sort(latencies);
                    int medianResonseTime = Math.toIntExact(latencies.get(latencies.size() / 2));
                    Long pninetynine = latencies.get((int) (latencies.size() * 0.9));

                    System.out.println("Mean response time: " + meanResponseTime);
                    System.out.println("Median response time: " + medianResonseTime);
                    System.out.println("P99 response time: " + pninetynine);
                    System.out.println("Max response time:" + latencies.get(latencies.size()-1));



                }

            }
        }


    }

    public static boolean checkprogress(int totalThreads, ArrayList<Job> jobs, double p) throws InterruptedException {
        int completeTh = 0;
        while (completeTh < totalThreads * p) {
            completeTh = 0;
            for (Job j : jobs) {
                if (!j.isAlive()) {
                    completeTh++;
                }
            }
            Thread.sleep(1000);
            System.out.println("totalProcessComplete:" + completeTh);
        }
        return true;
    }



    public static ArrayList<Job> getPhaseJobs(int totalThreads, int startTime, int endTime, int totalRequests) {
        ArrayList<Job> jobs = new ArrayList<>();
        for (int i = 0; i < totalThreads; i++) {
            int startSkierID = i * (numSkiers/totalThreads) + 1;
            int endSkierID = (i+1) * (numSkiers/totalThreads);
            Job th = new Job(startSkierID, endSkierID, startTime, endTime, ip, port, totalRequests, numSkiLifts);
            th.start();
            System.out.println(i);
            jobs.add(th);
        }
        return jobs;
    }
}
