package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.enums.RemoteExecAction;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by nicolas on 26/09/2016.
 */
public class NamingUtilsTest {

    @org.junit.Test
    public void classicRootArchive() throws Exception {
        String context = NamingUtils.getContext.apply("ROOT.war");
        assertThat(context, Matchers.equalToIgnoringCase("/"));

        context = NamingUtils.getContext.apply("ROOT.WAR");
        assertThat(context, Matchers.equalToIgnoringCase("/"));

        context = NamingUtils.getContext.apply("root.war");
        assertThat(context, Matchers.equalToIgnoringCase("/"));
    }

    @org.junit.Test
    public void classicArchive() throws Exception {
        String context = NamingUtils.getContext.apply("helloworld.war");
        assertThat(context, Matchers.equalToIgnoringCase("/helloworld"));

        context = NamingUtils.getContext.apply("HELLOWORLD.WAR");
        assertThat(context, Matchers.equalToIgnoringCase("/helloworld"));
    }
}