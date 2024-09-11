document.addEventListener("DOMContentLoaded", function() {
    var userId = localStorage.getItem('userId');

    if (!userId) {
        var urlParams = new URLSearchParams(window.location.search);
        userId = urlParams.get('userId');
        localStorage.setItem('userId', userId);
    }

    if (window.history.replaceState) {
        var newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
        window.history.replaceState({path: newUrl}, '', newUrl);
    }

    var userId = localStorage.getItem('userId');
    if (userId) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/user/' + userId, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    var userData = JSON.parse(xhr.responseText);
                    var userNameDisplay = document.getElementById('userNameDisplay');
                    var userRoleDisplay = document.getElementById('userRoleDisplay');
                    if (userNameDisplay && userRoleDisplay) {
                        userNameDisplay.textContent = userData.name;
                        userRoleDisplay.textContent = userData.role;
                    } else {
                        console.error('Elemento com ID "userNameDisplay" ou "userRoleDisplay" não encontrado.');
                    }
                } else {
                    console.error('Erro ao obter dados do usuário: ' + xhr.status);
                }
            }
        };
        xhr.send();
    }
});

var currentDate = new Date();
var sessionEndDate = new Date(localStorage.getItem('sessionEnd'));

if (localStorage.getItem('loggedIn') === 'true' && currentDate <= sessionEndDate) {
} else {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('userId');
    localStorage.removeItem('sessionEnd');
    window.location.href = '/login';
}

function logout() {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('userId');
    localStorage.removeItem('sessionEnd');
    window.location.href = '/login';
}

function setShowHelpFlagAndRedirect() {
    sessionStorage.setItem('showHelp', 'true');
    window.location.href = '/nav/configuration';
}

function toggleDropdown() {
    document.querySelector('.dropdown').classList.toggle('hidden');
    document.querySelector('#dropdownHeader').classList.toggle('bg-purple-800');
    document.querySelector('#dropdownMain').classList.toggle('text-white');
    document.querySelector('#dropdownMain').classList.toggle('hover:text-gray-200');
    document.querySelector('#userRoleDisplay').classList.toggle('text-white');
}

const toggleSidebar = () => {
    document.querySelector('.sidebar').classList.toggle('active');
}