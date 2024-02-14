package com.academy.fintech.pe.core.service.agreement.db.schedule;

public interface ScheduleService {
    /**
     * Add given schedule to db and return its id.
     */
    long addSchedule(Schedule schedule);
}
