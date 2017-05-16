const chalk = require("chalk");

exports.error = function (message) {
  console.error(chalk.red(message));
};

exports.info = function (message) {
  console.log(message);
};
