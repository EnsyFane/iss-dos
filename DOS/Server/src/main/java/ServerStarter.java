import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerStarter {
    private static final Logger _logger = LogManager.getLogger();

    public static void main(String[] args) {
        _logger.info("Starting server.");

        System.out.println("Starting server...");
        try {
            var factory = new ClassPathXmlApplicationContext("classpath:spring-server.xml");

            _logger.info("Server started.");

            System.out.println("Server started.");
        } catch (Exception e) {
            _logger.fatal(e);

            e.printStackTrace();
        }
    }
}
