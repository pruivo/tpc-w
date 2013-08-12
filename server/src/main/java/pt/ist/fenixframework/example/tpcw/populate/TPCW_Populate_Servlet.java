package pt.ist.fenixframework.example.tpcw.populate;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import tpcw_dto.*;

public class TPCW_Populate_Servlet extends HttpServlet {
    
  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
      try {
      String numItems  = req.getParameter("NUM_ITEMS");
      String numEbs = req.getParameter("NUM_EBS");
      String useIndexes = req.getParameter("USE_INDEXES");

      if (numItems == null) {
          numItems = "1000";
      }
      if (numEbs == null) {
          numEbs = "10";
      }
      if (useIndexes == null) {
	  useIndexes = "false";
      }

      // res.setCharacterEncoding("UTF-8");
      PrintWriter out = res.getWriter();
      // // Set the content type of this servlet's result.
      res.setContentType("text/html");
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD W3 HTML//EN\">");
      out.println("<HTML><HEAD><TITLE> Populate database via web request</TITLE></HEAD>");
      out.print("<BODY BGCOLOR=\"#ffffff\">");

      out.println("Populating database via web request with NUM_EBS=" + numEbs + "; NUM_ITEMS=" + numItems + ";USE_INDEXES=" + useIndexes + "...<br/> <br/>");
      out.flush();
      res.flushBuffer();
      Thread t = new Thread(new TPCW_Populate(numEbs, numItems, useIndexes));
      t.start();
      // TPCW_Populate.main(numEbs, numItems);
      try {
          t.join();
          out.println("Done!");
      } catch (InterruptedException ie) {
          out.println("Something went wrong:<br/>");
          out.println("<pre>");
          ie.printStackTrace(out);
          out.println("</pre>");
      } finally {
          out.print("</BODY> </HTML>\n");
          out.close();
      }
      return;
      } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
      }
    }
}
