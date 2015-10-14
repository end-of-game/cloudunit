# LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

#!/bin/bash

BIN_REPO=https://github.com/Treeptik/cloudunit/releases/download/1.0
BIN_LIST=bin-list

while read FILE; do
      echo "$FILE"
      BASENAME=$(basename "$FILE")
      DIRNAME=$(dirname "$FILE")
      mkdir -p "$DIRNAME"
      wget "$BIN_REPO/$BASENAME" -O "$FILE"
      echo ""
done < "$BIN_LIST"
