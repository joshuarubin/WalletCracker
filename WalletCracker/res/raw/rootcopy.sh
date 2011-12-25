#!/system/bin/sh

if [ -z $3 ]; then
  echo "usage: $0 <from> <to> <uid>" 1>&2
  exit 1
fi

from=$1
to=$2
uid=$3

if [ ! -r $from ]; then
  echo "$from is not readable" 1>&2
  exit 1
fi

touch $to > /dev/null 2>&1
ret=$?
if [ $ret -ne "0" ]; then
  echo "could not write to $to" 1>&2
  exit 1
fi

dd if=$from of=$to > /dev/null 2>&1
ret=$?
if [ $ret -ne "0" ]; then
  echo "error copying $from to $to" 1>&2
  exit 1
fi

chown $uid.$uid $to > /dev/null 2>&1
ret=$?
if [ $ret -ne "0" ]; then
  echo "error changing permisions on $to to $uid.$uid" 1>&2
  exit 1
fi

chmod 660 $to > /dev/null 2>&1
ret=$?
if [ $ret -ne "0" ]; then
  echo "error changing permissions to 660 on $to" 1>&2
  exit 1
fi

exit 0
