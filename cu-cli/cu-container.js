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
    .description('list all containers')
    .action(function(name) {
        client.containers
            .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldnt list containers: '+error);
                    process.exit(1);
                }
                if (!doc._embedded) {
                    doc._embedded = { 'cu:containers': [] };
                }
                out.info(columnify(
                    doc._embedded['cu:containers'],
                    { columns: ['name', 'state'] }));
            });
    });

program
    .command('volume <name>')
    .description('list all services')
    .action(function(name) {
        client.containers
            .follow('cu:containers[name:'+name+']','cu:mounts')
            .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldn\'t list services: '+error);
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


program
    .command('env <name>')
    .description('list all environnements variable from a container')
    .action(function(name) {
        client.containers
            .follow('cu:containers[name:'+name+']','cu:variables')
            .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldn\'t list environnements variables: '+error);
                    process.exit(1);
                }
                if (!doc._embedded) {
                    doc._embedded = { 'cu:variables': [] };
                }
                out.info(columnify(
                    doc._embedded['cu:variables'],
                    { columns: ['key', 'value', 'role'] }));
            });
    });




program.parse(process.argv);
