package fr.treeptik.cloudunit.docker.core;

import com.spotify.docker.client.ApacheUnixSocket;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.Image;
import fr.treeptik.cloudunit.docker.model.Volume;
import fr.treeptik.cloudunit.dto.DockerResponse;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import jnr.unixsocket.UnixSocketAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;

/**
 * Created by guillaume on 19/10/16.
 */
public class UnixSocketDockerDriver implements DockerDriver, ConnectionSocketFactory {

    private File socketFile;

    private URI socketUri;
    
    public UnixSocketDockerDriver(URI socketUri){
        this.socketUri = socketUri;
    }

    public void init(){
        final String filename = socketUri.toString()
                .replaceAll("^unix:///", "unix://localhost/")
                .replaceAll("^unix://localhost", "");
        this.socketFile = new File(filename);
    }


    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return new ApacheUnixSocket();
    }


    @Override
    public DockerResponse find(DockerContainer container) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse findAll() throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse create(DockerContainer container) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse start(DockerContainer container) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse stop(DockerContainer container) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse kill(DockerContainer container) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse remove(DockerContainer container) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse findAnImage(Image image) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse commit(DockerContainer container, String tag, String repository) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse pull(String tag, String repository) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse removeImage(Image image) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse createVolume(Volume volume) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse findVolume(Volume volume) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public DockerResponse removeVolume(Volume volume) throws FatalDockerJSONException {
        return null;
    }

    @Override
    public Socket connectSocket(int connectTimeout,
                                Socket socket,
                                HttpHost host,
                                InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress,
                                HttpContext context) throws IOException {
        try {
            socket.connect(new UnixSocketAddress(socketFile), connectTimeout);
        } catch (SocketTimeoutException e) {
            throw new ConnectTimeoutException(e, null, remoteAddress.getAddress());
        }

        return socket;
    }
    public static URI sanitizeUri(final URI uri) {
        if (uri.getScheme().equals("unix")) {
            return URI.create("unix://localhost:80");
        } else {
            return uri;
        }
    }

    public File getSocketFile() {
        return socketFile;
    }

    public URI getSocketUri() {
        return socketUri;
    }
}
