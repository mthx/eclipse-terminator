#!/bin/bash

OUTPUT=`pwd`

for dir in "../salma-hayek" "../terminator";
do 
  make -C "$dir"
  pushd "$dir/.generated/classes"
  jar cf "$OUTPUT/lib/`basename $dir`.jar" *
  popd
  cp $dir/lib/jars/* "$OUTPUT/lib/"
  cp $dir/.generated/i386_Linux/lib/*.so $OUTPUT/src/
done
  
cp ../terminator/.generated/terminfo/terminator src/net/hillsdon/eclipse/terminator/resources/terminfo

