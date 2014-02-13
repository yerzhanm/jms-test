package kz.bee.mdb;

import java.util.Date;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

/**
 * @author lang
 *
 */

public class MyMDB implements MessageListener{

	
	private static final Logger _log = Logger.getLogger(MyMDB.class);
	
	@Override
	public void onMessage(Message message) {
		
		try
        {
            if(message instanceof TextMessage)
            {
                String content = ((TextMessage)message).getText();

                _log.info("Received text message with contents: [" + content + "] at " + new Date());
                //Thread.sleep(30000);
            }
        }
        catch(Exception e)
        {
            _log.error(e.getMessage(), e);
        }
		
	}

}
