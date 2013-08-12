package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import pt.ist.dap.structure.DataReader;


public class ProfilePolicy extends LoadBalancePolicy {
	private int selector = 0;
	private int numberOfServers;
	private int[] serverRequests;
	private int totalRequests;
	private boolean stats = true;
	
	private HashMap<String,Integer> methodIds;
	private HashMap<Integer,Integer> methodIdsToClusterIds;
	private HashMap<Integer,Integer> methodHashCodesToClusterIds;
	private HashMap<Integer,String> reverseMethodIds;
	private HashMap<String,Integer> numberOfRequestsPerType;
	private HashMap<String,Integer> servletNames;
	private String localPath = "/home/sgarbatov/work/alchemist/tpcw/newData/clustering/results/";
	
	public ProfilePolicy(String[] serverAddresses) {
		super(serverAddresses);
		numberOfServers = serverAddresses.length;
		serverRequests = new int[numberOfServers];
		methodIds = (HashMap<String,Integer>) DataReader.loadSerializedObject(localPath+"methodNameToMethodId.serialized");
		methodIdsToClusterIds = (HashMap<Integer,Integer>) DataReader.loadSerializedObject(localPath+"methodIdToClusterId.serialized");
		methodHashCodesToClusterIds = (HashMap<Integer,Integer>) DataReader.loadSerializedObject(localPath+"methodHashCodeClusterId.serialized");
		reverseMethodIds = (HashMap<Integer,String>) DataReader.loadSerializedObject(localPath+"methodIdToMethodName.serialized");
		numberOfRequestsPerType = new HashMap<String,Integer>();
		servletNames = new HashMap<String,Integer>();
		
		
		for (Integer i : reverseMethodIds.keySet()) {
			reverseMethodIds.put(i, reverseMethodIds.get(i).replace("_doGet",""));//making sure there's no trailing _doGet in the names
			methodHashCodesToClusterIds.put(reverseMethodIds.get(i).hashCode(), methodIdsToClusterIds.get(i));
			System.out.println("Servlet["+i+"] "+reverseMethodIds.get(i)+" in cluster["+methodIdsToClusterIds.get(i)+"]");
			numberOfRequestsPerType.put(reverseMethodIds.get(i), 0);
		}
	}
	
	private String getNextServerAddress() {
		int serverIndex = selector++ % numberOfServers;
		if (stats) {
			totalRequests++;
			serverRequests[serverIndex]++;
			
			if ((totalRequests % 10000) == 0) {
				System.out.println("\n\nLoad Balance Stats:");
				for (int i = 0; i < numberOfServers; i++) {
					System.out.println("server index " + i + " has served " + serverRequests[i] + " requests.");
				}
				for (String reqType :  numberOfRequestsPerType.keySet()) {
					System.out.println(reqType + " has been called " + numberOfRequestsPerType.get(reqType) + " times");
				}
				System.out.println("list of servlet urls:");
				for (String reqName :  servletNames.keySet()) {
                                        System.out.println("\t--" + reqName + "-");
                                }

				
				System.out.println("\n\n");
			}
		}
		return serverAddresses[serverIndex];
	}
	
	public URL getUrl(HttpServletRequest req) throws IOException {
		String selectedServerFullPath = getNextServerAddress();
		String servletPath = req.getServletPath();
		String queryString = req.getQueryString();
		String newUrl = "";
		HttpSession session = req.getSession(false);

		if (!servletNames.containsKey(servletPath)) servletNames.put(servletPath, 1);

		String reqName = servletPath.replace("/","");
		reqName = reqName.replace("_servlet","");

		if (stats) {
			try {
				if (!numberOfRequestsPerType.containsKey(reqName)) numberOfRequestsPerType.put(reqName, 0);
				numberOfRequestsPerType.put(reqName, numberOfRequestsPerType.get(reqName)+1);
			} catch (Exception ex) {
				System.out.println("ex "+ex+"\n for reqType "+reqName);
			}
		}
		
		newUrl = selectedServerFullPath + servletPath;
		
		if (req.getRequestedSessionId() != null) newUrl = newUrl + ";jsessionid=" + req.getRequestedSessionId();
		
		if (queryString != null) newUrl = newUrl + "?" + queryString;
		
		//if (session != null) newUrl = newUrl + ";jsessionid=" + session.getId();
		
		return new URL(newUrl);
	}
	
}
