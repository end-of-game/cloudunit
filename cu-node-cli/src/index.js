#! /usr/bin/env node

import Vorpal from 'vorpal';
import applicationCommand from './commands/application.command';

let vorpal = Vorpal();

applicationCommand.setup(vorpal);

vorpal
    .delimiter('cloudunit$')
    .show();