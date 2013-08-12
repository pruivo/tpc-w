package rbe.util;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/8/12
 * Time: 7:52 PM
 *
 * @author Pedro Ruivo
 */
public class InfinispanUtils {

   public static final String ATTRIBUTE_NOT_AVAILABLE = "N/A";

   public static enum CacheAttributes {
      TotalNumberOfDetectedDeadlocks,
      Commits,
      Prepares,
      Rollbacks,
      WriteTXDuration90Percentile,
      LocksAcquisitionRate,
      HoldTime,
      LocalExecNoCont,
      ReadOnlyTXDuration90Percentile,
      WriteTXDuration95Percentile,
      RollbackCommandCost,
      ApplicationContentionFactor,
      MaxReplayTime,
      //LocksInterArrivalHistogram,
      CommittedTransactionsWritePercentage,
      TimeoutExceptionOnPrepare,
      Throughput,
      WriteTransactionTotalExecutionTime,
      CommitCommandCost,
      ClusteredGetCommandSize,
      NumNodesInvolvedInPrepare,
      WriteTXDuration99Percentile,
      TransactionsWritePercentage,
      CommitCommandSize,
      ReadOnlyTXDuration99Percentile,
      Rtt,
      DeadlockExceptionOnPrepare,
      TransactionsArrivalRate,
      LocalContentionProbability,
      LockWaitingTime,
      ReadOnlyTransactionExecutionTime,
      WriteTransactionLocalExecutionTime,
      PerTransactionAcquiredLocks,
      PrepareCommandSize,
      RemoteGetCost,
      AvgReplayTime,
      ReadOnlyTXDuration95Percentile,
      PutsInLocalKeys,
      PutsInRemoteKeys,
      GetsInLocalKeys,
      GetsInRemoteKeys,
      RemoteTopPuts,
      TopLockFailedKeys,
      TopLockedKeys,
      TopContendedKeys,
      LocalTopPuts,
      LocalTopGets,
      RemoteTopGets,
      TopWriteSkewFailedKeys,
      LocalPrepares,
      LocalCommits,
      LocalRollbacks,
      AvgSuccessfulTxCommit,
      AvgFailedTxCommit,
      SuccessfulCommits,
      FailedCommits
   }

   public static enum ChannelAttributes {
      SentBytes,
      SentMessages,
      ReceivedBytes,
      ReceivedMessages
   }

   //org.infinispan:type=Cache,name="DomainCache(repl_sync)",manager="DefaultCacheManager",component=Cache
   public static ObjectName getCacheComponent(String cacheName, String cacheManagerName, String component)
         throws MalformedObjectNameException {
      StringBuilder sb = new StringBuilder("org.infinispan:type=Cache,name=")
            .append(cacheName.startsWith("\"") ? cacheName :
                          ObjectName.quote(cacheName))
            .append(",manager=").append(cacheManagerName.startsWith("\"") ? cacheManagerName :
                                              ObjectName.quote(cacheManagerName))
            .append(",component=").append(component);
      return new ObjectName(sb.toString());
   }

   //org.infinispan:type=CacheManager,name="DefaultCacheManager",component=CacheManager
   public static ObjectName getCacheManagerComponent(String cacheManagerName, String component)
         throws MalformedObjectNameException {
      StringBuilder sb = new StringBuilder("org.infinispan:type=CacheManager,name=")
            .append(cacheManagerName.startsWith("\"") ? cacheManagerName :
                          ObjectName.quote(cacheManagerName))
            .append(",component=").append(component);
      return new ObjectName(sb.toString());
   }

   //org.infinispan:type=channel,cluster="x"
   public static ObjectName getChannelComponent(String clusterName) throws MalformedObjectNameException {
      StringBuilder sb = new StringBuilder("org.infinispan:type=channel,cluster=")
            .append(clusterName.startsWith("\"") ? clusterName :
                          ObjectName.quote(clusterName));
      return new ObjectName(sb.toString());
   }

   //org.infinispan:type=protocol,cluster="x",protocol=FD_SOCK
   public static ObjectName getProtocolComponent(String clusterName, String component) throws MalformedObjectNameException {
      StringBuilder sb = new StringBuilder("org.infinispan:type=protocol,cluster=")
            .append(clusterName.startsWith("\"") ? clusterName :
                          ObjectName.quote(clusterName))
            .append(",component=").append(component);
      return new ObjectName(sb.toString());
   }

}
