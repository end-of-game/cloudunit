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

program
    .command('add <app> <service> <volume> <mountpoint>')
    .description('mount a volume on a service')
    .action(function(app, service, volume, mountpoint) {
        client.applications
            .follow('cu:applications[name:'+app+']', 'cu:services', 'cu:services[name:'+service+']')
            .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldn\'t access mounted volumes: '+error);
                    process.exit(1);
                }
                client.volumes.follow('cu:volumes[name:'+volume+']').getResource(function(err, data){
                    if (error) {
                        out.error('Couldn\'t access mounted volumes: '+error);
                        process.exit(1);
                    }
                    client.containers
                        .follow('cu:containers[name:'+doc.containerName+']', 'cu:mounts')
                        .post({ 'volume': data, 'mountPoint': mountpoint }, function (error, response) {
                            if (error) {
                                out.error('Couldn\'t mount a volume on service: '+error);
                                return;
                            }
                            if (response.statusCode != 201) {
                                var responseJson = JSON.parse(response.body);
                                out.error('Couldn\'t mount a volume on service : '+responseJson["message"]);
                                return;
                            }
                            out.info('Volume '+volume+' mounted on service '+service);
                        });
                })
            });
    });



program.parse(process.argv);
