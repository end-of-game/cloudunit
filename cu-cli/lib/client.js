const traverson = require("traverson");
const JsonHalAdapter = require('traverson-hal');
traverson.registerMediaType(JsonHalAdapter.mediaType, JsonHalAdapter);

const cuHost = process.env["CU_HOST"] || "http://localhost:9000";

exports.applications =
  traverson.from(cuHost+'/applications')
    .jsonHal()
    .withRequestOptions({ headers: { 'Content-Type': 'application/hal+json'}})


exports.images =
    traverson.from(cuHost+'/images')
        .jsonHal()
        .withRequestOptions({ headers: { 'Content-Type': 'application/hal+json'}})