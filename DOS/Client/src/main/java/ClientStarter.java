import GUI.GUIMain;
import service.IDOSService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientStarter {
    private static final Logger _logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            var factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
            var server = (IDOSService)factory.getBean("appService");
            System.out.println("Obtained a reference to remote DOS server.");

            _logger.info("Obtained a reference to remote DOS server.");

            GUIMain.main(server, args);
        } catch (Exception e) {
            _logger.fatal(e);

            e.printStackTrace();
        }
    }
}
