package com.szlachto.moneytransfer;

import com.szlachto.moneytransfer.dao.DAOFactory;
import com.szlachto.moneytransfer.service.AccountService;
import com.szlachto.moneytransfer.service.CustomerService;
import com.szlachto.moneytransfer.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Init {
    private static final Logger LOGGER = LogManager.getLogger(Init.class);

    public static void main(String... args) {

        LOGGER.info("Starting Money Transfer RESTful Application");
        DAOFactory.getDAOFactory(DAOFactory.H2).populateTestData();
        LOGGER.info("Initialisation Complete");

        try {
            startService();
        } catch (Exception e) {
            LOGGER.error("Faild to start server.", e);
        }
    }

    private static void startService() throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                CustomerService.class.getCanonicalName() + ","
                        + TransactionService.class.getCanonicalName() + ","
                        + AccountService.class.getCanonicalName());
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

}
