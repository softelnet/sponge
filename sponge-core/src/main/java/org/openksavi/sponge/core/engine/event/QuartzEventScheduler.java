/*
 * Copyright 2016-2017 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.core.engine.event;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.event.QuartzEventSchedulerEntry;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventSchedulerEntry;

/**
 * An event scheduler that uses Quartz.
 */
public class QuartzEventScheduler extends BaseEventScheduler {

    private static final Logger logger = LoggerFactory.getLogger(QuartzEventScheduler.class);

    public static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";

    protected static final String KEY_PARAMETERS = "parameters";

    /** Quartz scheduler. */
    private Scheduler scheduler;

    /** Lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new event scheduler.
     *
     * @param engine the engine.
     * @param outQueue an output event queue.
     */
    public QuartzEventScheduler(SpongeEngine engine, EventQueue outQueue) {
        super(engine, outQueue);
    }

    /**
     * Starts up this event scheduler.
     */
    @Override
    public void doStartup() {
        try {
            Properties props = new Properties();
            String name = getName() + "-" + SpongeUtils.getRandomUuidString();
            props.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, name);
            props.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_ID, name);
            props.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, SimpleThreadPool.class.getName());

            // There should be only one thread here to ensure the proper order of scheduled events.
            props.put(PROP_THREAD_COUNT, Integer.toString(1));

            StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
            schedulerFactory.initialize(props);

            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw SpongeUtils.wrapException(getName(), e);
        }
    }

    /**
     * Shuts down this event scheduler.
     */
    @Override
    public void doShutdown() {
        if (scheduler != null) {
            try {
                scheduler.clear();
                scheduler.shutdown(true);
            } catch (SchedulerException e) {
                throw SpongeUtils.wrapException(getName(), e);
            }
        }
    }

    /**
     * Quartz job.
     */
    public static class EventSchedulerJob implements Job {

        public EventSchedulerJob() {
            //
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                EventSchedulerJobParameters parameters =
                        (EventSchedulerJobParameters) context.getJobDetail().getJobDataMap().get(KEY_PARAMETERS);

                // Add the event (or its clone if necessary) to the queue immediately.
                parameters.getScheduler().scheduleNow(parameters.isSingle() ? parameters.getEvent() : parameters.getEvent().clone());
            } catch (Throwable e) {
                // Throw only JobExecutionException
                if (e instanceof JobExecutionException) {
                    throw e;
                } else {
                    throw new JobExecutionException(e);
                }
            }
        }
    }

    public static class EventSchedulerJobParameters {

        private QuartzEventScheduler scheduler;

        private Event event;

        private boolean single;

        private EventSchedulerEntry entry;

        public EventSchedulerJobParameters(QuartzEventScheduler scheduler, Event event, boolean single) {
            this.scheduler = scheduler;
            this.event = event;
            this.single = single;
        }

        public QuartzEventScheduler getScheduler() {
            return scheduler;
        }

        public void setScheduler(QuartzEventScheduler scheduler) {
            this.scheduler = scheduler;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public boolean isSingle() {
            return single;
        }

        public void setSingle(boolean single) {
            this.single = single;
        }

        public EventSchedulerEntry getEntry() {
            return entry;
        }

        public void setEntry(EventSchedulerEntry entry) {
            this.entry = entry;
        }
    }

    protected EventSchedulerEntry doSchedule(Event event, Trigger trigger, boolean single) {
        validateEvent(event);

        lock.lock();
        try {
            JobDataMap data = new JobDataMap();
            EventSchedulerJobParameters parameters = new EventSchedulerJobParameters(this, event, single);
            data.put(KEY_PARAMETERS, parameters);

            //@formatter:off
            JobDetail job = newJob(EventSchedulerJob.class)
                    .withIdentity(getNextEntryId(), getName())
                    .setJobData(data)
                    .build();
            //@formatter:on

            try {
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                throw SpongeUtils.wrapException(getName(), e);
            }

            EventSchedulerEntry entry = new QuartzEventSchedulerEntry(job.getKey(), event);
            parameters.setEntry(entry);

            logger.debug("Scheduling event: {}", event);

            return entry;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Schedules an event after a specified time.
     *
     * @param event event.
     * @param delay delay in milliseconds.
     * @return scheduled event entry.
     */
    @Override
    public EventSchedulerEntry scheduleAfter(Event event, long delay) {
        return scheduleAfter(event, delay, 0);
    }

    /**
     * Schedules an event after a specified time with the specified interval.
     *
     * @param event event.
     * @param delay delay in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    @Override
    public EventSchedulerEntry scheduleAfter(Event event, long delay, long interval) {
        TriggerBuilder<Trigger> builder = newTrigger();

        if (delay > 0) {
            builder.startAt(Date.from(Instant.now().plusMillis(delay)));
        }

        if (interval > 0) {
            builder.withSchedule(
                    simpleSchedule().withIntervalInMilliseconds(interval).repeatForever().withMisfireHandlingInstructionFireNow());
        }

        return doSchedule(event, builder.build(), interval == 0);
    }

    /**
     * Schedules an event at a specified time.
     *
     * @param event event.
     * @param at time in milliseconds.
     * @return scheduled event entry.
     */
    @Override
    public EventSchedulerEntry scheduleAt(Event event, long at) {
        return scheduleAt(event, at, 0);
    }

    /**
     * Schedules an event at a specified time with the specified interval.
     *
     * @param event event.
     * @param at time in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    @Override
    public EventSchedulerEntry scheduleAt(Event event, long at, long interval) {
        TriggerBuilder<Trigger> builder = newTrigger();

        if (at >= 0) {
            builder.startAt(new Date(at));
        }

        if (interval > 0) {
            builder.withSchedule(simpleSchedule().withIntervalInMilliseconds(interval).repeatForever());
        }

        return doSchedule(event, builder.build(), interval == 0);
    }

    @Override
    public EventSchedulerEntry scheduleAt(Event event, String crontabSpec) {
        return doSchedule(event,
                newTrigger().withSchedule(cronSchedule(Validate.notNull(crontabSpec, "Crontab specification cannot be null"))).build(),
                false);
    }

    /**
     * Removes the scheduled event entry.
     *
     * @param entry the scheduled event entry.
     * @return {@code true} if the specified entry has been scheduled.
     */
    @Override
    public boolean remove(EventSchedulerEntry entry) {
        logger.debug("Descheduling entry with id: {}", entry.getId());

        try {
            return scheduler.deleteJob((JobKey) entry.getId());
        } catch (SchedulerException e) {
            throw SpongeUtils.wrapException(getName(), e);
        }
    }

    /**
     * Returns all scheduled entries.
     *
     * @return all scheduled entries.
     */
    @Override
    public List<EventSchedulerEntry> getEntries() {
        try {
            return scheduler.getCurrentlyExecutingJobs().stream()
                    .map(context -> ((EventSchedulerJobParameters) context.getMergedJobDataMap().get(KEY_PARAMETERS)).getEntry())
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            throw SpongeUtils.wrapException(getName(), e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
