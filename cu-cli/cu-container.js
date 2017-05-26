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



program.parse(process.argv);
