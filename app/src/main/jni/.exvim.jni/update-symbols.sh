#!/bin/bash
export DEST="./.exvim.jni"
export TOOLS="/home/hwp/.vim/tools/"
export TMP="${DEST}/_symbols"
export TARGET="${DEST}/symbols"
sh ${TOOLS}/shell/bash/update-symbols.sh
