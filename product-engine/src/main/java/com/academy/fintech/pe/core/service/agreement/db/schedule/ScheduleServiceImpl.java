package com.academy.fintech.pe.core.service.agreement.db.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Override
    public long addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule).getScheduleId();
    }
}
