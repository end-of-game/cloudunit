#!/usr/bin/env node
'use strict';
const program = require('commander');
const chalk = require('chalk');
const columnify = require('columnify');
const fs =  require("fs");
const request = require('request');

const client = require('./lib/client');
const out = require('./lib/out');

program
  .command('list <app> <service>')
  .alias('ls')
  .description('list all environnement variables for a service')
  .action(function(app, service) {
    client.applications
        .follow('cu:applications[name:'+app+']', 'cu:services', 'cu:services[name:'+service+']')
        .getResource(function (error, doc) {
            if (error) {
                out.error('Couldn\'t list environment variables: '+error);
                process.exit(1);
            }

            client.containers
            .follow('cu:containers[name:'+doc.containerName+']', 'cu:variables')
            .getResource(function (error, doc) {
              if (error) {
                  out.error('Couldn\'t list environment variables: '+error);
                  process.exit(1);
              }
              if (!doc._embedded) {
                  doc._embedded = { 'cu:variables': [] };
              }
              doc._embedded['cu:variables'].forEach(function (v) {
                out.info('export '+v.key+'='+v.value);
              });
            });
          });
  });

program.parse(process.argv);
