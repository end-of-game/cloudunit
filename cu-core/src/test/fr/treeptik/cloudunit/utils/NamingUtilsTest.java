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
    public void classicRootArchive1() throws Exception {
        String context = NamingUtils.getContext.apply("ROOT.war");
        assertThat(context, Matchers.equalToIgnoringCase("/"));
    }

    @org.junit.Test
    public void classicRootArchive2() throws Exception {
        String context = NamingUtils.getContext.apply("ROOT.WAR");
        assertThat(context, Matchers.equalToIgnoringCase("/"));
    }

    @org.junit.Test
    public void classicRootArchive3() throws Exception {
        String context = NamingUtils.getContext.apply("root.war");
        assertThat(context, Matchers.equalToIgnoringCase("/"));
    }

    @org.junit.Test
    public void classicArchive1() throws Exception {
        String context = NamingUtils.getContext.apply("helloworld.war");
        assertThat(context, Matchers.equalToIgnoringCase("/helloworld"));
    }

    public void classicArchive2() throws Exception {
        String context = NamingUtils.getContext.apply("HELLOWORLD.WAR");
        assertThat(context, Matchers.equalToIgnoringCase("/helloworld"));
    }

    @org.junit.Test
    public void checkProtocolUnix() throws Exception {
        String context = NamingUtils.protocolSocket.apply(true);
        assertThat(context, Matchers.equalToIgnoringCase("unix"));
    }

    @org.junit.Test
    public void checkProtocolHttp() throws Exception {
        String context = NamingUtils.protocolSocket.apply(false);
        assertThat(context, Matchers.equalToIgnoringCase("http"));
    }

}