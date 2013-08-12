## Overview

TPC-W is a transactional web e-Commerce benchmark specified by the [Transaction
Processing Performance Council][TPC].

[TPC]: http://www.tpc.org/

We started with an open-source Java implementation
[available online][tpcw-original], that already runs on top of Tomcat and
MySQL.  The business operations are implemented using hardcoded SQL queries to
the database.  Initially, we adapted it to run on top of the Fénix Framework.
This adaptation required transforming the hardcoded SQL queries into Java
code.  The detailed process is documented [here][adapting tpcw sql to java].

[tpcw-original]: http://tpcw.deadpixel.de/
[adapting tpcw sql to java]: http://www.esw.inesc-id.pt/permalinks/fenix-framework-tpcw 

### The domain model

Using Fénix Framework, the TPC-W domain model is described using the *Domain
Modeling Language (DML)*.  DML is a domain-specific language that enables
programmers to specify the structure of an application's domain model in a
Java-like syntax, and then automatically generate the structural Java code for
the classes.  The advantage is that it is shorter to write, and it also
represents relations between domain classes as a first-class concept.
Furthermore, the DML compiler can generate backend-specific code that
maintains the same programming API on top of the domain entities.  This allows
us to replace backends without having to change the application.

## Installation

In the following we describe how to install and run the TPC-W Benchmark.  This
benchmark is composed by two separate components: the application server and
the browser emulator.  The former is deployed to a web server and the latter
runs a given number of threads that simulate concurrent web clients
interacting with the application.

### Requirements

To run the benchmark:

  * Apache Tomcat 6.x (should work on 7+ versions but hasn't been tested)
  * JDK >= 1.5

To plot the measurement results:

  * FreeMat

### Get the source code

    git clone git://github.com/fenix-framework/examples.git

In the `examples/tpcw` directory there are two independent programs in
`server` and `client`.  The server is the web application to be deployed in
Tomcat.  The client contains the browser emulator.  The client also contains
some FreeMat files that process the benchmark's output, and the original TPC-W
implementation for reference.  From now on, operations regarding each program
should be performed in that program's top directory (e.g. to perform the
instructions regarding the server go to `examples/tpcw/server`).

## Running the benchmark

This is the *quick-and-dirty* how to get this benchmark running.  For other
alternatives please take a look at the configuration files within the source
code.

### Required configuration for Tomcat

<!-- In the server,  edit the file `build.properties` and set the following properties to reflect your own Tomcat configuration: -->

<!--  tomcat.username=test -->
<!--  tomcat.password=test -->

You should configure in your Tomcat a user that has permission to deploy a web
application, and then take note of those credentials.  Then start the Tomcat
web application server.

### Build and deploy the application server

To create the WAR packaged application do:

    mvn package

You may change the backend to another by passing the property
`-Dfenixframework.code.generator` in the previous command.  Have a look at the
`pom.xml file for some possible values.`

You may also change the following parameters when creating your application:

    mvn package -DNUM_ITEMS=1000 -DNUM_EBS=10
    
The values shown are the default. They represent:

  * `NUM_ITEMS`: the maximum number of books in the database and it must be
    one of 10^*b* with *b* in [3;7].
 
  * `NUM_EBS`: the maximum number of emulated browsers that TPC-W will need to
    support. It must be greater than 0.

If the `package` command succeeded you should be able to deploy the
application with:

    mvn tomcat6:deploy -Dtomcat.username=<username> -Dtomcat.password=<password>
    
Replace `<username>` and `<password>` with the correct credentials for your
Tomcat installation.

NOTE: Be careful in changing the default values, as they have an implication
in the number of instances of many domain objects and increasing them can
easily grow the dataset out of manageable proportions!  It is suggested that
you start with the default values.

Also, if later you wish to replace the deployed application server with
another version make sure you first remove the previously deployed code with:

    mvn tomcat6:undeploy -Dtomcat.username=<username> -Dtomcat.password=<password>    
    

### Check that the application is successfully deployed

Go to [TPC-W's home page][] and you should see a page similar to
[this](tpcw/docs/TPC-W_Home_Page.png).  Images in the loaded page do not show.
This is not an error.  It occurs simply because we didn't package the site's
images in the web application.

[TPC-W's home page]: http://localhost:8080/tpcw/TPCW_home_interaction

**You SHOULD NOT click any other links yet, because the data set is not
  populated**.  This was just to check whether the application is successfully
  deployed.

### Populate the dataset

Given that the default configuration uses Infinispan as an in-memory-only data
repository, we need to generate a data set every time we redeploy the server
application (which embeds Infinispan).  To do so we've created a servlet that
can be activated with:

    wget -q -T 0 -O - "http://localhost:8080/tpcw/TPCW_populate?NUM_EBS=10&NUM_ITEMS=1000"

In here the values for `NUM_EBS` and `NUM_ITEMS` should always be less than or
equal to the values used when building the application.  Again, the parameters
shown in the example are the default values, so they could be omitted.

If you do not have `wget` installed on your system you can alternatively
access the given URL in your browser.  Just make sure you **NEVER** reload the
page.  It may take a while to populate the data. You must wait for it to
finish before proceeding.  The population servlet is meant to run only once
and it does not support multiple executions.

Note: You may want to check in the server's logs
(`${CATALINA_HOME}/logs/catalina.out`) that the data population was
successful.

### Build the test client

Now move to the `client` application and run:

    ant dist-client-only

This creates the client application in the `dist` directory along with a shell
script to run it.

### Run the client

Just do something like:

    cd dist
    chmod 700 rbe.sh
    ./rbe.sh -r demo -u 60  -i 120 -d 20 -t 1 -b 1000 -n 3 -tt 0

This simulates 3 store clients continuously accessing the store during 120
seconds, with a previous warm-up period of 60 seconds and a ramp-down period
of 20 seconds, using workload 1 (TPC-W's browsing mix with 5% write
transactions).  To better understand the parameters and the values that can be
used run:

    ./rbe.sh -h

Just take note that the value of flag -b must match the value of `NUM_ITEMS`
used previously.

## Analyze the results

After the script completes a FreeMat file is produced with a name like
`rundemo_t1_e3_b1000_20111011_2301.m`.  This file must be processed in
FreeMat.  To do so you'll need the files in directory `freemat`.  Just copy
the generated file over tpcw.m and then open FreeMat.

    cp rundemo_t1_e3_b1000_20111011_2301.m ../freemat/tpcw.m

In FreeMat you will probably need to configure its path to point to the
`freemat` directory in the client.  Running the function `wips(tpcw)` will
produce a plot with the Web Interactions Per Second (WIPS) corresponding to
the current contents of tpcw.m.  The plot shows a timeline and the WIPS for
every second of the execution.  It also plots a curve with the average WIPS
for the 30 seconds around a given point, and a line with the overall average
WIPS.

This is an [example](tpcw/docs/Freemat_Plot.png) of the kind of FreeMat plot
you can expect to see.

## A sample script to automate stuff

The following is an example script for running a sequence of tests.  You most
probably will need to adjust it to match your needs.  When invoking the script
you can pass as the first paramenter either `ff` or `ogm` to test the
respective TPC-W implementation.  After ensuring that everything is ok by
manually running the previous steps at least once, scripting is most useful to
speed up things.

    #!/bin/bash

    # CATALINA_HOME should point to your tomcat installation and is usually
    # already set in the environment

    # SERVER and CLIENT can be set here
    TPCW=~/tpcw-benchmark/tpcw
    SERVER=${TPCW}/server
    CLIENT=${TPCW}/client

    function stop_server() {
        ${CATALINA_HOME}/bin/shutdown.sh
        sleep 5;
        killall -9 java # careful!
        sleep 5
    }

    function reboot_server() {
        ${CATALINA_HOME}/bin/shutdown.sh
        sleep 15;
        killall -9 java # careful!
        sleep 5
        ${CATALINA_HOME}/bin/startup.sh
        sleep 3;
    }

    function build_client() {
        cd ${CLIENT}
        ant dist-client-only
        chmod 700 dist/rbe.sh
    }

    # pick a default backend if none is given
    BACKEND_NAME=$1
    if [ "${BACKEND_NAME}" = "" ]; then
        BACKEND_NAME="ispn"
        BACKEND_TO_USE="pt.ist.fenixframework.backend.infinispan.InfinispanCodeGenerator"
    fi
    case $BACKEND_NAME in 
        "mem")
            BACKEND_TO_USE=pt.ist.fenixframework.backend.mem.memCodeGenerator
            ;;
        "ogm")
            BACKEND_TO_USE=pt.ist.fenixframework.backend.ogm.OgmCodeGenerator
            ;;
        "ispn")
            BACKEND_TO_USE=pt.ist.fenixframework.backend.infinispan.InfinispanCodeGenerator
            ;;
    esac
    
    echo "USING BACKEND: ${BACKEND_NAME}: ${BACKEND_TO_USE}"

    # ensure the client is build and ready
    build_client

    # start the web server
    ${CATALINA_HOME}/bin/startup.sh

    NUM_EBS=10
    # Mixes: 0=read-only; 1=browsing; 2=shopping; 3=ordering
    MIXES="0 1 2"

    # build and deploy the server app
    cd ${SERVER}
    mvn clean package tomcat6:deploy -DNUM_EBS=${NUM_EBS} -DNUM_ITEMS=1000

    # run a few tests
    cd ${CLIENT}/dist
    for mix in ${MIXES}; do
        time wget -q -T 0 -O - "http://localhost:8080/tpcw/TPCW_populate?NUM_EBS=${NUM_EBS}"
        \rm -rf ObjectStore/ PutObjectStoreDirHere/
        ./rbe.sh -r workload-${BACKEND_NAME}-mix${mix} -i 120 -t ${mix} -b 1000 -n ${NUM_EBS} -tt 0 -u 60 -d 20 -w http://localhost:8080/tpcw/
        reboot_server
    done
    # undeploy the server app
    cd ${SERVER}
    mvn tomcat6:undeploy

    stop_server

    echo "ALL DONE. Check the results in *.m files located at ${CLIENT}/dist"



