package com.creang.cron;

import java.util.Date;
import java.util.concurrent.*;

public class CronThreadPoolExecutor extends ScheduledThreadPoolExecutor implements CronExecutorService {

    public CronThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public CronThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public CronThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public CronThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override
    public void schedule(final Runnable task, final CronExpression expression) {
        if (task == null) throw new NullPointerException();

        this.setCorePoolSize(this.getCorePoolSize() + 1);

        System.out.println("CorePoolSize" + this.getCorePoolSize());

        Runnable scheduleTask = new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                Date now = new Date();
                Date time = expression.getNextValidTimeAfter(now);

                try {
                    while (time != null) {
                        CronThreadPoolExecutor.this.schedule(task, time.getTime() - now.getTime(), TimeUnit.MILLISECONDS);

                        while (now.before(time)) {
                            Thread.sleep(time.getTime() - now.getTime());

                            now = new Date();
                        }

                        time = expression.getNextValidTimeAfter(now);
                    }
                } catch (RejectedExecutionException | CancellationException e) {
                    // Occurs if executor was already shutdown when schedule() is called
                } catch (InterruptedException e) {
                    // Occurs when executing task are interrupted during shutdownNow()
                    Thread.currentThread().interrupt();
                }
            }
        };

        this.execute(scheduleTask);
    }
}
