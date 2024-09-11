if (localStorage.getItem('loggedIn') === 'true' && currentDate <= sessionEndDate) {
} else {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('userId');
    localStorage.removeItem('sessionEnd');
    window.location.href = '/login';
}

fetch('/user/' + localStorage.getItem('userId'))
    .then(response => {
      if (response.ok) {
        return response.json();
      }
      throw new Error('Erro ao recuperar os detalhes do usuário');
    })
    .then(usuario => {
      document.getElementById('name').value = usuario.name;
      document.getElementById('email').value = usuario.email;
      document.getElementById('role').value = usuario.role;
      document.getElementById('phone').value = usuario.phone;
    })
    .catch(error => {
      console.error(error);
    });

const checkCurrentPassword = (userId, currentPassword) => {
    fetch(`/user/${userId}/checkPassword?password=${currentPassword}`)
        .then(response => response.json())
        .then(result => {
            if (result) {
                updateUser();
            } else {
                const updateMessage = document.getElementById('updateMessage');
                updateMessage.textContent = 'Senha atual incorreta.';
                updateMessage.classList.add('text-red-500');
                updateMessage.style.display = 'block';
            }
        })
        .catch(error => {
            console.error(error);
        });
};

const updateUser = () => {
    const userId = localStorage.getItem('userId');
    const currentPassword = document.getElementById('password').value;
    const newPassword = document.getElementById('newPassword').value;
    const repeatNewPassword = document.getElementById('repeatNewPassword').value;

    if (newPassword || repeatNewPassword){
        if (newPassword !== repeatNewPassword) {
            const updateMessage = document.getElementById('updateMessage');
            updateMessage.textContent = 'A nova senha e a confirmação não coincidem.';
            updateMessage.classList.add('text-red-500');
            updateMessage.style.display = 'block';
            setTimeout(() => {
                            document.getElementById('updateMessage').style.display = 'none';
                        }, 3000);
            return;
        }
    }

    fetch(`/user/${userId}/checkPassword?currentPassword=${currentPassword}`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Erro ao verificar a senha atual');
        }
    })
    .then(passwordMatches => {
        if (passwordMatches) {
            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const role = document.getElementById('role').value;
            const phone = document.getElementById('phone').value;
            const formData = {
                name: name,
                email: email,
                role: role,
                phone: phone,
                passwordHash: newPassword
            };

            fetch(`/user/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            })
            .then(response => {
                if (response.ok) {
                    const updateMessage = document.getElementById('updateMessage');
                    updateMessage.textContent = 'Dados do usuário atualizados com sucesso.';
                    updateMessage.classList.add('text-green-500');
                    updateMessage.style.display = 'block';
                    setTimeout(() => {
                                    document.getElementById('updateMessage').style.display = 'none';
                                }, 3000);
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
                        document.getElementById('password').value = '';
                        document.getElementById('newPassword').value = '';
                        document.getElementById('repeatNewPassword').value = '';
                } else {
                    const updateMessage = document.getElementById('updateMessage');
                    updateMessage.textContent = 'Erro ao atualizar os detalhes do usuário.';
                    updateMessage.classList.add('text-red-500');
                    updateMessage.style.display = 'block';
                    setTimeout(() => {
                                    document.getElementById('updateMessage').style.display = 'none';
                                }, 3000);
                }
            })
            .catch(error => {
                console.error(error);
            });
        } else {
            const updateMessage = document.getElementById('updateMessage');
            updateMessage.textContent = 'Senha atual incorreta.';
            updateMessage.classList.add('text-red-500');
            updateMessage.style.display = 'block';
            setTimeout(() => {
                            document.getElementById('updateMessage').style.display = 'none';
                        }, 3000);
        }
    })
    .catch(error => {
        console.error(error);
    });
};

document.addEventListener("DOMContentLoaded", function() {
   const showHelp = sessionStorage.getItem('showHelp');
   if (showHelp === 'true') {
       toggleAjuda();
       sessionStorage.removeItem('showHelp');
   }
});

const togglePerfil = () => {
  let perfil = document.querySelector('.perfil');
  let ajuda = document.querySelector('.ajuda');
  let liPerfil = document.querySelector('.li-perfil');
  let liAjuda = document.querySelector('.li-ajuda');

  ajuda.classList.remove('block');
  ajuda.classList.add('hidden');
  liAjuda.classList.remove('text-purple-contrast')
  liAjuda.classList.remove('font-bold')
  liAjuda.classList.remove('border-b-2')
  liAjuda.classList.remove('border-violet-contrast')
  perfil.classList.remove('hidden');
  perfil.classList.add('block');
  liPerfil.classList.add('text-purple-contrast');
  liPerfil.classList.add('font-bold');
  liPerfil.classList.add('border-b-2');
  liPerfil.classList.add('border-violet-contrast');
}

const toggleAjuda = () => {
  let perfil = document.querySelector('.perfil');
  let ajuda = document.querySelector('.ajuda');
  let liPerfil = document.querySelector('.li-perfil');
  let liAjuda = document.querySelector('.li-ajuda');

  ajuda.classList.remove('hidden');
  perfil.classList.remove('block');
  perfil.classList.add('hidden');

  liPerfil.classList.remove('text-purple-contrast');
  liPerfil.classList.remove('font-bold');
  liPerfil.classList.remove('border-b-2');
  liPerfil.classList.remove('border-violet-contrast');

  liAjuda.classList.add('text-purple-contrast');
  liAjuda.classList.add('font-bold');
  liAjuda.classList.add('border-b-2');
  liAjuda.classList.add('border-violet-contrast');

}

toggleAcordion = (id) => {
  let acordion = document.getElementById(id);
  acordion.classList.toggle('hidden');
}