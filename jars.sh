#!/bin/bash

OUTPUT=`pwd`

for dir in "../salma-hayek" "../terminator";
do 
  make -C "$dir"
  pushd "$dir/.generated/classes"
  jar cf "$OUTPUT/lib/`basename $dir`.jar" *
  popd
  cp $dir/lib/jars/* "$OUTPUT/lib/"
  cp $dir/.generated/i386_Linux/lib/*.so $OUTPUT
done

