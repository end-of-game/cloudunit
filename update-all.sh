CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo
echo "Maj du dépôt cloudunit et des sous modules référencés dans le parent."
echo

git pull --rebase
git submodule update
