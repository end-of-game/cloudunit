/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

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

