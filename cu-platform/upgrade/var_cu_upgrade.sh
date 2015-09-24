#!/bin/bash

FILE=/home/admincu/.profile
source $FILE

NEW_VAR=$((CU_UPGRADE+1))

grep -q "export CU_UPGRADE=" $FILE && sed --in-place "s/^export CU_UPGRADE=.*/export CU_UPGRADE=$NEW_VAR/" $FILE || echo "export CU_UPGRADE=$NEW_VAR" >> $FILE
