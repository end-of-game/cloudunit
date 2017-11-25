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
import fr.treeptik.cloudunit.service.VolumeAssociationService;
import fr.treeptik.cloudunit.service.VolumeService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class MetricsConfiguration {

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private Counter findAppApplicationsCalls = metricRegistry.counter("find-all-applications");

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private VolumeService volumeService;

    @Bean(name = "metricRegistry")
    MetricRegistry provideMetricsRegistry(){
        return metricRegistry;
    }

    @Bean
    Counter applicationsStartedCalls() {
        return metricRegistry.counter("applications-started");
    }

    @Bean
    Counter applicationsDeletedCalls() {
        return metricRegistry.counter("applications-deleted");
    }

    @Bean
    Timer applicationsCreation() {
        return metricRegistry.timer("applications-creation");
    }

    @Bean
    Timer volumesCreation() {
        return metricRegistry.timer("volumes-creation");
    }

    @Bean
    Counter findAllApplicationCalls() {
        return findAppApplicationsCalls;
    }

    @Bean
    Counter fileExplorerCalls() {
        return metricRegistry.counter("file-explorer-call");
    }

    @Bean
    Counter logsDisplayCalls() {
        return metricRegistry.counter("logs-display-call");
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

        metricRegistry.register("volumesSize",
                (Gauge<Integer>) () -> volumeService.count());
    }

    private void configureReporters() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));

        reporter.start(5, TimeUnit.MINUTES);
    }


}