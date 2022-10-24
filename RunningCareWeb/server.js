const express = require('express');
const app = express();

app.listen(8000, function(){
    console.log('server ok');
});

app.use(express.static('css'));
app.use(express.static('html'));
app.use(express.static('js'));

app.get('/', function(req, res){
    res.sendFile(__dirname + '/html/main.html');
});

app.get('/login', function(req, res){
    res.sendFile(__dirname + '/html/login.html');
});

app.get('/signup', function(req, res){
    res.sendFile(__dirname + '/html/signup.html');
});

app.get('/myaccount', function(req, res){
    res.sendFile(__dirname + '/html/myaccount.html');
});

app.get('/aboutus', function(req, res){
    res.sendFile(__dirname + '/html/bluetooth.html');
});
