/* @flow */
import Command from './command';
import ApplicationController from '../controllers/application.controller'
import http from 'http'
import request from 'request'

class ApplicationCommand extends Command {

    setup(vorpal: Vorpal) {
        vorpal
            .command('create-app', 'Take control of an application')
            .action((args, callback) => {

                request("http://localhost:9000/applications.json", function (error, response, body) {
                    if (!error && response.statusCode == 200) {
                        console.log(body) // Print the body of response.
                    }
                })
             });        
    }

}

export default new ApplicationCommand();

