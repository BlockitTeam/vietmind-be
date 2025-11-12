package com.vm.config;

import org.jetbrains.annotations.NotNull;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Custom JobFactory that allows Quartz jobs to have Spring-managed dependencies injected.
 */
class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
    private final AutowireCapableBeanFactory beanFactory;

    AutowiringSpringBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @NotNull
    @Override
    protected Object createJobInstance(@NotNull TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        beanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(org.springframework.context.ApplicationContext applicationContext) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        // Ensure Quartz jobs get Spring dependencies injected
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        factory.setJobFactory(new AutowiringSpringBeanJobFactory(beanFactory));
        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        return schedulerFactoryBean.getScheduler();
    }
}
