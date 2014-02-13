package kz.bee.mdb;

import java.io.IOException;
import java.util.Random;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

@SuppressWarnings("serial")
@WebServlet("/jmsServlet")
public class MessageServlet extends HttpServlet {
   
   @Resource(name="java:/ConnectionFactory1Mgmt")
   private ConnectionFactory _connectionFactory;
   
   
   private static final Logger _log = Logger.getLogger(MessageServlet.class);
   private static final String DEFAULT_MESSAGE = "Hello, World!";
   private static final int DEFAULT_COUNT = 1;
   
   
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
       doPost(req, resp);
   }
   
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		   String content = (req.getParameter("message") == null) ? DEFAULT_MESSAGE : req.getParameter("message");
		   String countStr = (req.getParameter("count") == null) ? "" : req.getParameter("count");
		   
		   int count;
		   try{
			   count = Integer.parseInt(countStr);
		   }catch(Exception e){
			   count = DEFAULT_COUNT;
		   }
		   
		   Random r = new Random();
		   for(int i =0; i< count; i++){
			   int q = r.nextInt(500);
			   messageSender("Queue"+q, "[Queue"+q+"]"+content);
		   }
		   
   }
   
   public void messageSender(String destination, String content){
	   try{
		   Connection connection = _connectionFactory.createConnection();
		   Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
	       Queue queue = session.createQueue(destination);
		   MessageProducer producer = session.createProducer( queue );
		   connection.start();
		   TextMessage message = session.createTextMessage();
		   message.setText(content);
		   producer.send( message );
		   session.close();
		   connection.close();
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
   }
}
