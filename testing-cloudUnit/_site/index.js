/*
 Date: 22th June
 Author: IsmaStormZ
 */

const http = require('http');
const express = require('express');
const nodemailer = require('nodemailer');
const bodyParser = require('body-parser');
const sendmail = require('sendmail')({
    logger: {
        debug: console.log,
        info: console.info,
        warn: console.warn,
        error: console.error
    }});

const app = express();
const port = Number(process.env.PORT || 5000);

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended:true
}));

app.use('/', express.static(__dirname));

//Home page

app.get('/', (req,res) => {
    res.sendfile('index.html');
    console.log('CloudUnit reading console log ...' + req.url);
});

// Create reusable transporter object using the default SMTP transport and sending mail function

app.post('/contact', (req, res) => {
    console.log(JSON.stringify(req.body));
    if(req.body.setName == "" || req.body.setEmail == "") {     //check that the requested fields are filled in.
    res.send("Error: Name & Email should not be blank");        //Message if issue in field
    return false;
}

// setup email data
let mailOptions = {
    from: "cloudunit@treeptik.com",                         // sender address
    to: "Treeptik mail - <onifuerte@gmail.com>",            // list of receivers
    subject: 'Cloudunit new test.',
    html: "<b>" + "Name : " + req.body.setName + "<b>" + "<br>" + "Mail : " + req.body.setEmail   // name to form in index.html
};

sendmail(mailOptions, (err, reply) => {
    console.log(err);
    console.dir(reply);
});

/*
res.render('contact-success', {data: req.body});
*/
res.redirect("contact-success.html");
});

//Starting server

const server = http.createServer(app).listen(port, () => {
        console.log("Server Running on 127.0.0.1 : " + port);
});
