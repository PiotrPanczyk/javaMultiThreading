public class DebuggingThreads {

    public static void main (String args[]) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //Set thread name
                Thread.currentThread().setName("WorkerThread");

                //Set thread priority
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                System.out.println("Starting thread: " + Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName()+" priority is: "+Thread.currentThread().getPriority());
                throw new RuntimeException("Intentional Exception");
            }
        });

        /*
         * Unchecked exception will bring down whole thread if not caught and handled.
         * That makes it difficult to debug
         * We can use setUncaughtExceptionHandler to be able to do cleanup and log
         * useful information for troubleshooting.
         */
        t1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
               System.out.println("Critical error in thread: " + t.getName()
                       + " the error is: " + e.getMessage());
            }
        });

        System.out.println("Before Starting thread: " + Thread.currentThread().getName());
        t1.start();
        System.out.println("After Starting thread: " + Thread.currentThread().getName());

        //De-schedules thread for given time - no spinning loop, no CPU utilisation
        Thread.sleep(10000);

    }
}
