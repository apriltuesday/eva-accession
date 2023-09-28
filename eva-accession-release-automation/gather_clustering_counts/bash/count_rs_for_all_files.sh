#!/bin/bash

set -e

OUTPUT_FILE=$1
FILE_WITH_ALL_INPUTS=$2

#Initialise list of tmp output
ALL_TMP_OUTPUT=""

# Uses all input as file to process
for INPUT in `cat $FILE_WITH_ALL_INPUTS`
do
    echo "Process $INPUT"
    ASSEMBLY=$(basename $(dirname ${INPUT}));
    SC_NAME=$(basename $(dirname $(dirname ${INPUT})));
    TYPE=$(echo $(basename ${INPUT}) | cut -f 4 -d '_')
    OUTPUT=tmp_${SC_NAME}_${ASSEMBLY}_${TYPE}.txt
    if [[ ${INPUT} == *.vcf.gz ]]
    then
        zcat  "${INPUT}" | grep -v '^#' | awk -v annotation="${ASSEMBLY}-${SC_NAME}-${TYPE}" '{print $3" "annotation}' > ${OUTPUT}
    elif [[ ${INPUT} == *_unmapped_ids.txt.gz ]]
    then
        SC_NAME=$(basename $(dirname ${INPUT}));
        OUTPUT=tmp_${SC_NAME}_unmapped.txt
        zcat  "${INPUT}" | grep -v '^#' | awk -v annotation="Unmapped-${SC_NAME}-unmapped" '{print $1" "annotation}' > ${OUTPUT}
    else
        zcat  "${INPUT}" | grep -v '^#' | awk -v annotation="${ASSEMBLY}-${SC_NAME}-${TYPE}" '{print $1" "annotation}' > ${OUTPUT}
    fi
    ALL_TMP_OUTPUT=$OUTPUT" "$ALL_TMP_OUTPUT
done

echo "Concatenate all TMP files"

cat $ALL_TMP_OUTPUT | sort  \
    | awk '{if (current_rsid != $1){
             for (a in annotation){printf "%s,",a};
             print "";
             delete annotation; current_rsid=$1
            }; annotation[$2]=1; }
            END{for (a in annotation){printf "%s,",a};
            print ""; }' \
    | grep -v '^$' | sort | uniq -c | sort -nr > "$OUTPUT_FILE"

rm  $ALL_TMP_OUTPUT

