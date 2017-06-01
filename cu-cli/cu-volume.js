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
    .command('list')
    .alias('ls')
    .description('list all volumes')
    .action(function(name) {
        client.volumes
            .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldn\'t list volumes: '+error);
                    process.exit(1);
                }
                if (!doc._embedded) {
                    doc._embedded = { 'cu:volumes': [] };
                }
                out.info(columnify(
                    doc._embedded['cu:volumes'],
                    { columns: ['name'] }));
            });
    });

program
    .command('create <name>')
    .description('create a volume')
    .action(function(name) {
        client.volumes
            .post({ 'name': name }, function (error, response) {
                if (error) {
                    out.error('Couldn\'t create a volume: '+error);
                    return;
                }
                if (response.statusCode != 201) {
                    out.error('Couldn\'t create a volume: '+response.body);
                    return;
                }
                out.info('Volume '+name+' created');
            });
    });


program
    .command('rm <name>')
    .description('remove a volume')
    .action(function(name) {
        client.volumes
            .follow('cu:volumes[name:'+name+']','self')
            .delete(function (error, response) {
                if (error) {
                    out.error('Couldn\'t remove a volume: '+error);
                    process.exit(1);
                }
                if (response.statusCode != 204) {
                    out.error('Couldn\'t remove a volume: '+response.body);
                    process.exit(1);
                }
                out.info('Volume '+name+' removed');
            });
    });

program.parse(process.argv);
