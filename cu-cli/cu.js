#!/usr/bin/env node
'use strict';
const program = require("commander");

program
  .command('app', 'manage applications')
  .command('service', 'manage application services')
  .command('image', 'manage images')
  .parse(process.argv);
