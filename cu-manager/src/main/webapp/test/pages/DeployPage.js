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

