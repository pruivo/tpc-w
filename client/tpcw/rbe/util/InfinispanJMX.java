package rbe.util;

import com.sun.management.OperatingSystemMXBean;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/8/12
 * Time: 4:18 PM
 *
 * @author Pedro Ruivo
 */
public class InfinispanJMX {

   private MBeanServerConnection mBeanServer;
   private MemoryUsage heapMemory;
   private MemoryUsage nonHeapMemory;
   private final CPUData cpuData;
   private JMXServiceURL target;

   //local JVM
   public InfinispanJMX() {
      mBeanServer = ManagementFactory.getPlatformMBeanServer();
      cpuData = new CPUData();
      target = null;
      try {
         initMemoryAndOSBeans();
      } catch (IOException e) {
         //I think this never happens. the connection to the local VM never will be lost...
      }
   }

   //remote JVM
   public InfinispanJMX(String host, String port) throws MalformedURLException {
      cpuData = new CPUData();
      target = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+ port +"/jmxrmi");
      tryConnect();
   }

   //debugging
   public static void main(String[] args) throws Exception {
      byte input[] = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
      BigInteger maxLong = new BigInteger(input);
      System.out.println(new UUID(maxLong.longValue(), maxLong.longValue()));

      String hostname = "cloudtm.ist.utl.pt";
      String port = "8081";

      InfinispanJMX jmx = new InfinispanJMX(hostname, port);

      for(String s : jmx.mBeanServer.getDomains()) {
         System.out.println("domain found: " + s);
      }

      System.out.println("Caches Found:\n" + jmx.getAllCacheNames());
      System.out.println("Channels Found:\n" + jmx.getAllChannelNames());

      /*ObjectName tlStats = InfinispanUtils.getCacheComponent("StreamLibStatistics");
      for(MBeanAttributeInfo a : jmx.mBeanServer.getMBeanInfo(tlStats).getAttributes()) {
          System.out.println(a.getName());
      }*/

      System.out.println("used heap memory (%): " + jmx.getPercentOfUsedHeapMemory());
      System.out.println("used non-heap memory (%): " + jmx.getPercentOfUsedNonHeapMemory());
      System.out.println("cpu usage (%): " + jmx.getCPUUsagePercent());
   }

   private void initMemoryAndOSBeans() throws IOException {
      MemoryMXBean memory = ManagementFactory.newPlatformMXBeanProxy(mBeanServer,
                                                                     ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
      heapMemory = memory.getHeapMemoryUsage();
      nonHeapMemory = memory.getNonHeapMemoryUsage();

      OperatingSystemMXBean osBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServer,
                                                                              ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

      RuntimeMXBean runtimeMXBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServer,
                                                                             ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);

      synchronized (cpuData) {
         cpuData.osBean = osBean;
         cpuData.runtimeBean = runtimeMXBean;
         cpuData.nCPUs = osBean.getAvailableProcessors();
         cpuData.prevUpTime = runtimeMXBean.getUptime();
         cpuData.prevProcessCpuTime = osBean.getProcessCpuTime();
      }
   }

   private synchronized void tryConnect() {
      try {
         JMXConnector connector = JMXConnectorFactory.connect(target);
         mBeanServer = connector.getMBeanServerConnection();
         initMemoryAndOSBeans();
      } catch (IOException e) {
         resetMBeans();
      }
   }

   private void resetMBeans() {
      mBeanServer = null;
      heapMemory = null;
      nonHeapMemory = null;
   }

   private Object getAttribute(ObjectName component, String attr) {
      try {
         return mBeanServer.getAttribute(component, attr);
      } catch (IOException e) {
         tryConnect();
      } catch (Exception e) {
         //attr not found or another problem
      }
      try {
         return mBeanServer.getAttribute(component, attr);
      } catch (IOException e) {
         tryConnect();
      } catch (Exception e) {
         //attr not found or another problem
      }
      return InfinispanUtils.ATTRIBUTE_NOT_AVAILABLE;
   }

   public Map<String, Object> getCacheStats(String cacheManagerName, String cacheName) {
      try {
         Map<String, Object> stats = new HashMap<String, Object>();

         if(mBeanServer == null) {
            tryConnect();
            return stats;
         }

         ObjectName deadLockManager = InfinispanUtils.getCacheComponent(cacheName, cacheManagerName,
                                                                        "DeadlockDetectingLockManager");
         ObjectName transactions = InfinispanUtils.getCacheComponent(cacheName, cacheManagerName, "Transactions");
         ObjectName tlStats = InfinispanUtils.getCacheComponent(cacheName, cacheManagerName, "TLStatistics");
         ObjectName streamLibStats = InfinispanUtils.getCacheComponent(cacheName, cacheManagerName,
                                                                       "StreamLibStatistics");

         for(InfinispanUtils.CacheAttributes attribute : InfinispanUtils.CacheAttributes.values()) {
            String attrName = attribute.toString();
            switch(attribute) {
               case TotalNumberOfDetectedDeadlocks:
                  stats.put(attrName, getAttribute(deadLockManager, attrName));
                  break;
               case Prepares:
               case Commits:
               case Rollbacks:
               case LocalPrepares:
               case LocalCommits:
               case LocalRollbacks:
               case AvgSuccessfulTxCommit:
               case AvgFailedTxCommit:
               case SuccessfulCommits:
               case FailedCommits:
                  stats.put(attrName, getAttribute(transactions, attrName));
                  break;
               case RemoteTopPuts:
               case TopLockFailedKeys:
               case TopLockedKeys:
               case TopContendedKeys:
               case LocalTopPuts:
               case LocalTopGets:
               case RemoteTopGets:
               case TopWriteSkewFailedKeys:
                  stats.put(attrName, getAttribute(streamLibStats, attrName));
                  break;
               default:
                  stats.put(attrName, getAttribute(tlStats, attrName));

            }
         }

         return stats;
      } catch(NullPointerException e) {
         tryConnect();
      } catch (Exception e) {
         System.err.println("warning: error getting stats from MBean Server. " + e.getLocalizedMessage());
      }
      return Collections.emptyMap();
   }

   public Map<String, Object> getChannelStats(String channelName) {
      try {
         Map<String, Object> stats = new HashMap<String, Object>();

         if(mBeanServer == null) {
            tryConnect();
            return stats;
         }

         ObjectName channel = InfinispanUtils.getChannelComponent(channelName);

         for(InfinispanUtils.ChannelAttributes attribute : InfinispanUtils.ChannelAttributes.values()) {
            String attrName = attribute.toString();
            stats.put(attrName, getAttribute(channel, attrName));
         }

         return stats;
      } catch(NullPointerException e) {
         tryConnect();
      } catch (Exception e) {
         System.err.println("warning: error getting stats from MBean Server. " + e.getLocalizedMessage());
      }
      return Collections.emptyMap();
   }

   /*
  case SentBytes:
                  case SentMessages:
                  case ReceivedBytes:
                  case ReceivedMessages:
                      if(mBeanServer.isRegistered(channel)) {
                          stats.put(attrName, getAttribute(channel, attrName));
                      } else {
                          stats.put(attrName, InfinispanUtils.ATTRIBUTE_NOT_AVAILABLE);
                      }
                      break;
   */

   public float getPercentOfUsedHeapMemory() {
      if(heapMemory == null) {
         tryConnect();
         return -1;
      }
      long used = heapMemory.getUsed();
      long max = heapMemory.getMax();

      return used * 100.0f/ max;
   }

   public float getPercentOfUsedNonHeapMemory() {
      if(nonHeapMemory == null) {
         tryConnect();
         return -1;
      }
      long used = nonHeapMemory.getUsed();
      long max = nonHeapMemory.getMax();

      return used * 100.0f / max;
   }

   //see http://knight76.blogspot.com/2009/05/how-to-get-java-cpu-usage-jvm-instance.html
   public float getCPUUsagePercent() {
      if(mBeanServer == null) {
         tryConnect();
         return -1;
      }

      synchronized (cpuData) {
         float cpuUsage = 0.001f;
         long upTime = cpuData.runtimeBean.getUptime();
         long processCpuTime = cpuData.osBean.getProcessCpuTime();

         if (cpuData.prevUpTime > 0L && upTime > cpuData.prevUpTime) {
            long elapsedCpu = processCpuTime - cpuData.prevProcessCpuTime;
            long elapsedTime = upTime - cpuData.prevUpTime;
            cpuUsage = Math.min(99f, elapsedCpu / (elapsedTime * 10000f * cpuData.nCPUs));
         }

         cpuData.prevUpTime = upTime;
         cpuData.prevProcessCpuTime = processCpuTime;

         return cpuUsage;
      }
   }

   //each entry has a cache manager and a list of caches related to the cache manager
   public Map<String, Set<String>> getAllCacheNames() {
      if(mBeanServer == null) {
         tryConnect();
         return Collections.emptyMap();
      }

      Map<String, Set<String>> results = new HashMap<String, Set<String>>();
      try {
         for(ObjectName name : mBeanServer.queryNames(null, null)) {
            if(name.getDomain().equals("org.infinispan")) {
               String type = name.getKeyProperty("type");

               if("Cache".equals(type)) {
                  String cacheName = name.getKeyProperty("name");
                  String cacheManagerName = name.getKeyProperty("manager");
                  Set<String> caches = results.get(cacheManagerName);
                  if(caches == null) {
                     caches = new HashSet<String>();
                     results.put(cacheManagerName, caches);
                  }
                  caches.add(cacheName);
               }
            }
         }

      } catch (IOException e) {
         tryConnect();
         return Collections.emptyMap();
      }
      return results;
   }

   public Set<String> getAllChannelNames() {
      if(mBeanServer == null) {
         tryConnect();
         return Collections.emptySet();
      }

      Set<String> results = new HashSet<String>();
      try {
         for(ObjectName name : mBeanServer.queryNames(null, null)) {
            if(name.getDomain().equals("org.infinispan")) {
               String type = name.getKeyProperty("type");

               if("channel".equals(type)) {
                  results.add(name.getKeyProperty("cluster"));

               }
            }
         }

      } catch (IOException e) {
         tryConnect();
         return Collections.emptySet();
      }
      return results;
   }

   private class CPUData {
      OperatingSystemMXBean osBean;
      RuntimeMXBean runtimeBean;

      int nCPUs;
      long prevUpTime;
      long prevProcessCpuTime;
   }


}
