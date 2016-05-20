package fr.treeptik.cloudunit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

/**
 * Created by nicolas on 20/05/2016.
 */
public class ImageListTest {

    @Test
    public void testFoo() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, ImageList> imageLists = new TreeMap<>();

        ImageList imageListTomcat = new ImageList();
        imageListTomcat.add("tomcat-8.0.35", true);
        imageListTomcat.add("tomcat-8.0.21", false);
        imageLists.put("tomcat", imageListTomcat);

        ImageList imageListJBoss = new ImageList();
        imageListJBoss.add("jboss-8.0", false);
        imageListJBoss.add("jboss-10", true);
        imageLists.put("jboss", imageListJBoss);

        try {
            String jsonInString = mapper.writeValueAsString(imageLists);
            String expected = "{\"jboss\":{\"images\":[{\"name\":\"jboss-8.0\",\"favorite\":false},{\"name\":\"jboss-10\",\"favorite\":true}]},\"tomcat\":{\"images\":[{\"name\":\"tomcat-8.0.35\",\"favorite\":true},{\"name\":\"tomcat-8.0.21\",\"favorite\":false}]}}";
            Assert.assertEquals(jsonInString, expected);

            // inverse two fields
            String wrong = "{\"jboss\":{\"images\":[{\"name\":\"jboss-10\",\"favorite\":false},{\"name\":\"jboss-8-0\",\"favorite\":true}]},\"tomcat\":{\"images\":[{\"name\":\"tomcat-8.0.35\",\"favorite\":true},{\"name\":\"tomcat-8.0.21\",\"favorite\":false}]}}";
            Assert.assertNotEquals(jsonInString, wrong);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}