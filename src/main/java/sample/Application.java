package sample;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by algys on 08.02.17.
 */

@SpringBootApplication
public class Application {
   // AccountService accountService = new AccountService();
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}


