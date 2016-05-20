#!/bin/bash
export DEST="./.exvim.jni"
export TOOLS="/home/hwp/.vim/tools/"
export TMP="${DEST}/_inherits"
export TARGET="${DEST}/inherits"
sh ${TOOLS}/shell/bash/update-inherits.sh
