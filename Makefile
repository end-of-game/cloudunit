GIT_SFR=git submodule foreach --recursive
CURRENT_BRANCH:=$(shell git rev-parse --abbrev-ref HEAD)

all : fetch

checkout :
	$(GIT_SFR) git checkout $(CURRENT_BRANCH)

fetch :
	$(GIT_SFR) git fetch
	git fetch

merge : checkout
	$(GIT_SFR) git merge origin/$(CURRENT_BRANCH) --ff-only

pull : checkout
	$(GIT_SFR) git pull --rebase
	git pull --rebase

push :
	$(GIT_SFR) git push
	git push

commit-submodules : pull
	git commit -am"maj submodules"

# Pour initialiser les sous-modules
init :
	#git submodule update --init --recursive

maintenance:
	$(GIT_SFR) git repack
	$(GIT_SFR) git gc --auto
	$(GIT_SFR) git gc --aggressive
	git repack
	git gc --auto
	git gc --aggressive
