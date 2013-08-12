##############################################################################
# rbe.sh to run the RBE from TPC-W Java Implementation.
# 2003 by Jan Kiefer.
#
# This file is distributed "as is". It comes with no warranty and the 
# author takes no responsibility for the consequences of its use.
#
# Usage, distribution and modification is allowed to everyone, as long 
# as reference to the author is given and this license note is included.
##############################################################################

#!/bin/sh

# $1 = run nr
# $2 = nr of ebs (rbe)

help()
{
  cat <<HELP
rbe.sh - start rbe for tpc-w
usage: rbe [option]
where option can be:

    -r <nr>
	set the number of the run (for filename only), default: 1

    -n <nr>
	set the nummber of ebs to start, default: 10

    -nt <nr>
        set the total number of ebs, default is equals to parameter above.

    -u <seconds>
	ramp up time, default: 100

    -i <seconds>
	measurement interval time, default: 1200

    -d <seconds>
	ramp down time, default: 50

    -w <url>
	url of SUT to use, default: http://localhost:8080/tpcw/

    -t <nr>
	set the type for the rbe to use
		0: Read-Only Mix
		1: Browsing Mix
		2: Shopping mix (default)
		3: Ordering Mix
    -b <nr>
       set the number of items populated in the database, default: 1000

    -m <nr>
       set the MAXERROR (Maximum errors allowed). Default: 0 (no limit)

    -tt <nr>
       think time multiplication. Default: 0.01

    -p  print-only.  If -p is set the command that would be executed is printed instead

    -prefix <prefix>
        prefix for the output file. useful for multiple instance in the same node.

    -jmx_host <hostname>
        the hostname where the JVM is to monitoring

    -jmx_port <port>
        the port where the JVM is listen. The default values of -jmx_host and -jmx_port are the local JVM

HELP
  exit 0
}

error()
{
    # print an error and exit
    echo "$1"
    exit 1
}

tftype=2
runnr=1
numebs=10
ru=100
mi=1200
rd=50
url="http://localhost:8080/tpcw/"
n_items=1000
max_error=0
think_time=0.01
print_only=0

# The option parser, change it as needed
# In this example -f and -h take no arguments -l takes an argument
# after the l
while [ -n "$1" ]; do
case $1 in
    -h) help;shift 1;; # function help is called
    -r) runnr=$2;shift 2;;
    -n) numebs=$2;shift 2;;
    -nt) totalNumEbs=$2; shift 2;;
    -u) ru=$2;shift 2;;
    -i) mi=$2;shift 2;;
    -d) rd=$2;shift 2;;
    -w) url=$2;shift 2;;
    -t) tftype=$2;shift 2;;
    -b) n_items=$2;shift 2;;
    -m) max_error=$2; shift 2;;
    -tt) think_time=$2; shift 2;;
    -p) print_only=1; shift 1;;
    -prefix) prefix=$2; shift 2;;
    -jmx_host) jmx_host=$2; shift 2;;
    -jmx_port) jmx_port=$2; shift 2;;
    --) shift;break;; # end of options
    -*) echo "error: no such option $1. -h for help";exit 1;;
    *)  break;;
esac
done

if [ -z $totalNumEbs ]; then
totalNumEbs=$numebs
fi

datum=`date +%Y%m%d_%H%M`
filename=${prefix}"run"$runnr"_t"$tftype"_e"$numebs"_b"$n_items"_"$datum".m"

fact="rbe.EBTPCW"$tftype"Factory"
cust=$[$totalNumEbs * 2880]

CMD="java rbe.RBE -EB $fact $numebs -OUT $filename -RU $ru -MI $mi -RD $rd -WWW $url -ITEM $n_items -CUST $cust -GETIM false -TT $think_time -MAXERROR $max_error"

if [ -n "${jmx_host}" ]; then
CMD="$CMD -JMX_HOST ${jmx_host}"
fi

if [ -n "${jmx_port}" ]; then
CMD="$CMD -JMX_PORT ${jmx_port}"
fi

if [ $print_only == "1" ]; then
    CMD="echo "$CMD
fi

$CMD

