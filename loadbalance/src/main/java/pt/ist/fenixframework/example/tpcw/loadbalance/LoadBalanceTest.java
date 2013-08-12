package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;


public final class LoadBalanceTest {
	
	
	public static void main(String[] args) {
		try {
			LoadBalanceConfig conf = LoadBalanceConfig.loadConfig((new File("/home/git/alchemist/tpcw_lb/conf/lb.conf")).toURI().toURL());
			System.out.println("The config we got is: "+conf);
			
			LoadBalancePolicy plc = conf.getPolicyInstance();
			
			System.out.println("policy loaded = "+plc);
			
		} catch (Exception ex) {
			System.out.println("LoadBalanceTest we got an exception\n" + ex);
		}
	}
}