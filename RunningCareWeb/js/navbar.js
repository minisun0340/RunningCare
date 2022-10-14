const toggleBtn = document.querySelector('.navbar__toggleBtn');
const menu = document.querySelector('.navbar__menu');
const icons = document.querySelector('.navbar__icons');
const loginBtn = document.querySelector('.loginBtn')
const signupBtn = document.querySelector('.signupBtn');
const myaccountBtn = document.querySelector('.myaccountBtn')
const aboutusBtn = document.querySelector('.aboutusBtn')



function movepage(pagename){
    location.href = `/${pagename}`;
}

toggleBtn.addEventListener('click', () => {
    menu.classList.toggle('active');
});

loginBtn.addEventListener('click', ()=>{
    location.href = `/login`;
});

signupBtn.addEventListener('click', ()=>{
    location.href = `/signup`;
});

myaccountBtn.addEventListener('click', ()=>{
    location.href = `/myaccount`;
});

aboutusBtn.addEventListener('click', ()=>{
    location.href = `/aboutus`;
});