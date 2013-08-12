package pt.ist.fenixframework.example.tpcw;

public abstract class TxManager {

   public void save(Object obj) {
      // do nothing by default
   }

   public void setRootIdIfNeeded(Object rootObject) {
      // do nothing by default
   }

   public abstract <T> T getRoot();
   public abstract <T> T getDomainObject(Class<T> clazz, Object oid);
   public abstract <T> T withTransaction(TransactionalCommand<T> command);
    // When stopping the web container we need to stop JGroups.  This is here for convenience, but
    // we wnat this to go to a handler when unloading or stopping the web application
   public abstract void stop();
}
