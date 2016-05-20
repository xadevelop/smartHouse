#!/bin/bash
export DEST="./.exvim.jni"
export TOOLS="/home/hwp/.vim/tools/"
export IS_EXCLUDE=-not
export FOLDERS=""
export FILE_SUFFIXS=".*"
export TMP="${DEST}/_files"
export TARGET="${DEST}/files"
export ID_TARGET="${DEST}/idutils-files"
sh ${TOOLS}/shell/bash/update-filelist.sh
