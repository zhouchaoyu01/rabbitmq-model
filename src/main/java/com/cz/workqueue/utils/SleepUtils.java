package com.cz.workqueue.utils;

/**
 * @author zhouchaoyu
 * @time 2023-06-11-21:57
 */
public class SleepUtils {
    public static void sleep(int second){
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
