package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestSchedule {

    public static void main(String[] args) {
        //如何让每周四18:00:00定时执行任务?
        // initailDeLay 代表当前时间和周四的时间差
        //  period一周的间隔时间
        long period = 1000 * 60 * 60 * 24 * 7;
        LocalDateTime now = LocalDateTime.now();
        // 修改到周四
        LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);
        if (now.compareTo(time) >0){
            time = time.plusWeeks(1);
        }
        long initialDelay = Duration.between(now,time).toMillis();
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(()->{
            log.debug("running... ");
        },initialDelay,period, TimeUnit.MILLISECONDS);
    }
}
