import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.example.tpcw.loadbalance.LoadBalance;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class LoadBalanceHttpServlet extends HttpServlet {

    private static final boolean HTTP_LOAD_BALANCE = false;
    private static final String APP_NAME = "tpcw-server";

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (HTTP_LOAD_BALANCE) {
            httpLoadBalance(req, resp);
        } else {
            lardDispatcherLoadBalance(req, resp);
        }
    }

    private void httpLoadBalance(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println(TPCW_Util.getHTML(LoadBalance.getUrl(req)));
        out.close();
    }

    private void lardDispatcherLoadBalance(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final PrintWriter out = resp.getWriter();
        final StringBuilder newURL = new StringBuilder();
        final String servletPath = req.getServletPath();
        newURL.append(servletPath);
        final String sessionId = req.getRequestedSessionId();
        if (sessionId != null) {
            newURL.append(";jsessionid=").append(req.getRequestedSessionId());
        }
        final String queryString = req.getQueryString();
        if (queryString != null) {
            newURL.append("?").append(queryString);
        }

        try {
            out.println(FenixFramework.sendRequest(newURL.toString(), servletPath, APP_NAME, true));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
            e.printStackTrace();
        }
        out.close();
    }
}
