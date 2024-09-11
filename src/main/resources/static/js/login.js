function login() {
    var currentDate = new Date();
    var tomorrowDate = new Date(currentDate.getTime() + 86400000);

    localStorage.setItem('loggedIn', 'true');
    localStorage.setItem('sessionEnd', tomorrowDate);
}

document.addEventListener("DOMContentLoaded", function() {
    var currentDate = new Date();
    var sessionEndDate = new Date(localStorage.getItem('sessionEnd'));

    if (localStorage.getItem('loggedIn') === 'true' && currentDate <= sessionEndDate && localStorage.getItem('userId')) {
        window.location.href = '/nav/dashboard';
    } else {
    }
});