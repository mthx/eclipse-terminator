#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun/
export PATH=$JAVA_HOME/bin:"$PATH"

OUTPUT=`pwd`

for dir in "../salma-hayek" "../terminator";
do 
  make -C "$dir"
  MANIFEST="`pwd`/$dir/tmpManifest"
  echo "Implementation-Version: `svnversion $dir`" > $MANIFEST
  pushd "$dir/.generated/classes"
  jar cfm "$OUTPUT/lib/`basename $dir`.jar" $MANIFEST *
  popd
  if [[ -d $dir/lib/jars ]]; then
    cp $dir/lib/jars/* "$OUTPUT/lib/"
  fi
  rm $MANIFEST
done
  
cp ../terminator/.generated/terminfo/terminator src/net/hillsdon/eclipse/terminator/resources/terminfo

