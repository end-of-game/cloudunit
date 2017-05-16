package fr.treeptik.cloudunit.domain.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.ApplicationState;
import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.service.impl.ApplicationServiceImpl;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;

@RunWith(Parameterized.class)
public class ApplicationService_updateContainerStateTest {
    private static final String SERVICE_NAME = "service";
    private static final String APPLICATION_NAME = "boo";
    private static final String IMAGE_NAME = "service:1";

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { ContainerState.STOPPING, ContainerState.STOPPED, ApplicationState.STOPPING, ApplicationState.STOPPED },
            { ContainerState.STOPPED, ContainerState.STOPPING, ApplicationState.STOPPED, ApplicationState.STOPPING },
            { ContainerState.STOPPED, ContainerState.STARTING, ApplicationState.STARTING, ApplicationState.STARTING },
            { ContainerState.STARTING, ContainerState.STARTED, ApplicationState.STARTING, ApplicationState.STARTED },
            { ContainerState.STOPPED, ContainerState.STARTING, ApplicationState.STARTING, ApplicationState.STARTING },
        });
    }
    
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private OrchestratorService orchestratorService;
    
    private ApplicationServiceImpl applicationService;
    
    private final ContainerState oldContainerState;
    private final ContainerState newContainerState;
    
    private final ApplicationState oldApplicationState;
    private final ApplicationState newApplicationState;
    
    public ApplicationService_updateContainerStateTest(
            ContainerState oldContainerState,
            ContainerState newContainerState,
            ApplicationState oldApplicationState,
            ApplicationState newApplicationState) {
        this.oldContainerState = oldContainerState;
        this.newContainerState = newContainerState;
        this.oldApplicationState = oldApplicationState;
        this.newApplicationState = newApplicationState;
    }

    @Before
    public void setUp() {
        applicationService = new ApplicationServiceImpl();
        applicationService.setApplicationRepository(applicationRepository);
    }
    
    @Test
    public void testAssembleApplication() {
        Application application = assembleApplication();
        
        Optional<Service> serviceM = application.getService(SERVICE_NAME);
        
        assertThat(application.getState(), is(oldApplicationState));
        
        assertTrue(serviceM.isPresent());
        assertThat(serviceM.get().getState(), is(oldContainerState));
    }
    
    @Test
    public void testUpdateContainerState() {
        Application application = assembleApplication();
        assumeApplicationAssembled(application);
        Service service = application.getService(SERVICE_NAME)
                .orElseThrow(() -> new AssumptionViolatedException(""));
        
        applicationService.updateContainerState(application, service.getContainerName(), newContainerState);
        
        assertThat(application.getState(), is(newApplicationState));
        assertThat(service.getState(), is(newContainerState));
    }

    private void assumeApplicationAssembled(Application application) {
        Optional<Service> serviceM = application.getService(SERVICE_NAME);
        
        assumeThat(application.getState(), is(oldApplicationState));
        assumeTrue(serviceM.isPresent());
        assumeThat(serviceM.get().getState(), is(oldContainerState));
    }

    public Application assembleApplication() {
        Image image = new Image(IMAGE_NAME, SERVICE_NAME, ImageType.MODULE);
        
        Application application = new Application(APPLICATION_NAME);
        
        switch (oldApplicationState) {
        case STOPPED:
            break;
        case STOPPING:
            application.stop();
            break;
        case STARTED:
            application.start();
            application.started();
            break;
        case STARTING:
            application.start();
            break;
        case REMOVING:
            application.remove();
            break;
        default:
            break;
        }
        
        Service service = application.addService(image);
        service.setState(oldContainerState);
        return application;
    }
}
