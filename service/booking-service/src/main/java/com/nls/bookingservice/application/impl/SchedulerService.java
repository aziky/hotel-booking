package com.nls.bookingservice.application.impl;

import com.nls.bookingservice.infrastructure.external.client.BookingReminderJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final Scheduler scheduler;

    public void scheduleBookingReminder(UUID bookingId, UUID userId, LocalDateTime appointmentTime) throws SchedulerException {
        // Run 1 hour before appointment
        log.info("Start running schedule booking reminder");
        LocalDateTime sendTime = appointmentTime.minusMinutes(4);
        Date triggerTime = Date.from(sendTime.atZone(ZoneId.systemDefault()).toInstant());

        JobDetail jobDetail = JobBuilder.newJob(BookingReminderJob.class)
                .withIdentity(bookingId.toString(), "booking-reminders")
                .usingJobData("bookingId", bookingId.toString())
                .usingJobData("userId", userId.toString())
                .usingJobData("email", "pcm230304@gmail.com")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(bookingId + "-trigger", "booking-reminders")
                .startAt(triggerTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("run job scheduler successfully");
    }

}
