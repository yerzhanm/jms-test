package kz.bee.mdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

@SuppressWarnings("serial")
@WebServlet("/consumerRegister")
public class ConsumerRegisterServlet extends HttpServlet {
   
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
       doPost(req, resp);
   }
   
   
   private static final Logger _log = Logger.getLogger(ConsumerRegisterServlet.class);
   private Map<String, List> mcMap = new HashMap();
   private InitialContext context = null;
   private static final String CONNECTION_FACTORY = "ConnectionFactory1NonMgmt"; 
   private static final int DEFAULT_WORKERS_COUNT = 1;
   
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	   
	   
	   if((req.getParameter("action")!=null)&&(((String)req.getParameter("action")).equalsIgnoreCase("init"))){
		   initConsumers();
	   }
	   
	   try{
		   String countStr = (req.getParameter("count") == null) ? "" : req.getParameter("count");
		   int count;
		   try{
			   count = Integer.parseInt(countStr);
		   }catch(Exception e){
			   count = DEFAULT_WORKERS_COUNT;
		   }
		   
		   if((req.getParameter("queue")!=null)&&(req.getParameter("action")!=null)&&(req.getParameter("dest")!=null)){
			   if(((String)req.getParameter("action")).equals("create")){
				   createQueueConsumer(req.getParameter("queue"), req.getParameter("dest"), count);
			   }
			   if(((String)req.getParameter("action")).equals("close")){
				   stopQueueConsumer(req.getParameter("queue"));
			   }
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   String html = "";
	   for(Entry m : mcMap.entrySet()){
		   html+=m.getKey().toString()+"<br/>";
		   for(ConsumerObj co : (List<ConsumerObj>)m.getValue()){
			   html+="----"+co.getName()+" : "+co.getDestination()+"<br/>";
		   }
	   }
	   html+="<br/>";
	   
	   resp.setContentType("text/html");
	   resp.getWriter().write(html);
   }
   
   
   public void initConsumers(){
	   for(int i = 0; i<500; i++){
		   createQueueConsumer("queue"+i, "Queue"+i, 5);
	   }
   }
   
   
   private void initialize(){
	   try{
		   context = new InitialContext();
		}catch(Exception e){
		   e.printStackTrace();
	   }
   }
   
   private void createQueueConsumer(String queueName, String destination, int workersCount){
	   if(mcMap.get(queueName)==null){
		   try {
			   if(context==null) initialize();
			   List<ConsumerObj> cl = new ArrayList<ConsumerObj>();
			   for(int i=0; i<workersCount; i++){
				   ConsumerObj co = new ConsumerObj(context, CONNECTION_FACTORY, queueName, destination);
				   cl.add(co);
			   }
			   mcMap.put(queueName, cl);
			   _log.info("["+queueName+"] is registered with destination ["+destination+"], workers: "+workersCount+"!");
			}catch (Exception e) {
				e.printStackTrace();
			}
	   }else{
		   _log.info("["+queueName+"] is already exist!");
	   }
   }
   
   private void stopQueueConsumer(String queueName){
		   List<ConsumerObj> cl = mcMap.get(queueName);
		   if(cl!=null)
			   for(ConsumerObj co : cl){
				   co.closeResources();
			   }
		   mcMap.remove(queueName);
		   _log.info("["+queueName+"] is closed!");
   }
}