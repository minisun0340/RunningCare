const express = require('express');
const app = express();

app.listen(8000, function(){
    console.log('server ok');
});

app.use(express.static('css'));
app.use(express.static('js'));

app.get('/', function(req, res){
    res.sendFile(__dirname + '/main.html');
});