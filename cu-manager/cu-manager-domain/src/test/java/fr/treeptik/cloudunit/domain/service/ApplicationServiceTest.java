package fr.treeptik.cloudunit.domain.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.ApplicationState;
import fr.treeptik.cloudunit.domain.core.Image;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.repository.ImageRepository;
import fr.treeptik.cloudunit.domain.service.impl.ApplicationServiceImpl;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public class ApplicationServiceTest {
    private static final String SERVICE_NAME = "service";
    private static final String APPLICATION_NAME = "boo";
    private static final String IMAGE_NAME = "service:1";
    
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private ImageRepository imageRepository;
    
    @Mock
    private OrchestratorService orchestratorService;
    
    private ApplicationServiceImpl applicationService;
    
    @Before
    public void setUp() {
        applicationService = new ApplicationServiceImpl();
        applicationService.setApplicationRepository(applicationRepository);
        applicationService.setImageRepository(imageRepository);
        applicationService.setOrchestratorService(orchestratorService);
    }

    @Test
    public void testAddService() {
        when(imageRepository.findByName(IMAGE_NAME))
        .thenReturn(Optional.of(new Image(IMAGE_NAME, SERVICE_NAME, ImageType.MODULE)));
        
        Application application = new Application(APPLICATION_NAME);
        
        applicationService.addService(application, IMAGE_NAME);
        
        assertEquals(application.getState(), ApplicationState.STOPPING);
    }
}
