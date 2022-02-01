/*
 * Copyright (c) 27/4/18 1:10 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;


import com.gradle.entity.user.User;
import org.joda.time.LocalDateTime;


import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class TimeAgo {
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    public static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    /**
     * Get duration in sec, minutes, or days ago
     * @param duration
     * @return
     */
    public static String toDuration(long duration) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < TimeAgo.times.size(); i++) {
            Long current = TimeAgo.times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(TimeAgo.timesString.get(i)).append(temp != 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "0 seconds ago";
        else
            return res.toString();
    }

    /**
     * Get user last seen active
     * @param user
     * @return
     */
    public static String getUserLastSeen(User user) {
        LocalDateTime currentDateTime = new LocalDateTime();
        long duration = currentDateTime.toDateTime().getMillis() - user.getLastSeenAt().toDateTime().getMillis();
        return TimeAgo.toDuration(duration);
    }

    /*public static void main(String args[]) {
        System.out.println(toDuration(123));
        System.out.println(toDuration(1230));
        System.out.println(toDuration(12300));
        System.out.println(toDuration(123000));
        System.out.println(toDuration(1230000));
        System.out.println(toDuration(12300000));
        System.out.println(toDuration(123000000));
        System.out.println(toDuration(1230000000));
        System.out.println(toDuration(12300000000L));
        System.out.println(toDuration(123000000000L));
    }*/
    public static final Map<String, Long> timesl = new LinkedHashMap<>();

    static {
        timesl.put("year", TimeUnit.DAYS.toMillis(365));
        timesl.put("month", TimeUnit.DAYS.toMillis(30));
        timesl.put("week", TimeUnit.DAYS.toMillis(7));
        timesl.put("day", TimeUnit.DAYS.toMillis(1));
        timesl.put("hour", TimeUnit.HOURS.toMillis(1));
        timesl.put("minute", TimeUnit.MINUTES.toMillis(1));
        timesl.put("second", TimeUnit.SECONDS.toMillis(1));
    }

    public static String toRelative(long duration, int maxLevel) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : timesl.entrySet()) {
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0) {
                res.append(timeDelta)
                        .append(" ")
                        .append(time.getKey())
                        .append(timeDelta > 1 ? "s" : "")
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel) {
                break;
            }
        }
        if ("".equals(res.toString())) {
            return "0 seconds ago";
        } else {
            res.setLength(res.length() - 2);
            res.append(" ago");
            return res.toString();
        }
    }

    public static String toRelative(long duration) {
        return toRelative(duration, timesl.size());
    }

    public static String toRelative(Date start, Date end) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime());
    }

    public static String toRelative(Date start, Date end, int level) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime(), level);
    }

}