package pt.ist.fenixframework.example.tpcw;

import pt.ist.fenixframework.CallableWithoutException;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import pt.ist.fenixframework.example.tpcw.domain.App;
import pt.ist.fenixframework.example.tpcw.messaging.LARDRequestProcessor;

public class Init {
    static {
        FenixFramework.registerReceiver(new LARDRequestProcessor());
        final TransactionManager manager = FenixFramework.getTransactionManager();
        manager.withTransaction(new CallableWithoutException<Void>() {
                public Void call() {
                    DomainRoot root = FenixFramework.getDomainRoot();
                    App app = root.getApp();
                    if (app == null) {
                        app = new App();
                        root.setApp(app);
                        //     app.initInstance();  // shouldn't be needed
                    }
                    return null;
                }
            });
    }

    /* The following method is invoked to initialize the application.  It has no code, but by
       invoking it, this class gets loaded and the previous static code runs once */
    public static void initializeApp() {
        //empty on purpose

        // note that even if there are multiple calls to this method the static initializer code,
        // only runs once
    }

}
