#!/bin/bash

echo -e "\n\n### npm install ###"
npm prune -dd
npm install -dd
echo -e "\n\n### bower install ###"
bower install --allow-root
echo -e "\n\n### grunt build ###"
grunt build
