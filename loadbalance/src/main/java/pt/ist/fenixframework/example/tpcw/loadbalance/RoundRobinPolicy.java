package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class RoundRobinPolicy extends LoadBalancePolicy {
	private int selector = 0;
	private int numberOfServers;
	private int[] serverRequests;
	private boolean stats = false;
	
	public RoundRobinPolicy(String[] serverAddresses) {
		super(serverAddresses);
		numberOfServers = serverAddresses.length;
		serverRequests = new int[numberOfServers];
	}
	
	private String getNextServerAddress() {
		int serverIndex = selector++ % numberOfServers;
		if (stats) serverRequests[serverIndex]++;
		return serverAddresses[serverIndex];
	}
	
	public URL getUrl(HttpServletRequest req) throws IOException {
		String selectedServerFullPath = getNextServerAddress();
		String servletPath = req.getServletPath();
		String queryString = req.getQueryString();
		String newUrl = "";
		HttpSession session = req.getSession(false);
		
		newUrl = selectedServerFullPath + servletPath;
		
		if (req.getRequestedSessionId() != null) newUrl = newUrl + ";jsessionid=" + req.getRequestedSessionId();
		
		if (queryString != null) newUrl = newUrl + "?" + queryString;
		
		//if (session != null) newUrl = newUrl + ";jsessionid=" + session.getId();
		
		return new URL(newUrl);
	}
	
}
