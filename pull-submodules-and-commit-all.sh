CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo
echo "Maj des sous-modules sur la branche courrante."
echo

git submodule foreach git checkout $CURRENT_BRANCH
git submodule foreach git pull --rebase

echo
echo "Maj du dépôt cloudunit."
echo

git pull --rebase

echo
echo "Commit des maj. Vous devez pousser ce commit s'il vous convient."
echo

git commit -am"maj submodules"
