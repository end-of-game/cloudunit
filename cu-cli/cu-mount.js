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
  .description('list mounted volumes')
  .action(function(app, service) {
      client.applications
          .follow('cu:applications[name:'+app+']', 'cu:services', 'cu:services[name:'+service+']')
          .getResource(function (error, doc) {
              if (error) {
                  out.error('Couldn\'t list mounted volumes: '+error);
                  process.exit(1);
              }

              client.containers
              .follow('cu:containers[name:'+doc.containerName+']', 'cu:mounts')
              .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldn\'t list mounted volumes: '+error);
                    process.exit(1);
                }
                if (!doc._embedded) {
                    doc._embedded = { 'cu:mounts': [] };
                }
                out.info(columnify(
                    doc._embedded['cu:mounts'],
                    { columns: ['mountPoint'] }));
              });
          });
  });

program.parse(process.argv);
