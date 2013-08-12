package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;


public final class LoadBalanceConfig {
	public String policyClassName = "";
	public int numberOfReplicas = 1;
	public String[] replicaAddresses;
	
	
	public LoadBalanceConfig() {}
	
	public static final LoadBalanceConfig loadConfig(URL configURL) {
		System.out.println("LoadBalanceConfig.loadConfig at "+ configURL);
		InputStream urlStream = null;
		DataInputStream in = null;
		BufferedReader d = null;
		String line = null;
		int i = 0;
		LoadBalanceConfig conf = new LoadBalanceConfig();
		try {
			urlStream = configURL.openStream();
			in = new DataInputStream(new BufferedInputStream(urlStream));
			d = new BufferedReader(new InputStreamReader(in));
			
			line = d.readLine();
			
			while (line != null) {
				if (line.startsWith("#") || line.startsWith("//")) {
					line = d.readLine();
					continue;
				}
				//System.out.println("config line: "+line);
				if (line.toLowerCase().startsWith("policy_class")) {
					conf.policyClassName = new String(line.substring(line.lastIndexOf("=")+1));
				} else if (line.toLowerCase().startsWith("number_of_replicas")) {
					conf.numberOfReplicas = new Integer(line.substring(line.lastIndexOf("=")+1));
					conf.replicaAddresses = new String[conf.numberOfReplicas];
				} else if (line.toLowerCase().startsWith("replica")) {
					conf.replicaAddresses[i] = new String(line.substring(line.lastIndexOf("=")+1));
					i++;
				}
				//and ignore everything else...
				line = d.readLine();
			}
		} catch (IOException e) {
			System.err.println("Cannot read " + configURL + ".  Ignoring it...");
			System.err.println("Returning default LoadBalance config:\n" + conf);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {}
			}
			if (urlStream != null) {
				try {
					urlStream.close();
				} catch (IOException ioe) {}
			}
		}
		return conf;
	}
	
	public LoadBalancePolicy getPolicyInstance() {
		try {
			//LoadBalanceConfig conf = LoadBalanceConfig.loadConfig((new File("/home/git/alchemist/tpcw_lb/conf/lb.conf")).toURI().toURL());
			System.out.println("LoadBalanceConfig::getPolicyInstance from " + this);
			Class lbClass = Class.forName(this.policyClassName);
			System.out.println("LB class is " + lbClass);
			Object[] input = new Object[1];
			
			input[0] = this.replicaAddresses;
			
			Constructor constr = lbClass.getConstructor(this.replicaAddresses.getClass());
			
			LoadBalancePolicy policy = (LoadBalancePolicy)constr.newInstance(input);
			
			return policy;
		} catch (Exception ex) {
			System.out.println("LoadBalanceConfig::getPolicyInstance we got an exception\n" + ex);
			ex.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		String res = "";
		res = "policyClassName="+policyClassName+"\nnumberOfReplicas="+numberOfReplicas
		+"\nreplicaAddresses="+replicaAddresses;
		for(String s : replicaAddresses) res += ("\n-" + s);
		return res;
	}
}
