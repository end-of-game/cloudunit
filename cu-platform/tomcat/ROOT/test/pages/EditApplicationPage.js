var EditApplicationPage = (function () {
  function EditApplicationPage() {
    "use strict";
    this.pageTitle = element(by.binding('editApp.application.name'));
    this.goBackLink = element(by.css('.go-back-link'));
    this.menu = element(by.css('.tabs'));
    this.previewLink = element(by.id('preview-link'));

    this.overviewTab = element(by.id('overview-tab'));
    this.aliasTab = element(by.id('alias-tab'));
    this.addModuleTab = element(by.id('add-service-tab'));
    this.jvmConfigTab = element(by.id('configure-jvm-tab'));
    this.deployTab = element(by.id('deploy-tab'));
    this.logsTab = element(by.id('logs-tab'));
    this.monitoringTab = element(by.id('monitoring-tab'));
    this.snapshotTab = element(by.id('snapshot-tab'));

    this.overviewContent = element(by.id('overview'));
    this.aliasContent = element(by.id('alias'));
    this.addModuleContent = element(by.id('add-service'));
    this.jvmConfigContent = element(by.id('configure-jvm'));
    this.deployContent = element(by.id('deploy'));
    this.logsContent = element(by.id('logs'));
    this.monitoringContent = element(by.id('monitoring'));
    this.snapshotContent = element(by.id('snapshot'));

    this.moduleList = element.all(by.repeater('module in editApp.application.modules'));
  }

  return EditApplicationPage;

})();

module.exports = EditApplicationPage;

