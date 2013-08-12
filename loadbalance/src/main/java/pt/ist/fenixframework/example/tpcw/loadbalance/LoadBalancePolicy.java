package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


public abstract class LoadBalancePolicy {
	protected static String[] serverAddresses;
	
	public LoadBalancePolicy(String[] serverAddresses) {
		this.serverAddresses = serverAddresses;
	}
	
	public abstract URL getUrl(HttpServletRequest req) throws IOException;
}