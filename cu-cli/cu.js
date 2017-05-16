#!/usr/bin/env node
'use strict';
const program = require("commander");

program
  .command('app', 'manage applications')
  .command('service', 'manage application services')
  .parse(process.argv);
