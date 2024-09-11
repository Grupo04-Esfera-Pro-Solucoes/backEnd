if (localStorage.getItem('loggedIn') === 'true' && currentDate <= sessionEndDate) {
} else {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('userId');
    localStorage.removeItem('sessionEnd');
    window.location.href = '/login';
}

let currentPage = 0; // Página atual
let pageSize = 20; // Tamanho padrão da página
let sortBy = 'idLead'; // Ordenação padrão


let idGeralLead;

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
    await fetchAllLeads(currentPage);
    await fetchAllStatusProposals();
}

function handleCloseAddLead() {
    clearLeadFields();
    let addLead = document.getElementById('cadLead');
    addLead.classList.toggle('hidden');
}

function formatarData(data) {
    const dataObj = new Date(data);
    dataObj.setUTCHours(0, 0, 0, 0);

    const dia = String(dataObj.getUTCDate()).padStart(2, '0');
    const mes = String(dataObj.getUTCMonth() + 1).padStart(2, '0');
    const ano = dataObj.getUTCFullYear();

    return `${dia}/${mes}/${ano}`;
}

function listLeads(leads) {
    let table = document.getElementById('tableLeads');
    let tbody = table.getElementsByTagName('tbody')[0];
    tbody.innerHTML = '';
    leads.forEach(data => {
        let tr = document.createElement('tr');
        tr.className = 'bg-white border-b hover:bg-gray-50';
        let dataFormatada = formatarData(data.date);
        let resultIcon = getResultIcon(data.result.result);

        tr.innerHTML = `
            <td class="px-6 py-3">
                <span class='align-middle inline-block text-primary font-bold'>${data.idLead}</span>
            </td>
            <td class="px-6 py-3">${data.idClient.name}</td>
            <td class="px-6 py-3">
                <div class="flex items-center gap-2">
                    ${resultIcon}
                    <span>${data.result.result}</span>
                </div>
            </td>
            <td class="px-6 py-3">${data.description}</td>
            <td class="px-6 py-3">${dataFormatada}</td>
            <td class="px-6 py-3">${data.callTime}</td>
            <td class="px-6 py-3">${data.duration}</td>
            <td class="px-6 py-3">${data.contact}</td>
            <td class="px-6 py-3">
                <div class="flex items-center gap-2">
                    <div class="bg-purple-contrast px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-purple-950"
                        onClick="handleAddProposal(${data.idLead})">
                        <i class="fa-regular fa-handshake text-gray-200"></i>
                    </div>
                    <div class="bg-gray-200 px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-gray-300"
                        onClick="handleCloseEditLead(${data.idLead})">
                        <ion-icon name="create" fontSize='' class='text-lg'></ion-icon>
                    </div>
                    <div class="bg-gray-200 px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-gray-300"
                        onClick="showDeleteLeadModal(${data.idLead})">
                        <ion-icon name="trash" fontSize='' class='text-lg'></ion-icon>
                    </div>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function updateLastLeadView(choice) {
    const lastLeadView = document.getElementById('lastLeadView');
    lastLeadView.textContent = choice;
}

async function fetchAllLeads(page) {
    updateLastLeadView('all');
    
    await fetch(`/lead/all/${localStorage.getItem('userId')}?page=${page}&size=${pageSize}&sort=${sortBy}`)
        .then(response => response.json())
        .then(data => {
            listLeads(data.content);
            updatePagination(data);
        })
        .catch(error => console.error('Error:', error));
}

async function fetchAllLeadsToday(page) {
    updateLastLeadView('today');
    
    const userId = localStorage.getItem('userId');
    const currentDate = new Date().toISOString().split('T')[0];
    const url = `/lead/all/today/${userId}?page=${page}&size=${pageSize}&sort=${sortBy}&date=${currentDate}`;
    
    await fetch(url)
        .then(response => response.json())
        .then(data => {
            listLeads(data.content);
            updatePagination(data);
        })
        .catch(error => console.error('Error:', error));
}

function updatePagination(pageInfo) {
    const totalPages = pageInfo.totalPages;
    const currentPage = pageInfo.number;
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = '';

    const lastLeadView = document.getElementById('lastLeadView').textContent;

    if (lastLeadView === 'all') {
        if (currentPage > 0) {
            paginationElement.innerHTML += `<button class="font-semibold mx-2 float-right" onClick="fetchAllLeads(${currentPage - 1})">Anterior</button>`;
        }
        if (currentPage < totalPages - 1) {
            paginationElement.innerHTML += `<button class="font-semibold mx-2" onClick="fetchAllLeads(${currentPage + 1})">Próximo</button>`;
        }
    } else if (lastLeadView === 'today') {
        if (currentPage > 0) {
            paginationElement.innerHTML += `<button class="font-semibold mx-2 float-right" onClick="fetchAllLeadsToday(${currentPage - 1})">Anterior</button>`;
        }
        if (currentPage < totalPages - 1) {
            paginationElement.innerHTML += `<button class="font-semibold mx-2" onClick="fetchAllLeadsToday(${currentPage + 1})">Próximo</button>`;
        }
    }
}

async function fetchAddLead() {
    event.preventDefault();
    const data = {
        contact: document.getElementById('contact').value,
        date: document.getElementById('date').value,
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
                icon: 'success',
                title: 'Ligação cadastrada com sucesso!',
                showConfirmButton: false,
                timer: 2000
            })
            handleCloseAddLead();
            fetchAllLeads(currentPage);
        })
        .catch((error) => {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao cadastrar Ligação!',
                showConfirmButton: true
            })
            console.error('Error:', error);
        });
}

async function deleteLead() {
    event.preventDefault();
    let warning = document.getElementById('warnings');
    let warningMessage = document.getElementById('warningMessage');
    let warningTitle = document.getElementById('warningTitle');
    await fetch(`/lead/delete/${sessionStorage.getItem("idLeadToDel")}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then((response) => {
            if (response.status === 409) {
                warning.classList.toggle('hidden');
                warningMessage.textContent = 'Lead não pode ser excluído, pois está associado a uma ou mais propostas.';
                warningTitle.classList.add('bg-red-200');
                setTimeout(() => {
                    warning.classList.toggle('hidden');
                    warningTitle.classList.remove('bg-red-200');
                }, 3000);
            } else if (response.status === 404) {
                Swal.fire({
                    icon: 'error',
                    title: 'Erro ao excluir ligação!',
                    text: 'Ligação não encontrado.',
                    showConfirmButton: true
                });
            } else {
                warning.classList.toggle('hidden');
                warningMessage.textContent = 'Lead excluído com sucesso.';
                warningTitle.classList.add('bg-green-200');
                setTimeout(() => {
                    warning.classList.toggle('hidden');
                    warningTitle.classList.remove('bg-green-200');
                }, 3000);
            }
            showDeleteLeadModal();
            fetchAllLeads(currentPage);
        })
        .catch(error => console.error('Erro ao buscar nome do cliente:', error));
}

async function fetchSearchLeadByClientCpfCnpj(page) {

    const name = document.getElementById('searchByNamePesquisar').value;

    await fetch(`/lead/name/${name}/${localStorage.getItem('userId')}?page=${page}&size=${pageSize}&sort=${sortBy}`,
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            listLeads(data.content);
            updatePaginationSearch(data);
        })
        .catch(error => console.error('Error:', error));
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
            clientSelect.addEventListener('change', changeContactPhone);
        })
        .catch(error => console.error('Erro ao buscar nome do cliente:', error));
}


async function changeContactPhone() {
    await fetch(`/contact/clientId/${document.getElementById('clientSelect').value}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('contact').value = data.data;
        })
        .catch(error => console.error('Erro ao buscar telefone do cliente:', error));
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

function handleCloseEditLead(idLead) {
    let editLead = document.getElementById('editLead');
    editLead.classList.toggle('hidden');

    if (!editLead.classList.contains('hidden')) {
        getElementsEditLead(idLead);
    } else {
        clearLeadEditFields(); // Limpa os campos de edição do lead
    }
}

function getCurrentTime() {
    const now = new Date();
    let hours = now.getHours();
    let minutes = now.getMinutes();

    hours = hours < 10 ? '0' + hours : hours;
    minutes = minutes < 10 ? '0' + minutes : minutes;

    return `${hours}:${minutes}`;
}

function clearLeadFields() {
    const today = new Date();
    document.getElementById('date').value = formatDate(today);
    document.getElementById('contact').value = '';
    document.getElementById('callTime').value = getCurrentTime();
    document.getElementById('duration').value = '';
    document.getElementById('description').value = '';
    document.getElementById('clientSelect').value = '';
    document.getElementById('cpfCnpjSearchByCPF').value = '';
    document.getElementById('result').value = ''; // Define o resultado padrão para "Atendeu"
}

function clearLeadEditFields() {
    document.getElementById('dateEdit').value = '';
    document.getElementById('contactEdit').value = '';
    document.getElementById('callTimeEdit').value = '';
    document.getElementById('durationEdit').value = '';
    document.getElementById('descriptionEdit').value = '';
    document.getElementById('clientIdEdit').value = '';
    document.getElementById('cpfCnpjEdit').value = '';
    document.getElementById('resultEdit').value = ''; // Define o resultado padrão para "Atendeu"
}

function formatDate(date) {
    let day = ("0" + date.getDate()).slice(-2);
    let month = ("0" + (date.getMonth() + 1)).slice(-2);
    let year = date.getFullYear();
    return `${year}-${month}-${day}`;
}

document.addEventListener('DOMContentLoaded', (event) => {
    const today = new Date();
    document.getElementById('date').value = formatDate(today);
    document.getElementById('callTime').value = getCurrentTime();
});

function getElementsEditLead(id) {
    fetch(`/lead/${id}/${localStorage.getItem('userId')}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data) {
                document.getElementById('dateEdit').value = (new Date(data.date)).toISOString().substring(0, 10);
                document.getElementById('contactEdit').value = data.contact;
                document.getElementById('callTimeEdit').value = data.callTime;
                document.getElementById('durationEdit').value = data.duration;
                document.getElementById('descriptionEdit').value = data.description;
                document.getElementById('clientIdEdit').value = data.idClient.idClient;
                document.getElementById('cpfCnpjEdit').value = data.idClient.cpfCnpj;
                document.getElementById('resultEdit').value = data.result.idLeadResult;
                idGeralLead = data.idLead;
            }
        })

        .catch(error => {
            console.error('Error fetching lead data:', error);
        });

}

function handleAddProposal(idLead) {
    let addLeadModal = document.getElementById('addProposalModal');
    addLeadModal.classList.toggle('hidden');

    if (!addLeadModal.classList.contains('hidden')) {
        prepareAddProposalModal(idLead);
    }
}

function handleCloseAddProposal() {
    let addLeadModal = document.getElementById('addProposalModal');
    addLeadModal.classList.add('hidden');
}

async function fetchAllStatusProposals() {
    var selection = document.getElementById("status");
    await fetch('/statusProposal')
        .then(response => response.json())
        .then(data => {
            data.forEach(value => {
                selection.innerHTML += `<option value="${value.idStatusProposal}">${value.name}</option>`
            })
        })
        .catch(error => console.error('Error:', error));
}

function prepareAddProposalModal(idLead) {
    const today = new Date();
    document.getElementById('idLead').value = idLead;
    fetchSearchProposalByName();
    document.getElementById('dateProposal').value = formatDate(today);
    document.getElementById('value').value = '';
    document.getElementById('service').value = '';
    document.getElementById('status').value = '';
    document.getElementById('file').value = '';
    document.getElementById('idClient').value = '';
    document.getElementById('name').value = '';
    document.getElementById('description').value = '';
}

async function fetchSearchProposalByName() {
    const id = document.getElementById('idLead').value;
    await fetch(`/lead/${id}/${localStorage.getItem('userId')}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('name').value = data.idClient.name;
            document.getElementById('idClient').value = data.idClient.idClient;
        })
        .catch(error => console.error('Error:', error));
}

async function fetchAddProposal() {
    event.preventDefault();
    const fileInput = document.getElementById('file');
    const file = fileInput.files[0]; // Obtém o primeiro arquivo selecionado

    const data = {
        idLead: document.getElementById('idLead').value,
        idStatusProposal: document.getElementById('status').value,
        service: document.getElementById('service').value,
        proposalDate: document.getElementById('dateProposal').value,
        value: document.getElementById('value').value,
        description: document.getElementById('descriptionProposal').value,
    };

    const formData = new FormData();
    formData.append('idLead', data.idLead);
    formData.append('idStatusProposal', data.idStatusProposal);
    formData.append('service', data.service);
    formData.append('completionDate', data.proposalDate);
    formData.append('value', data.value);
    formData.append('description', data.description);
    formData.append('file', file);
    formData.append('idUser', localStorage.getItem('userId'));


    await fetch(`/proposal`, {
        method: 'POST',
        body: formData
    })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: 'Proposta cadastrada com sucesso!',
                showConfirmButton: false,
                timer: 2000
            })
            handleCloseAddProposal();
        })
        .catch((error) => {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao cadastrar proposta!',
                showConfirmButton: true
            })
            console.error('Error:', error);
        });
}

async function fetchAllStatusLeads() {
    var selection = document.getElementById("result");
    var selectionEdit = document.getElementById("resultEdit");
    await fetch('/LeadResult')
        .then(response => response.json())
        .then(data => {
            data.forEach(value => {
                selection.innerHTML += `<option value="${value.idLeadResult}">${value.name}</option>`;
                selectionEdit.innerHTML += `<option value="${value.idLeadResult}">${value.name}</option>`;
            });
        })
        .catch(error => console.error('Error:', error));
}

async function fetchAddEditLead() {
    event.preventDefault();
    let id = idGeralLead;
    const data = {
        contact: document.getElementById('contactEdit').value,
        date: document.getElementById('dateEdit').value,
        callTime: document.getElementById('callTimeEdit').value,
        duration: document.getElementById('durationEdit').value,
        description: document.getElementById('descriptionEdit').value,
        result: {
            idLeadResult: document.getElementById('resultEdit').value,
        },
        idClient: {
            idClient: document.getElementById('clientIdEdit').value,
        }
    };

    await fetch('/lead/' + id, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then((response) => {
            Swal.fire({
                icon: 'success',
                title: 'Ligação editada com sucesso!',
                showConfirmButton: false,
                timer: 2000
            })
            idGeralLead = null;
            handleCloseEditLead();
            fetchAllLeads(0);
        })
        .catch((error) => {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao editar Ligação!',
                showConfirmButton: true
            })
            console.error('Error:', error);
        });
}

function showDeleteLeadModal(idLead) {
    let deleteModal = document.getElementById('deleteLeadModal');
    deleteModal.classList.toggle('hidden');

    sessionStorage.setItem('idLeadToDel', idLead);
}

async function exportLeads() {
    try {
        Swal.fire({
            title: 'Exportando...',
            text: 'Por favor, aguarde enquanto exportamos as Ligações.',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
        const response = await fetch('/lead/export/'+localStorage.getItem('userId'));
        const leads = await response.json();

        if (leads && leads.length > 0) {
            let csvContent = '';

            const header = 'ID,NOME,RESULTADO,DESCRIÇÃO,DATA,HORA,DURAÇÃO,CONTATO';
            csvContent += header + '\n';

            leads.forEach(lead => {
                const id = lead.idLead || '';
                const nome = lead.idClient ? lead.idClient.name : '';
                const resultado = lead.result ? lead.result.result : '';
                const descricao = lead.description || '';
                const data = lead.date ? new Date(lead.date).toLocaleDateString() : '';
                const hora = lead.callTime || '';
                const duracao = lead.duration || '';
                const contato = lead.contact || '';

                const leadData = `${id},${nome},${resultado},${descricao},${data},${hora},${duracao},${contato}`;
                csvContent += leadData + '\n';
            });

            const blob = new Blob([csvContent], { type: 'text/csv' });

            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = 'leads.csv';

            link.click();
            Swal.close();
        } else {
            console.log('Não há dados de leads para exportar.');
            Swal.close();
            Swal.fire({
                title: 'Sem dados',
                text: 'Não há dados de Ligações para exportar.',
                icon: 'info',
                confirmButtonText: 'OK'
            });
        }
    } catch (error) {
        console.error('Erro ao exportar leads:', error);
        Swal.close();
        Swal.fire({
            title: 'Erro!',
            text: 'Ocorreu um erro ao exportar as Ligações.',
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}

function getResultIcon(result) {
    switch (result) {
        case 'Atendido':
            return '<ion-icon name="checkmark-circle" class="text-green-500 text-2xl"></ion-icon>';
        case 'Desligado':
            return '<ion-icon name="close-circle" class="text-red-500 text-2xl"></ion-icon>';
        case 'Cx. Postal':
            return '<ion-icon name="help-circle" class="text-blue-500 text-2xl"></ion-icon>';
        case 'Ocupado':
            return '<ion-icon name="remove-circle-outline" class="text-orange-500 text-2xl"></ion-icon>';
        default:
            return '';
    }
}

