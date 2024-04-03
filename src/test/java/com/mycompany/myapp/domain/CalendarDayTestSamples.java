package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CalendarDayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CalendarDay getCalendarDaySample1() {
        return new CalendarDay().id(1L);
    }

    public static CalendarDay getCalendarDaySample2() {
        return new CalendarDay().id(2L);
    }

    public static CalendarDay getCalendarDayRandomSampleGenerator() {
        return new CalendarDay().id(longCount.incrementAndGet());
    }
}
