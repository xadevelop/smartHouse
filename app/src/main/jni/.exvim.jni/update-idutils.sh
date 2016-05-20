#!/bin/bash
export DEST="./.exvim.jni"
export TOOLS="/home/hwp/.vim/tools/"
export TMP="${DEST}/_ID"
export TARGET="${DEST}/ID"
sh ${TOOLS}/shell/bash/update-idutils.sh
