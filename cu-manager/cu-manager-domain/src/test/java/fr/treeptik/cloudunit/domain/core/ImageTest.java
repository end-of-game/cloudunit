package fr.treeptik.cloudunit.domain.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.treeptik.cloudunit.orchestrator.core.ImageType;

@RunWith(Parameterized.class)
public class ImageTest {
    @Parameters(name = "{index} {0}/{1}:{2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "cloudunit", "tomcat", "8" },
            { "cloudunit", "vertx-ruby", "3.3" },
            { null, "vert.x", "3.3-ruby" },
            { "cloudunit", "tomcat", null },
            { null, "fatjar", null },
        });
    }

    private final String namespace;
    private final String basename;
    private final String tag;
    private final String imageName;
    
    public ImageTest(String namespace, String basename, String tag) {
        this.namespace = namespace;
        this.basename = basename;
        this.tag = tag;
        
        StringBuilder imageNameBuffer = new StringBuilder();
        
        if (namespace != null && !namespace.isEmpty()) {
            imageNameBuffer.append(namespace);
            imageNameBuffer.append("/");
        }
        imageNameBuffer.append(basename);
        if (tag != null && !tag.isEmpty()) {
            imageNameBuffer.append(":");
            imageNameBuffer.append(tag);
        }
        this.imageName = imageNameBuffer.toString();
    }

    @Test
    public void testNamespace() {
        Image image = new Image(imageName, ImageType.SERVER);
        
        assertEquals(namespace, image.getNamespace());
    }
    
    @Test
    public void testBasename() {
        Image image = new Image(imageName, ImageType.SERVER);
        
        assertEquals(basename, image.getBasename());
    }
    
    @Test
    public void testTag() {
        Image image = new Image(imageName, ImageType.SERVER);
        
        assertEquals(tag, image.getTag());
    }
}
