if (localStorage.getItem('loggedIn') === 'true' && currentDate <= sessionEndDate) {
} else {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('userId');
    localStorage.removeItem('sessionEnd');
    window.location.href = '/login';
}

let currentPage = 0; // Página atual
let pageSize = 20; // Tamanho padrão da página
let sortBy = 'idClient'; // Ordenação padrão

let idGeralClient;

window.onload = async function () {
    var userId = localStorage.getItem('userId');
    if (userId) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/user/' + userId, true);
        xhr.onreadystatechange = function () {
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
    await fetchAllClients(currentPage);
}


function fooSelected() {
    let checkboxes = document.getElementsByName('foo');
    for (let checkbox of checkboxes) {
        if (checkbox.checked) {
            return true;
        }
    }
    return false;

}

function selectAllCheckboxes(source) {
    let checkboxes = document.getElementsByName('foo');
    for (let checkbox of checkboxes) {
        checkbox.checked = source.checked;
    }
}

function handleCloseAddCliente() {
    let addCliente = document.getElementById('cadClient');
    addCliente.classList.toggle('hidden');
}


function listClients(clients) {
    let table = document.getElementById('tableClients');
    let tbody = table.getElementsByTagName('tbody')[0];
    tbody.innerHTML = '';
    clients.forEach(data => {
        let tr = document.createElement('tr');
        tr.className = 'bg-white border-b hover:bg-gray-50'
        tbody.appendChild(tr);
        tr.innerHTML = `
      <td class="px-4 py-4"><input id="${data.client.idClient}" type="checkbox"
      class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 dark:focus:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
      name="foo" value="${data.client.idClient}" onClick="" />
      <label for="checkbox-table-search-1" class="sr-only">checkbox</label>
      <td class="px-6">
        <span class='align-middle inline-block text-primary font-bold'> ${data.client.name} </span>
      </td>
      
      <td class="px-6">${data.address.city ? data.address.city : ""}</td>
      <td class="px-6">${data.contact[2].data}</td>
      <td class="px-6">${data.client.cpfCnpj}</td>
      <td class="px-6">${data.contact[3].data}</td>
      <td class="px-6">
      ${data.contact[2].data ? `<a class="bg-gray-200 px-2 py-2 rounded-lg text-black font-bold flex items-center justify-center w-full cursor-pointer hover:bg-gray-300"
        href="https://wa.me/${data.contact[2].data}"
        target="_blank"
        >
          <ion-icon name="logo-whatsapp" fontSize='' class='text-lg mx-2'></ion-icon>
          <span class="text-sm">Enviar Mensagem</span>
        </a>` : `<p class="text-center">Whatsapp não cadastrado</p>`}
        
      </td>
      <td>
        <div class="flex items-center gap-2">
          <div class="bg-purple-contrast px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-purple-950"
            onClick="handleAddLead(${data.client.cpfCnpj}, ${data.contact[2].data})"
          >
            <i class="fa-solid fa-phone-volume text-gray-200"></i>
          </div>
          <div class="bg-gray-200 px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-gray-300"
          onClick="handleCloseEditClient(${data.client.idClient})"
          >
            <ion-icon name="create" fontSize='' class='text-lg'></ion-icon>
          </div>
          <div class="bg-gray-200 px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-gray-300"
          onClick="showUniqueDeleteClientModal(${data.client.idClient})"
          >
            <ion-icon name="trash" fontSize='' class='text-lg'></ion-icon>
          </div>
        </div>
      </td>
    `;
    });
}

async function fetchAllClients(page) {
    await fetch(`/client-address-contact/all/${localStorage.getItem('userId')}?page=${page}&size=${pageSize}&sortBy=${sortBy}`)
        .then(response => response.json())
        .then(data => {
            listClients(data.content);
            updatePagination(data);
        })
        .catch(error => console.error('Error:', error));
}

function updatePagination(pageInfo) {
    const totalPages = pageInfo.totalPages;
    const currentPage = pageInfo.number;
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = '';

    if (currentPage > 0) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2 float-right" onClick="fetchAllClients(${currentPage - 1})">Anterior</button>`;
    }
    if (currentPage < totalPages - 1) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2" onClick="fetchAllClients(${currentPage + 1})">Próximo</button>`;
    }
}


async function fetchAddClient(event) {
    event.preventDefault();
    const data = {
        client: {
            name: document.getElementById('name').value,
            cpfCnpj: document.getElementById('cpf').value,
            company: document.getElementById('company').value,
            role: document.getElementById('role').value,
            date: document.getElementById('date').value,
            user: {
                idUser: Number(localStorage.getItem("userId"))
            }
        },
        contact: [
            {
                "data": document.getElementById('celular').value,
                idTypeContact: {
                    "idTypeContact": 1,
                    type: "celular"
                }
            },
            {
                "data": document.getElementById('telefone').value,
                idTypeContact: {
                    "idTypeContact": 2,
                    type: "telefone"
                }
            },
            {
                "data": document.getElementById('whatsapp').value,
                idTypeContact: {
                    "idTypeContact": 3,
                    type: "whatsapp"
                }
            },
            {
                "data": document.getElementById('email').value,
                idTypeContact: {
                    "idTypeContact": 4,
                    type: "email"
                }
            }
        ],
        address: {
            zipCode: document.getElementById('zipCode').value,
            street: document.getElementById('street').value,
            number: document.getElementById('number').value,
            state: document.getElementById('state').value,
            city: document.getElementById('city').value,
            country: document.getElementById('country').value
        }
    };

    if (!validarDocumento(data.client.cpfCnpj)) {
        Swal.fire({
            title: "CPF/CNPJ inválido!",
            text: "O CPF/CNPJ informado é inválido!",
            icon: "error"
        });
        return;
    }

    await fetch('/client-address-contact/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(() => {
            Swal.fire({
                title: "Cliente cadastrado com sucesso!",
                text: "O cliente foi cadastrado com sucesso!",
                icon: "success",
                showConfirmButton: false,
                timer: 3000
            });
            handleCloseAddCliente();
            clearClientCadFields();
            fetchAllClients(currentPage);
        })
        .catch((error) => {
            Swal.fire({
                title: "Erro ao cadastrar cliente!",
                text: "Erro ao cadastrar cliente!",
                icon: "error"
            });
            console.error('Error:', error);
        });
}

// IMPORT CLIENTS

function handleCloseImportClientData() {
    let importClientData = document.getElementById('importClientData');
    importClientData.classList.toggle('hidden');
}


async function fetchImportClientData() {
    event.preventDefault();


    const formData = new FormData();
    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files[0]) {
        Swal.fire({
            title: "Arquivo não selecionado!",
            text: "Selecione um arquivo para importar!",
            icon: "error"
        });
        return;
    }
    let loading = document.getElementById('loadingSpinner');
    let fileDiv = document.getElementById('fileDiv');
    loading.classList.toggle('hidden');
    fileDiv.classList.toggle('hidden');

    formData.append('file', fileInput.files[0]);
    await fetch('/client-address-contact/import/'+Number(localStorage.getItem("userId")), {
        method: 'POST',
        body: formData
    })
        .then(response =>
            Swal.fire({
                title: "Dados importados com sucesso!",
                text: "Os dados foram importados com sucesso!",
                icon: "success",
                background: '#fff',
                showConfirmButton: false,
                timer: 3000
            })
        )
        .catch(error => {
            Swal.fire({
                title: "Erro ao importar dados!",
                text: "Erro ao importar dados!",
                icon: "error",
                background: '#fff',
                showConfirmButton: false,
                timer: 3000
            });
            console.error('Error:', error)
        })
        .finally(() => {
                loading.classList.toggle('hidden');
                fileDiv.classList.toggle('hidden');
                document.getElementById('fileInput').value = '';
                handleCloseImportClientData();
                fetchAllClients(currentPage);
            }
        );
}

async function deleteClient() {
    event.preventDefault();
    let warning = document.getElementById('warnings');
    let warningMessage = document.getElementById('warningMessage');
    let warningTitle = document.getElementById('warningTitle');
    await fetch(`/client-address-contact/delete/${sessionStorage.getItem('idClientToDel')}/${localStorage.getItem('userId')}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then((response) => {
            if (response.status === 409) {
                warning.classList.toggle('hidden');
                warningTitle.classList.add('bg-red-200');
                warningMessage.innerHTML = 'Não é possível excluir o cliente pois ele possui leads associados.';
                warningTitle.innerHTML = 'Erro ao excluir cliente';
                setTimeout(() => {
                    warningTitle.classList.remove('bg-red-200');
                    warning.classList.toggle('hidden');
                }, 3000);
            } else if (response.status === 200) {
                warning.classList.toggle('hidden');
                warningTitle.classList.add('bg-green-200');
                warningMessage.innerHTML = 'Cliente excluído com sucesso.';
                warningTitle.innerHTML = 'Cliente excluído';
                setTimeout(() => {
                    warningTitle.classList.remove('bg-green-200');
                    warning.classList.toggle('hidden');
                }, 3000);
            }
            showUniqueDeleteClientModal();
            fetchAllClients(currentPage);
        })
        .catch((error) => {
            Swal.fire({
                title: "Erro ao excluir cliente!",
                text: "Erro ao excluir cliente!",
                icon: "error",
                showOkButton: true,
            });
            console.error('Error:', error);
        });
    sessionStorage.removeItem('idClientToDel');
}

async function deleteSelectedClients() {
    event.preventDefault();

    let warning = document.getElementById('warnings');
    let warningMessage = document.getElementById('warningMessage');
    let warningTitle = document.getElementById('warningTitle');

    let checkboxes = document.getElementsByName('foo');
    let clients = [];
    for (let checkbox of checkboxes) {
        if (checkbox.checked) {
            clients.push(checkbox.value);
        }
    }

    try {
        const response = await fetch(`/client-address-contact/delete/${localStorage.getItem('userId')}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(clients)
        });

        if (response.status === 200) {
            const message = await response.text();  // assuming the server sends back plain text
            warning.classList.toggle('hidden');
            warningTitle.classList.add('bg-green-200');
            warningMessage.innerHTML = message;  // Using server's response message
            warningTitle.innerHTML = 'Clientes excluídos';
            setTimeout(() => {
                warningTitle.classList.remove('bg-green-200');
                warning.classList.toggle('hidden');
            }, 3000);
        } else if (response.status === 409) {
            const errorMessage = await response.text();
            warning.classList.toggle('hidden');
            warningTitle.classList.add('bg-red-200');
            warningMessage.innerHTML = errorMessage;
            warningTitle.innerHTML = 'Erro ao excluir clientes';
            setTimeout(() => {
                warningTitle.classList.remove('bg-red-200');
                warning.classList.toggle('hidden');
            }, 3000);
        }

        showDeleteClientModal();
        fetchAllClients(currentPage);
    } catch (error) {
        Swal.fire({
            title: "Erro ao excluir clientes!",
            text: "Erro ao excluir clientes!",
            icon: "error",
            showOkButton: true,
        });
        console.error('Error:', error);
    }
}

function handleCloseEditClient(idClient) {
    let editClient = document.getElementById('editClient');
    editClient.classList.toggle('hidden');

    if (!editClient.classList.contains('hidden')) {
        getElementsEditClient(idClient);
    } else {
        clearClientEditFields();
    }
}

function clearClientEditFields() {
    document.getElementById('nameEdit').value = '';
    document.getElementById('cpfEdit').value = '';
    document.getElementById('companyEdit').value = '';
    document.getElementById('roleEdit').value = '';
    document.getElementById('celularEdit').value = '';
    document.getElementById('telefoneEdit').value = '';
    document.getElementById('whatsappEdit').value = '';
    document.getElementById('emailEdit').value = '';
    document.getElementById('zipCodeEdit').value = '';
    document.getElementById('streetEdit').value = '';
    document.getElementById('numberEdit').value = '';
    document.getElementById('stateEdit').value = '';
    document.getElementById('cityEdit').value = '';
    document.getElementById('countryEdit').value = '';
}

function clearClientCadFields() {
    document.getElementById('name').value = '';
    document.getElementById('cpf').value = '';
    document.getElementById('company').value = '';
    document.getElementById('role').value = '';
    document.getElementById('celular').value = '';
    document.getElementById('telefone').value = '';
    document.getElementById('whatsapp').value = '';
    document.getElementById('email').value = '';
    document.getElementById('zipCode').value = '';
    document.getElementById('street').value = '';
    document.getElementById('number').value = '';
    document.getElementById('state').value = '';
    document.getElementById('city').value = '';
    document.getElementById('country').value = '';

}

function getElementsEditClient(idClient) {
    fetch(`/client-address-contact/${idClient}/${localStorage.getItem('userId')}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.client && data.contact && data.address) {
                idGeralClient = data.client.idClient;
                document.getElementById('nameEdit').value = data.client.name;
                document.getElementById('cpfEdit').value = data.client.cpfCnpj;
                document.getElementById('companyEdit').value = data.client.company;
                document.getElementById('roleEdit').value = data.client.role;
                document.getElementById('dateEdit').value = (new Date(data.client.date)).toISOString().substring(0, 10);
                // Considerar verificar se cada contato existe
                document.getElementById('celularEdit').value = data.contact[0]?.data || '';
                document.getElementById('telefoneEdit').value = data.contact[1]?.data || '';
                document.getElementById('whatsappEdit').value = data.contact[2]?.data || '';
                document.getElementById('emailEdit').value = data.contact[3]?.data || '';
                document.getElementById('zipCodeEdit').value = data.address.zipCode;
                document.getElementById('streetEdit').value = data.address.street;
                document.getElementById('numberEdit').value = data.address.number;
                document.getElementById('stateEdit').value = data.address.state;
                document.getElementById('cityEdit').value = data.address.city;
                document.getElementById('countryEdit').value = data.address.country;
            }
        })
        .catch(error => {
            console.error('Error fetching client data:', error);
        });
}

async function fetchSearchClientByCpfCnpj() {
    const cpfCnpj = document.getElementById('cpfCnpjSearchByCPF').value;

    await fetch(`/client/cpf/`+cpfCnpj+"/"+localStorage.getItem('userId'), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            // Limpar as opções anteriores
            const clientSelect = document.getElementById('clientSelect');
            clientSelect.innerHTML = '';

            data.forEach(client => {
                const option = document.createElement('option');
                option.value = client.idClient;
                option.textContent = client.name;
                clientSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Erro ao buscar nome do cliente:', error));
}

function handleAddLead(cpfCnpj, phone) {
    let addLeadModal = document.getElementById('addLeadModal');
    addLeadModal.classList.toggle('hidden');

    if (!addLeadModal.classList.contains('hidden')) {
        prepareAddLeadModal(cpfCnpj, phone);
    }
}

function handleCloseAddLead() {
    let addLeadModal = document.getElementById('addLeadModal');
    addLeadModal.classList.add('hidden');
}

function formatDate(date) {
    let day = ("0" + date.getDate()).slice(-2);
    let month = ("0" + (date.getMonth() + 1)).slice(-2);
    let year = date.getFullYear();
    return `${year}-${month}-${day}`;
}

document.addEventListener('DOMContentLoaded', (event) => {
    const today = new Date();
    document.getElementById('dateLead').value = formatDate(today);
});

function getCurrentTime() {
    const now = new Date();
    let hours = now.getHours();
    let minutes = now.getMinutes();

    hours = hours < 10 ? '0' + hours : hours;
    minutes = minutes < 10 ? '0' + minutes : minutes;

    return `${hours}:${minutes}`;
}

function prepareAddLeadModal(cpfCnpj, phone) {
    const today = new Date();
    document.getElementById('cpfCnpjSearchByCPF').value = cpfCnpj;
    fetchSearchClientByCpfCnpj()
    document.getElementById('dateLead').value = formatDate(today);
    document.getElementById('contact').value = phone;
    document.getElementById('callTime').value = getCurrentTime();
    document.getElementById('duration').value = '';
    document.getElementById('description').value = '';
    document.getElementById('clientSelect').value = '';
    document.getElementById('result').value = '';
}

async function fetchAddLead() {
    event.preventDefault();
    const data = {
        contact: document.getElementById('contact').value,
        date: document.getElementById('dateLead').value,
        callTime: document.getElementById('callTime').value,
        duration: document.getElementById('duration').value,
        description: document.getElementById('description').value,
        result: {
            idLeadResult: document.getElementById('result').value,
        },
        idClient: {
            idClient: document.getElementById('clientSelect').value
        }
    };


    await fetch('/lead', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(() => {
            Swal.fire({
                title: "Ligação cadastrada com sucesso!",
                text: "A ligação foi cadastrada com sucesso!",
                icon: "success",
                background: '#fff',
                showConfirmButton: false,
                timer: 2000
            });
            handleCloseAddLead();
        })
        .catch((error) => {
            Swal.fire({
                title: "Erro ao cadastrar ligação!",
                text: "Erro ao cadastrar ligação!",
                icon: "error",
                background: '#fff',
                showConfirmButton: false,
                timer: 2000
            });
            console.error('Error:', error);
        });
}

function fetchEditClient(event) {
    event.preventDefault();
    const idClient = idGeralClient;
    const data = {
        client: {
            name: document.getElementById('nameEdit').value,
            cpfCnpj: document.getElementById('cpfEdit').value,
            company: document.getElementById('companyEdit').value,
            role: document.getElementById('roleEdit').value,
            date: document.getElementById('dateEdit').value,
            user: {
                idUser: Number(localStorage.getItem("userId"))
            }
        },
        contact: [
            {
                "data": document.getElementById('celularEdit').value,
                idTypeContact: {
                    "idTypeContact": 1,
                    type: "celular"
                }
            },
            {
                "data": document.getElementById('telefoneEdit').value,
                idTypeContact: {
                    "idTypeContact": 2,
                    type: "telefone"
                }
            },
            {
                "data": document.getElementById('whatsappEdit').value,
                idTypeContact: {
                    "idTypeContact": 3,
                    type: "whatsapp"
                }
            },
            {
                "data": document.getElementById('emailEdit').value,
                idTypeContact: {
                    "idTypeContact": 4,
                    type: "email"
                }
            }
        ],
        address: {
            zipCode: document.getElementById('zipCodeEdit').value,
            street: document.getElementById('streetEdit').value,
            number: document.getElementById('numberEdit').value,
            state: document.getElementById('stateEdit').value,
            city: document.getElementById('cityEdit').value,
            country: document.getElementById('countryEdit').value
        }
    };

    fetch(`/client-address-contact/update/${idClient}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(() => {
            Swal.fire({
                title: "Cliente atualizado com sucesso!",
                text: "O cliente foi atualizado com sucesso!",
                icon: "success",
                background: '#fff',
                showConfirmButton: false,
                timer: 3000
            })
            idGeralClient = null;
            handleCloseEditClient();
            fetchAllClients(currentPage);
        })
        .catch((error) => {
            Swal.fire({
                title: "Erro ao atualizar cliente!",
                text: "Erro ao atualizar cliente!",
                icon: "error",
                background: '#fff',
                showConfirmButton: false,
                timer: 3000
            });
            console.error('Error:', error);
        });
}

async function fetchSearchClientByName(page) {
    const name = document.getElementById('searchClient').value;
    await fetch(`/client-address-contact/name/${name}/${localStorage.getItem('userId')}?page=${page}&size=${pageSize}&sort=${sortBy}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            listClients(data.content);
            updatePaginationSearch(data);

        })
        .catch(error => console.error('Error:', error));
}


function updatePaginationSearch(pageInfo) {
    const totalPages = pageInfo.totalPages;
    const currentPage = pageInfo.number;
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = '';

    if (currentPage > 0) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2 float-right" onClick="fetchSearchLeadByClientCpfCnpj(${currentPage - 1})">Anterior</button>`;
    }
    if (currentPage < totalPages - 1) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2" onClick="fetchSearchLeadByClientCpfCnpj(${currentPage + 1})">Próximo</button>`;
    }
}

function showDeleteClientModal() {
    let modal = document.getElementById('deleteClientModal');
    modal.classList.toggle('hidden');
}

function showUniqueDeleteClientModal(idClient) {
    let modal = document.getElementById('deleteUniqueClientModal');
    modal.classList.toggle('hidden');

    sessionStorage.setItem('idClientToDel', idClient);
}


/*
    Validadores
 */

function validarDocumento(documento) {
    documento = documento.replace(/[^\d]+/g, ''); // Remove caracteres não numéricos

    if (documento.length === 11) {
        return validaCPF(documento);
    } else if (documento.length === 14) {
        return validaCNPJ(documento);
    } else {
        return false; // Não é CPF nem CNPJ
    }
}

function validaCPF(cpf) {
    if (cpf.length !== 11 || !!cpf.match(/(\d)\1{10}/)) {
        return false;
    }

    let soma = 0;
    let resto;

    for (let i = 1; i <= 9; i++) {
        soma += parseInt(cpf[i - 1]) * (11 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf[9])) return false;

    soma = 0;
    for (let i = 1; i <= 10; i++) {
        soma += parseInt(cpf[i - 1]) * (12 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf[10])) return false;

    return true;
}

function validaCNPJ(cnpj) {
    if (cnpj.length !== 14 || !!cnpj.match(/(\d)\1{13}/)) {
        return false;
    }

    let tamanho = cnpj.length - 2;
    let numeros = cnpj.substring(0, tamanho);
    let digitos = cnpj.substring(tamanho);
    let soma = 0;
    let pos = tamanho - 7;

    for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) pos = 9;
    }

    let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
    if (resultado !== parseInt(digitos.charAt(0))) return false;

    tamanho = tamanho + 1;
    numeros = cnpj.substring(0, tamanho);
    soma = 0;
    pos = tamanho - 7;
    for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) pos = 9;
    }

    resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
    if (resultado !== parseInt(digitos.charAt(1))) return false;

    return true;
}

