package pt.ist.fenixframework.example.tpcw.messaging;

import pt.ist.fenixframework.messaging.RequestProcessor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class LARDRequestProcessor implements RequestProcessor {

    private static String getHTML(URL url) throws IOException {
        final StringBuilder html = new StringBuilder();
        final byte[] buffer = new byte[4096];
        int r;
        final BufferedInputStream in = new BufferedInputStream(url.openStream(), 4096);
        try {
            while ((r = in.read(buffer, 0, buffer.length)) != -1) {
                if (r > 0) {
                    html.append(new String(buffer, 0, r));
                }
            }
        } finally {
            try {
                in.close();
            } catch (IOException ioe) {
                System.out.println("TPCW_LB:Unable to close URL." + url.toExternalForm());
            }
        }

        return html.toString();
    }

    @Override
    public Object onRequest(String s) {
        try {
            return getHTML(createUrl(s));
        } catch (RuntimeException e) {
            System.out.println("TPCW_LB:Unable to process request: " + s + ": " + e.getLocalizedMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("TPCW_LB:Unable to process request: " + s + ": " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private URL createUrl(String action) throws MalformedURLException {
        return new URL("http://localhost:8080/tpcw/" + action);
    }

}
