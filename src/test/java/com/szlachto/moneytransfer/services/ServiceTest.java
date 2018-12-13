package com.szlachto.moneytransfer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szlachto.moneytransfer.dao.DAOFactory;
import com.szlachto.moneytransfer.service.AccountService;
import com.szlachto.moneytransfer.service.CustomerService;
import com.szlachto.moneytransfer.service.TransactionService;
import com.szlachto.moneytransfer.utils.PropertiesUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ServiceTest {
    protected static Server server = null;
    protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    protected static CloseableHttpClient client;
    protected static DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    protected ObjectMapper mapper = new ObjectMapper();
    final static URIBuilder URI_BUILDER = new URIBuilder().setScheme("http").setHost("localhost:8084");

    @BeforeAll
    public static void setup() throws Exception {
        PropertiesUtils.loadConfig("src\\test\\resources\\h2_db.properties");
        h2DaoFactory.populateTestData();
        startServer();
        connManager.setDefaultMaxPerRoute(100);
        connManager.setMaxTotal(200);
        client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();
    }

    @AfterAll
    public static void closeClient() {
        HttpClientUtils.closeQuietly(client);
    }


    private static void startServer() throws Exception {
        if (server == null) {
            server = new Server(8084);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                    CustomerService.class.getCanonicalName() + "," +
                            AccountService.class.getCanonicalName() + "," +
                            TransactionService.class.getCanonicalName());
            server.start();
        }
    }
}
