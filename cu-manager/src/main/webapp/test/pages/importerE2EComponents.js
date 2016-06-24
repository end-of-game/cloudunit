var DashboardPage = require('./DashboardPage');
var EditApplicationPage = require('./EditApplicationPage');

var E2EComponents = (function() {
	function E2EComponents() {
		this.DashboardPage = new DashboardPage();
		this.EditApplicationPage = new EditApplicationPage();
	}

	return E2EComponents;
})();

module.exports = E2EComponents;