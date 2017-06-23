/*
 * Copyright 2016-2017 Softelnet.
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
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.event.QuartzEventCronEntry;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.event.CronEventGenerator;
import org.openksavi.sponge.engine.event.EventGenerator;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventCronEntry;

/**
 * Cron event generator that uses Quartz as a Cron implementation.
 */
public class QuartzCronEventGenerator extends BaseEventGenerator implements CronEventGenerator {

    private static final Logger logger = LoggerFactory.getLogger(QuartzCronEventGenerator.class);

    public static final String PROP_THREAD_COUNT = "org.quartz.threadPool.threadCount";

    protected static final String KEY_GENERATOR = "generator";

    protected static final String KEY_EVENT = "event";

    protected static final String KEY_EVENT_GENERATE_ENTRY = "eventGenerateEntry";

    /** Quartz scheduler. */
    protected Scheduler scheduler;

    /**
     * Creates a new Cron event generator.
     *
     * @param engine
     *            the engine.
     * @param outQueue
     *            an output event queue.
     */
    public QuartzCronEventGenerator(Engine engine, EventQueue outQueue) {
        super("Cron", engine, outQueue);
    }

    /**
     * Starts up Cron.
     */
    @Override
    public void startup() {
        try {
            Properties props = new Properties();
            props.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, "Cron");
            props.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_ID, "Cron");
            props.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, SimpleThreadPool.class.getName());
            props.put(PROP_THREAD_COUNT, Integer.toString(engine.getConfigurationManager().getCronThreadCount()));

            StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
            schedulerFactory.initialize(props);

            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw Utils.wrapException(getName(), e);
        }
    }

    /**
     * Shuts down Cron.
     */
    @Override
    public void shutdown() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                throw Utils.wrapException(getName(), e);
            }
        }
    }

    /**
     * Quartz job.
     */
    public static class EventGeneratorJob implements Job {

        public EventGeneratorJob() {
            //
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                EventGenerator generator = (EventGenerator) context.getJobDetail().getJobDataMap().get(KEY_GENERATOR);
                Event event = (Event) context.getJobDetail().getJobDataMap().get(KEY_EVENT);

                // Add the event to the queue.
                generator.putEvent(event, true);
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

    @Override
    public EventCronEntry schedule(Event event, String crontabSpec) {
        JobDataMap data = new JobDataMap();
        data.put(KEY_GENERATOR, this);
        data.put(KEY_EVENT, event);

        //@formatter:off
        JobDetail job = newJob(EventGeneratorJob.class)
                .withIdentity(getNextEntryId(), getName())
                .setJobData(data)
                .build();

        Trigger trigger = newTrigger()
                .withSchedule(cronSchedule(crontabSpec))
                .build();
        //@formatter:on

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw Utils.wrapException(getName(), e);
        }

        EventCronEntry entry = new QuartzEventCronEntry(job.getKey(), event, crontabSpec);
        job.getJobDataMap().put(KEY_EVENT_GENERATE_ENTRY, entry);

        logger.debug("Scheduling task with id: {}", entry.getId());

        return entry;
    }

    /**
     * Removes the crontab entry.
     *
     * @param entry
     *            crontab entry.
     * @return {@code true} if the specified entry has been scheduled.
     */
    @Override
    public boolean remove(EventCronEntry entry) {
        logger.debug("Descheduling task with id: {}", entry.getId());

        try {
            return scheduler.deleteJob((JobKey) entry.getId());
        } catch (SchedulerException e) {
            throw Utils.wrapException(getName(), e);
        }
    }

    /**
     * Returns all scheduled crontab entries.
     *
     * @return all scheduled crontab entries.
     */
    @Override
    public List<EventCronEntry> getEntries() {
        try {
            return scheduler.getCurrentlyExecutingJobs().stream()
                    .map(context -> (EventCronEntry) context.getMergedJobDataMap().get(KEY_EVENT_GENERATE_ENTRY))
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            throw Utils.wrapException(getName(), e);
        }
    }
}
