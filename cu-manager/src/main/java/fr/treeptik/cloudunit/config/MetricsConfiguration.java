package fr.treeptik.cloudunit.config;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.Timer;
import fr.treeptik.cloudunit.service.ApplicationService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;

@Configuration
public class MetricsConfiguration {

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private Counter findAppApplicationsCalls = metricRegistry.counter("find-all-applications");

    @Autowired
    private ApplicationService applicationService;

    @Bean(name = "metricRegistry")
    MetricRegistry provideMetricsRegistry(){
        return metricRegistry;
    }

    @Bean
    Counter applicationsStarted() {
        return metricRegistry.counter("applications-started");
    }

    @Bean
    Counter applicationsStopped() {
        return metricRegistry.counter("applications-stopped");
    }

    @Bean
    Counter applicationsDeleted() {
        return metricRegistry.counter("applications-deleted");
    }

    @Bean
    Timer applicationsCreation() {
        return metricRegistry.timer("applications-creation");
    }

    @Bean
    Counter findAllApplicationCalls() {
        return findAppApplicationsCalls;
    }

    @PostConstruct
    public void init() {
        configureReporters();
        evaluateCounters();
    }

    private void evaluateCounters() {
        metricRegistry.register("applicationsSize",
                (Gauge<Long>) () -> applicationService.countAllApplications());

        metricRegistry.register("applicationsRunningSize",
                (Gauge<Long>) () -> applicationService.countAllRunningApplications());
    }

    private void configureReporters() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));

        reporter.start(1, TimeUnit.MINUTES);
    }


}