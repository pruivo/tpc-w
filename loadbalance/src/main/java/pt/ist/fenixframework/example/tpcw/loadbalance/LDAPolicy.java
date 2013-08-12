package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import pt.ist.dap.structure.DataReader;


public class LDAPolicy extends LoadBalancePolicy {
	protected int numberOfServers;
	protected int[] serverRequests;
	protected boolean stats = false;
	/*
	methodName->methodId	//methodIds
	methodId->clusterId
	methodName.hashCode->clusterId
	id->methodName			//reverseMethodIds
	*/
	protected HashMap<String,Integer> servletNameToClusterIds;
	protected HashMap<Integer,Integer> methodHashCodesToClusterIds;
	//protected String localPath = "path_to_tpcw_folder/tpcw/loadbalancer/clustering/results/4clusters_lda/";
	protected String localPath = "path_to_tpcw_folder/tpcw/loadbalancer/clustering/results/3clusters_lda/";


	public LDAPolicy(String[] serverAddresses) {
		super(serverAddresses);
		numberOfServers = serverAddresses.length;
		serverRequests = new int[numberOfServers];

		//System.out.println("Loading input data from "+localPath);

		servletNameToClusterIds = (HashMap<String,Integer>) DataReader.loadSerializedObject(localPath+"servletNameToClusterId.serialized");
		methodHashCodesToClusterIds = (HashMap<Integer,Integer>) DataReader.loadSerializedObject(localPath+"hashCodesToClusterIds.serialized");

		if (servletNameToClusterIds != null && methodHashCodesToClusterIds != null) {
			for (String s : servletNameToClusterIds.keySet()) {
				//System.out.println("Servlet["+s+"] in cluster["+methodHashCodesToClusterIds.get(s.hashCode())+"]");
			}
		} else {
			throw new Error("Could not load properly the load balancing input data. Terminating application");
		}
	}

	private String getServerAddress(String servletName) {
		//System.out.println("finding server for "+servletName);
		Integer res = methodHashCodesToClusterIds.get(servletName.hashCode());
		if (res != null) {
			//System.out.println("servlet "+servletName+" going to "+res);
			return serverAddresses[res % numberOfServers];
		} else {
			//System.out.println("could not find info on servlet "+servletName);
			return serverAddresses[0];
		}
	}

	public URL getUrl(HttpServletRequest req) throws IOException {
		String servletPath = req.getServletPath();

		String selectedServerFullPath = getServerAddress(servletPath);
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
