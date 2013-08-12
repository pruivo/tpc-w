package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class LoadBalance {
	private static int numberOfServers;
	private static final String lbConfigFile = "lb.conf";
	private static String[] serverAddresses;
	private static LoadBalancePolicy policy;
	private static LoadBalanceConfig conf;
	
	static {//static init
		try {
			conf = LoadBalanceConfig.loadConfig(Thread.currentThread().getContextClassLoader().getResource(lbConfigFile));
			//conf = LoadBalanceConfig.loadConfig((new File("/path_to_tpcw/loadbalancer/conf/lb.conf")).toURI().toURL());
			numberOfServers = conf.numberOfReplicas;
			serverAddresses = conf.replicaAddresses;
			policy = conf.getPolicyInstance();
			System.out.println("LoadBalanceConfig is = " + conf);
			System.out.println("\nLoadBalancer initialized!\n\n");
		} catch (Exception ex) {
			System.out.println("LoadBalancer::we got an exception at init");
			ex.printStackTrace();
		}
	}
	
	public LoadBalance() {}
	
	public static final URL getUrl(HttpServletRequest req) throws IOException {
		return policy.getUrl(req);
	}
}

