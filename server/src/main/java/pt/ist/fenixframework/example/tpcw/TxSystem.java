package pt.ist.fenixframework.example.tpcw;

public abstract class TxSystem {

    // private static final String TX_SYS_PROP_NAME = "txsystem.classname";
    private static final String TX_MGR_FF = "pt.ist.fenixframework.example.tpcw.ff.FFTxManager";
    private static final String TX_MGR_HIB = "pt.ist.fenixframework.example.tpcw.hib.HibOgmTxManager";
    private static final String TX_MGR_ISPN = "pt.ist.fenixframework.example.tpcw.ispn.IspnTxManager";

    private static final String[] TX_MGR = new String[]{ TX_MGR_FF, TX_MGR_HIB, TX_MGR_ISPN };


    private static TxManager createTxManagerInstance() {
        // lookup any of the known classes that implement the TxManager
        for (String txMgrClassname : TX_MGR) {
            System.out.println("Trying to find TxManager class: " + txMgrClassname);
            try {
                Class<TxManager> txMgr = (Class<TxManager>)Class.forName(txMgrClassname);
                System.out.println("Found TxManager class: " + txMgrClassname);
                return txMgr.newInstance();
            } catch (ClassNotFoundException cnfe) {
                System.out.println("Could not find: " + txMgrClassname);
            } catch (InstantiationException ie) {
                System.out.println("ERROR: Could not instantiate: " + txMgrClassname);
            } catch (IllegalAccessException iae) {
                System.out.println("ERROR: Could not access: " + txMgrClassname);
            }
        }
        System.out.printf("ERROR: Couldn't find any suitable TxManager");
        System.exit(1);
        return null;
    }

    private static final TxManager txManager = createTxManagerInstance();
    // private static final DomainFactory domainFactory = txManager.makeDomainFactory();

    public static TxManager getManager() {
        return txManager;
    }

    // public static DomainFactory getDomainFactory() {
    //     return domainFactory;
    // }

    // public void save(Object obj) {
    //     // do nothing by default
    // }

    // public abstract <T extends pt.ist.fenixframework.DomainObject> T fromOID(Class<T> clazz, long oid);
    // public abstract DomainFactory makeDomainFactory();

    // public abstract <T> T getRoot();
    // public abstract <T> T getDomainObject(Class<T> clazz, Object oid);
    // public abstract void withTransaction(TransactionalCommand command);
}
