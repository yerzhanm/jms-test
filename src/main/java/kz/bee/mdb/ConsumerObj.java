package kz.bee.mdb;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.jboss.logging.Logger;

/**
 * @author lang
 *
 */
public class ConsumerObj {
	
	private static final Logger _log = Logger.getLogger(ConsumerObj.class);
	
	private ConnectionFactory connectionFactory = null;
    private Connection connection = null;
    private Session session = null;
    private Queue queue = null;
	private MessageConsumer consumer = null;
	private Date name;
	private String destination = "";
	
	public ConsumerObj(InitialContext ctx, String connFactory, String queueName, String destination){
		try{
			name = new Date();
			this.destination = destination;
			
			connectionFactory = (ConnectionFactory) ctx.lookup(connFactory);
			connection = connectionFactory.createConnection();
		    session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
	        queue = session.createQueue(destination);
			consumer = session.createConsumer( queue );
			MessageListenerBean ml = new MessageListenerBean(""+name.getTime()+" : "+destination);
			consumer.setMessageListener(ml);
			connection.start();
			_log.info("Created : ["+name.getTime()+" : "+destination+"]");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void closeResources(){
		if(consumer!=null)
			try { consumer.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		if(session!=null)
			try { session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		if(connection!=null)
			try { connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		
		_log.info("Closed : ["+name.getTime()+" : "+destination+"]");
	}

	public Date getName() {
		return name;
	}

	public String getDestination() {
		return destination;
	}
}
