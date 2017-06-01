#!/usr/bin/env node
'use strict';
const program = require("commander");

program
  .command('app', 'manage applications')
  .command('service', 'manage application services')
  .command('image', 'manage service images')
  .command('volume', 'manage data volumes')
  .command('env', 'manage environment variables')
  .command('mount', 'manage mounted data volumes')
  .parse(process.argv);
