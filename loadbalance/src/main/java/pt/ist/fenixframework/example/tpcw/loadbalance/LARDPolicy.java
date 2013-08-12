package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import pt.ist.dap.structure.DataReader;


public class LARDPolicy extends LDAPolicy {
	//protected String localPath = "path_to_tpcw_folder/tpcw/loadbalancer/clustering/results/4clusters_lard/";
	protected String localPath = "path_to_tpcw_folder/tpcw/loadbalancer/clustering/results/3clusters_lard/";

	public LARDPolicy(String[] serverAddresses) {
		super(serverAddresses);
	}
}
