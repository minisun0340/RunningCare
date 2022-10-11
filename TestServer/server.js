//설치한 라이브러리 참고
const express = require('express'); 
//그 라이브러리를 이용해서 새로운 객체 생성
const app = express(); 
//서버를 어디에 열지 정하기
// listen(서버띄울 포트번호, 띄운 후 실행할 코드)
app.listen(8080, function(){
    console.log('listening on 8080');
}); 

// 누군가가 /pet으로 방문을 하면 pet 관련 안내문을 띄워주기
// app.get('경로', function(요청, 응답){
//  응답.send('펫용품 쇼핑할 수 있는 페이지입니다.');
//});
app.get('/pet', function(req, res){
    res.send('펫쇼핑 페이지입니다');
});

app.get('/beauty!', function(req, res){
    res.send('뷰티용품 페이지입니다.');
});

//어쩌구로 접속시 html파일 보내기
// .sendFile(보낼파일경로)
app.get('/', function(req, res){
    res.sendFile(__dirname + '/index.html');
});

app.get('/map', (req, res)=>{
    res.sendFile(__dirname + '/map.html');
})