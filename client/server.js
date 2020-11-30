var express = require('express');
var app = express();
app.use(express.static('dist/client'));
app.get('/', function (_, res, _) {
    res.redirect('/');
});
app.listen(8080)