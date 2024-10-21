#!/bin/sh


NodeLinkRoute()
{
  awk '
  FNR == 1 {++nf}
  nf == 1 {
    xnode[++nbnodes] = $1
    ynode[nbnodes] = $2
    next
  }
  nf == 2 {
    print "polygon "
    print xnode[$1],ynode[$1],xnode[$2],ynode[$2]
    next
  }  
END {
    print "point symbol 50"
    for (i=1; i <= nbnodes; ++i) {
      print xnode[i],ynode[i]
    }
    for (i=1; i <= nbnodes; ++i) {
      print "text \"" i "\""
      print xnode[i],ynode[i]
    }
  }
  ' "$1" "$2"
}

NodeLinkRoute "$1" "$2"
