#!/bin/bash

cd lib/jmagick
JAVA_HOME=`java_home` ./configure || exit $?
make || exit $?
cd lib
rm libJMagick.so
mv lib*.so libJMagick.dylib
