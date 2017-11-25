package com.yvr.ch03;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Exercises {

    //region Chapter 03 Exercise 01 ------------------------------------------------------------------------------------
    /* Chapter 03 Exercise 01
        Enhance the lazy logging technique by providing conditional logging.
        A typical call would be logIf(Level.FINEST, () -> i == 10, () -> "a[10] = " + a[10]).
        Don’t evaluate the condition if the logger won’t log the message.
    */
    public void ch03Ex01_v1() {
        logIf(false, () -> System.out.println("Do not write out"));
        logIf(true, () -> System.out.println("Write out"));

        try {
            logIf2(false, () -> {
                System.out.println("Do not write out");
                return 0;
            });
            logIf2(true, () -> {
                System.out.println("Write out");
                return 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logIf(final boolean flag_in, Runnable runnable_in) {
        if (flag_in) {
            runnable_in.run();
        }
    }

    private void logIf2(final boolean flag_in, Callable<Integer> callable_in) throws Exception {
        if (flag_in) {
            callable_in.call();
        }
    }

    public void ch03Ex01_v2() {
        Logger.getGlobal().setLevel(Level.OFF);
        logIf(Level.INFO, () -> true, () -> "you'll never see it");
        Logger.getGlobal().setLevel(Level.ALL);
        int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int i = 0; i < a.length; i++) {
            log(i, a);
        }
    }

    private void log(int i, int[] a) {
        logIf(Level.INFO, () -> i == 10, () -> "a[10] = " + a[10]);
    }

    private void logIf(Level level, Supplier<Boolean> condition, Supplier<String> message) {
        Logger logger = Logger.getGlobal();
        if (logger.isLoggable(level)   //evaluate condition only if the logger will log the message
                && condition.get()) {
            logger.log(level, message.get());
        }
    }
    //endregion Chapter 03 Exercise 01 ---------------------------------------------------------------------------------
}
