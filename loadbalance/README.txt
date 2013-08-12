This readme file contains instructions about how to setup and deploy a multiple node/instance version of the TPC-W 
benchmark with a load-balancer front-end.

This file focuses on the information necessary for configuring the load-balancer. For information regarding the client/server 
please consult the appropriate readme file.

The current implementation of the TPC-W load-balancer supports 3 and 4 server node deployment configurations as well as 3 
alternative request distribution policies, namely:

* RoundRobin - classic round-robin request distribution policy.

* LDA - request distribution policy that seeks to maximize data locality by clustering application data and functionality. 
In practice, it makes sure that every available server node is responsible for processing request types (services) which 
belong to a particular sub-group of all available requests types. The contents of the clusters-of-functionality-types are 
established through the use of the current state-of-the-art in clustering algorithms Latent Dirichlet Allocation (LDA). 
This policy only focuses on maximizing data locality and improving the efficiency of the target application in terms of 
usage of computational resources (mainly cpu and memory). It does not make any effort to distribute incoming requests in 
a way that leads to a uniform load distribution among existing nodes.

* LARD - a theoretically idealized request distribution policy that makes use of perfect a-priori knowledge about the target 
application behaviour. This policy incorporates knowledge covering the average time necessary for processing all request 
types, as well as the relative proportions in their invocations at run-time. By using this information, the policy makes 
an attempt to obtain a more uniform load-distribution among existing nodes, while, similarly to LDA, keeping each server 
node responsible for processing only a particular sub-group of requests types. It should be noted that the LARD policy does 
not take into account the actual domain data necessary for the execution of requests, when establishing the contents of the 
groups of requests that each server node should be responsible for. Last but not least, this policy was created for the 
purpose of comparing it against the developed LDA-based policy. The LARD policy is not something that can be realistically 
achieved in practice.

For more information about the policies, their effects and comparative results, please consult:
Garbatov, S. and Cachopo J. (2012). "Explicit use of working-set correlation for load-balancing in clustered web servers". 
Proceedings of the Seventh International Conference on Software Engineering Advances (ICSEA 2012), Lisbon, Portugal.


There are 2 points in the loadbalancer that need to be configured to have it operating properly. 

The first of these is to ensure that the lb.conf configuration file is available in the classpath of the application. 
If you have trouble doing this, then edit the 

tpcw/loadbalancer/src/java/pt/ist/fenixframework/example/tpcw/loadbalance/LoadBalance.java

and uncomment the

//conf = LoadBalanceConfig.loadConfig((new File("/path_to_tpcw/loadbalancer/conf/lb.conf")).toURI().toURL())

making sure that you indicate the proper path to the configuration file. 


An example of the contents of a configuration file, for the RoundRobin policy, with 3 server nodes follows:

policy_class=pt.ist.fenixframework.example.tpcw.loadbalance.RoundRobinPolicy
number_of_replicas=3
replica1=http://node02:18080/tpcw
replica2=http://node03:18080/tpcw
replica3=http://node04:18080/tpcw

Configuration files, for the LARD, LDA and RoundRobin policies, for 3 and 4 server nodes deployment can be found in the 
tpcw/loadbalancer/conf folder:

LARDPolicy_3replica.conf
LARDPolicy_4replica.conf
LDAPolicy_3replica.conf
LDAPolicy_4replica.conf
RoundRobinPolicy_3replica.conf
RoundRobinPolicy_4replica.conf


The only changes necessary to be done on these files would be to change the information about the correct URL 
where the TPC-W replicas have been deployed. Depending on the policy with which the load-balancer should operate, 
the appropriate configuration file should be renamed to lb.conf and added to the application classpath.


The second aspect that needs to be configured (only for the LDA and LARD policies) is the path to the serialized 
version of the contents of the groups of request types that should be processed by each server node. This can be 
accomplished by editting the localPath variable in

tpcw/loadbalancer/src/java/pt/ist/fenixframework/example/tpcw/loadbalance/LDAPolicy.java
or
tpcw/loadbalancer/src/java/pt/ist/fenixframework/example/tpcw/loadbalance/LARDPolicy.java

to point to the correct path where this data is available. The folders for the LARD policy are:

tpcw/loadbalancer/data_clustering/results/3clusters_lard/
tpcw/loadbalancer/data_clustering/results/4clusters_lard/

while for the LDA policy are:

tpcw/loadbalancer/data_clustering/results/3clusters_lda/
tpcw/loadbalancer/data_clustering/results/4clusters_lda/


Last but not least, after having deployed the TPC-W server nodes and the load-balancer, make sure that the workload 
generator (in the client) is configured to send all requests to the URL where the load-balancer has been deployed.
