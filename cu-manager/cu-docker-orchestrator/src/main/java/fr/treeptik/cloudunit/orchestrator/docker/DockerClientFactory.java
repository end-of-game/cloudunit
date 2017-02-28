package fr.treeptik.cloudunit.orchestrator.docker;

import java.net.URI;
import java.nio.file.Path;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;

public class DockerClientFactory implements FactoryBean<DockerClient>, InitializingBean {
    public static enum EndpointMode {
        SOCKET, HTTP;
    }
    
    private EndpointMode endpointMode;
    
    private Path certificatesPath;
    
    private URI uri;

    private Long connectTimeoutMillis;

    private Long readTimeoutMillis;

    private Integer connectionPoolSize;
    
    public EndpointMode getEndpointMode() {
        return endpointMode;
    }

    public void setEndpointMode(EndpointMode endpointMode) {
        this.endpointMode = endpointMode;
    }

    public Path getCertificatesPath() {
        return certificatesPath;
    }

    public void setCertificatesPath(Path certificatesPath) {
        this.certificatesPath = certificatesPath;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public long getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(long readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    @Override
    public DockerClient getObject() throws Exception {
        DefaultDockerClient.Builder builder;
        switch (endpointMode) {
        case SOCKET:
            builder = DefaultDockerClient.fromEnv();
            break;
        case HTTP:
            builder = DefaultDockerClient.builder().uri(uri);
            break;
        default:
            throw new IllegalStateException();
        }
        if (uri.getScheme().equals("https")) {
            DockerCertificates certs = new DockerCertificates(certificatesPath);
            builder.dockerCertificates(certs);
        }
        if (connectTimeoutMillis != null) {
            builder.connectTimeoutMillis(connectTimeoutMillis);
        }
        if (readTimeoutMillis != null) {
            builder.readTimeoutMillis(readTimeoutMillis);
        }
        if (connectionPoolSize != null) {
            builder.connectionPoolSize(connectionPoolSize);
        }
        return builder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return DockerClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(endpointMode, "Docker endpoint mode must not be null");
    }

}
