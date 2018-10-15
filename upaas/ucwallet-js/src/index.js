var express = require("express");
var app = express();

// http://localhost:8080/index.html
app.use(express.static("public")).listen(8080);