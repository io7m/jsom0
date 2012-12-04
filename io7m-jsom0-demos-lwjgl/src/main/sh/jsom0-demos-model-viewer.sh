#!/bin/sh

JAR_PATH=""

for JAR in `ls lib/*.jar`
do
  if [ -z "${JAR_PATH}" ]
  then
    JAR_PATH="${JAR}"
  else
    JAR_PATH="${JAR_PATH}:${JAR}"
  fi
done

exec java -Djava.library.path="lib/native/linux" -cp "${JAR_PATH}" "com.io7m.jsom0.demos.ModelViewerLWJGL30" $@
