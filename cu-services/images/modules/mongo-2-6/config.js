'use strict';

module.exports = function () {
    /**
     * Contains all configuration functions.
     * @type {{filesPath: string, production: object}}
     */
    var config = {};

    config.filesPath = './server/var';

    /**
     * The production configuration
     * This configuration used when no other configuration is specified.
     *
     * @returns {Object} config
     */
    config.production = function () {
        // Contains all settings for this configuration
        return {
            node_env: 'production',
            app_name: 'mms',
            app_displayname: 'Mongo Management Studio',
            protocol: 'http',
            host: '0.0.0.0',
            port: 3333,
            rights: {
                enabled: false,
                masterLoginPage: false
            },
            session: {
                key: 'mongo-management-studio.sid',
                secret: 'a7f4eb39-744e-43e3-a30b-3ffea846030f',
                maxLife: 804600,
                inactiveTime: 3600,
                stores: {
                    inMemory: {
                        type: 'inMemory'
                    },
                    tingoDb: {
                        type: 'tingoDb',
                        dbPath: require('path').join(config.filesPath, 'db'),
                        collectionName: 'sessions'
                    }
                },
                activeStore: 'inMemory'
            },
            logging: {
                appenders: {
                    file: {
                        maxLogSize: 2048000,
                        backups: 10
                    }
                },
                loggers: {
                    audit: {
                        active: true,
                        level: 'WARN',
                        appender: 'file'
                    },
                    syslog: {
                        active: true,
                        level: 'WARN',
                        appender: 'file'
                    },
                    express: {
                        active: true,
                        level: 'WARN',
                        appender: 'file'
                    },
                    socket: {
                        active: true,
                        level: 'WARN',
                        appender: 'file'
                    }
                }
            }
        };
    };

    return config;
};
