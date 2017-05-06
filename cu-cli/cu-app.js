#!/usr/bin/env node
'use strict';
const program = require('commander');
const chalk = require('chalk');
const columnify = require('columnify');

const client = require('./lib/client')
const out = require('./lib/out');

program
  .command('create <name>')
  .description('create an application')
  .action(function(name) {
    client.applications
      .post({ 'name': name }, function (error, response) {
        if (error) {
          out.error('Couldnt create an application: '+error);
          return;
        }
        if (response.statusCode != 201) {
          out.error('Couldnt create an application: '+response.body);
          return;
        }
        out.info('Application '+name+' created');
      });
  });

  program
    .command('list')
    .alias('ls')
    .description('list all applications')
    .action(function(name) {
      client.applications
        .getResource(function (error, doc) {
          if (error) {
            out.error('Couldnt list applications: '+error);
            process.exit(1);
          }
          if (!doc._embedded) {
            doc._embedded = { 'cu:applications': [] };
          }
          out.info(columnify(
            doc._embedded['cu:applications'],
            { columns: ['name', 'state'] }));
        });
    });

  program
    .command('start <name>')
    .description('start an application')
    .action(function(name) {
      client.applications
        .follow('cu:applications[name:'+name+']','cu:start')
        .post({}, function (error, response) {
          if (error) {
            out.error('Couldnt start an application: '+error);
            process.exit(1);
          }
          if (response.statusCode != 204) {
            out.error('Couldnt start an application: '+response.body);
            process.exit(1);
          }
          out.info('Application '+name+' started');
        });
    });

  program
    .command('stop <name>')
    .description('stop an application')
    .action(function(name) {
      client.applications
        .follow('cu:applications[name:'+name+']','cu:stop')
        .post({}, function (error, response) {
          if (error) {
            out.error('Couldnt stop an application: '+error);
            process.exit(1);
          }
          if (response.statusCode != 204) {
            out.error('Couldnt stop an application: '+response.body);
            process.exit(1);
          }
          out.info('Application '+name+' stopped');
        });
    });

  program
    .command('rm <name>')
    .description('remove an application')
    .action(function(name) {
      client.applications
        .follow('cu:applications[name:'+name+']','self')
        .delete(function (error, response) {
          if (error) {
            out.error('Couldnt remove a application: '+error);
            process.exit(1);
          }
          if (response.statusCode != 204) {
            out.error('Couldnt remove a application: '+response.body);
            process.exit(1);
          }
          out.info('Application '+name+' removed');
        });
    });

program.parse(process.argv);
