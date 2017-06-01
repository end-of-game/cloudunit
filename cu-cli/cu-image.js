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
    .description('list all images')
    .action(function(name) {
        client.images
            .getResource(function (error, doc) {
                if (error) {
                    out.error('Couldn\'t list images: '+error);
                    process.exit(1);
                }
                if (!doc._embedded) {
                    doc._embedded = { 'cu:images': [] };
                }
                out.info(columnify(
                    doc._embedded['cu:images'],
                    { columns: ['name', 'displayName', 'type', 'serviceName', 'version'] }));
            });
});

program.parse(process.argv);
