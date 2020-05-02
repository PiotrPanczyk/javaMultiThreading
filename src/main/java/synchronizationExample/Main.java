package synchronizationExample;

import java.util.Random;

public class Main {
    public static void  main(String[] args){
        Metrics metrics = new Metrics();
        BusinessLogic businessLogicThread1 = new BusinessLogic(metrics);
        BusinessLogic businessLogicThread2 = new BusinessLogic(metrics);
        MetricsPrinter metricsPrinterThread = new MetricsPrinter(metrics);

        businessLogicThread1.setName("bsLogic1");
        businessLogicThread1.start();
        businessLogicThread2.setName("bsLogic2");
        businessLogicThread2.start();
        metricsPrinterThread.setName("metrics");
        metricsPrinterThread.start();
    }

    public static class MetricsPrinter extends Thread {
        private Metrics metrics;

        public MetricsPrinter(Metrics metrics){
            this .metrics = metrics;
        }

        public void run(){
            while(true){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /*
                Because getAverage method is not synchronized
                We will not slow down business logic by calling it from separate thread
                 */
                double currentAverage = metrics.getAverage();
                System.out.println("Current Average is: " + currentAverage);
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics){
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                long start = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                metrics.addSample(end - start);
            }
        }
    }

    public static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;

        /*
        Sample can be added by multiple threads.
        Class variables count and average are shared between threads
        and are critical and need to be executed atomically.
        That's why we need to synchronize this method.
         */
        public synchronized void addSample(long sample){
            double currentSum = average * count;
            count++;
            average = (average + sample) / count;
        }

        /*
        Reads and writes from primitives are atomic,
         but not for 64bit long and double
        For getAvarave to be atomic we need to declare
         variable average volatile
         */
        public double getAverage(){
            return average;
        }
    }
}
