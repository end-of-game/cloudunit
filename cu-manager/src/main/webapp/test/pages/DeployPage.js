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

var DeployPage = (function () {
  function DeployPage() {
    "use strict";
    this.path = require('path');
    this.uploadFileInput = element(by.id('upload-file'));
    this.uploadBtn = element(by.css('.upload-btn'));
    this.infoName = element(by.binding('uploader.queue[0].file.name'));
    this.infoSize = element(by.binding('uploader.queue[0].file.size'));
    this.uploadMeta = element(by.css('.upload-meta'));
    this.clearBtn = element(by.css('.clear-btn'));
    this.cancelBtn = element(by.css('.cancel-btn'));
    this.fileTypeError = element(by.css('.file-type-error'));
    this.appStatusError = element(by.css('.app-status-error'));
    this.progressBar = element(by.css('.upload-progress'));
    this.deployFile = function (filePath) {
      var absolutePath = this.path.resolve(__dirname, filePath);
      return this.uploadFileInput.sendKeys(absolutePath);
    }
  }

  return DeployPage;

})();

module.exports = DeployPage;

