package app.lsgui.remote;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpClientInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientInterface.class);

    private static final SslContextFactory sslContextFactory = new SslContextFactory();
    private static HttpClient client;

    private HttpClientInterface() {
    }

    public static synchronized HttpClient getClient() {
        if (client == null) {
            client = new HttpClient(sslContextFactory);
        }
        return client;
    }

    public static synchronized void startClient() {
        if (client != null && !client.isStarted()) {
            LOGGER.debug("Start HTTP Client");
            try {
                client.start();
            } catch (Exception e) {
                LOGGER.error("Could not start HTTP Client", e);
            }
        }
    }

    public static void stopClient() {
        if (client != null && client.isStarted() && !client.isStopped()) {
            LOGGER.debug("Stop HTTP Client");
            try {
                client.stop();
            } catch (Exception e) {
                LOGGER.error("Could not stop HTTP Client", e);
            }
        }
    }

}
