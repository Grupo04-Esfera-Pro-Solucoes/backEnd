if (localStorage.getItem('loggedIn') === 'true' && currentDate <= sessionEndDate) {
} else {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('userId');
    localStorage.removeItem('sessionEnd');
    window.location.href = '/login';
}

let idGeralProposal;

let currentPage = 0; // Página atual
let pageSize = 20; // Tamanho padrão da página
let sortBy = 'idProposal'; // Ordenação padrão

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
    await fetchAllProposals(currentPage);
    await fetchAllStatusProposals();
}

function handleCloseAddProposal() {
    clearProposalFields();
    let addProposal = document.getElementById('cadProposal');
    addProposal.classList.toggle('hidden');
}


async function fetchAllProposals(page) {
    await fetch(`/proposal/all/${localStorage.getItem('userId')}?page=${page}&size=${pageSize}&sort=${sortBy}`)
        .then(response => response.json())
        .then(data => {
            listProposals(data.content);
            updatePagination(data);
        })
        .catch(error => console.error('Error:', error));
}

async function fetchSearchProposalByClientName(page) {
    const name = document.getElementById('searchProposal').value;
    await fetch(`/proposal/search/${name}/${localStorage.getItem('userId')}?page=${page}&size=${pageSize}&sort=${sortBy}`, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            listProposals(data.content);
            updatePaginationSearch(data);
        })
        .catch(error => console.error('Error:', error));

}


function updatePagination(pageInfo) {
    const totalPages = pageInfo.totalPages;
    const currentPage = pageInfo.number;
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = '';

    if (currentPage > 0) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2 float-right" onClick="fetchAllProposals(${currentPage - 1})">Anterior</button>`;
    }
    if (currentPage < totalPages - 1) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2" onClick="fetchAllProposals(${currentPage + 1})">Próximo</button>`;
    }
}

function updatePaginationSearch(pageInfo) {
    const totalPages = pageInfo.totalPages;
    const currentPage = pageInfo.number;
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = '';

    if (currentPage > 0) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2 float-right" onClick="fetchSearchProposalByClientName(${currentPage - 1})">Anterior</button>`;
    }
    if (currentPage < totalPages - 1) {
        paginationElement.innerHTML += `<button class="font-semibold mx-2" onClick="fetchSearchProposalByClientName(${currentPage + 1})">Próximo</button>`;
    }

}
function listProposals(proposals) {
    let table = document.getElementById('tableProposals');
    let tbody = table.getElementsByTagName('tbody')[0];
    tbody.innerHTML = '';
    proposals.forEach(data => {
        let tr = document.createElement('tr');
        tr.className = 'bg-white border-b hover:bg-gray-50';
        let dataFormatada = new Date(data.proposalDate).toLocaleDateString();

        const idProposal = data.idProposal ? data.idProposal : '';
        const clientName = data.idLead ? data.idLead.idClient.name : '';
        const clientCpfCnpj = data.idLead ? data.idLead.idClient.cpfCnpj : '';
        const service = data.service ? data.service : '';
        const description = data.description ? data.description : '';
        const value = data.value ? data.value : '';
        const status = data.idStatusProposal ? data.idStatusProposal.name : '';
        const hasFile = data.file ? true : false;
        const statusID = data.idStatusProposal ? data.idStatusProposal.idStatusProposal : '';

                    let iconHTML = '';
                    if (statusID == 1) {
                        iconHTML = '<ion-icon name="checkmark-circle" class="text-green-500 text-2xl mr-1" size="small" ></ion-icon>';
                    } else if (statusID == 2) {
                        iconHTML = '<ion-icon name="close-circle" class="text-red-500 text-2xl mr-1" size="small"></ion-icon>';
                    } else if (statusID == 3) {
                        iconHTML = '<ion-icon name="time" class="text-blue-500 text-2xl mr-1" size="small"></ion-icon>';
                    } else if (statusID == 4) {
                        iconHTML = '<ion-icon name="briefcase" class="text-orange-400 text-2xl mr-1" size="small"></ion-icon>';
                    }


        tr.innerHTML = `

            <td class="pl-6 py-3">
                <span class='align-middle inline-block text-primary font-bold'>${idProposal}</span>
            </td>
            <td class="px-6 py-3">${clientName}</td>
            <td class="px-6 py-3">${clientCpfCnpj}</td>
            <td class="px-6 py-3">${service}</td>
            <td class="px-6 py-3"><div class='flex items-center h-full w-full'> ${iconHTML} ${status}</div></td>
            <td class="px-6 py-3"><div class="truncate ...">${description}</div></td>
            <td class="px-6 py-3">${dataFormatada}</td>
            <td class="px-6 py-3">${value}</td>
            <td class="px-6 py-3">
                ${hasFile ? `<div class="bg-gray-200 px-2 py-2 rounded-lg text-black font-bold flex items-center justify-center w-full cursor-pointer hover:bg-gray-300" onclick="downloadFile('${idProposal}', '${idProposal}')">
                    
                        <ion-icon name="document" fontSize='' class='text-lg mx-2'></ion-icon>
                    </div>` : `<p class="">Sem Anexo</p>`}
            </td>
            <td class="px-6 py-3">
                <div class="flex items-center gap-2">
                    <div class="bg-gray-200 px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-gray-300"
                        onClick="handleCloseEditProposal(${idProposal})"
                    >
                        <ion-icon name="create" fontSize='' class='text-lg'></ion-icon>
                    </div>
                    <div class="bg-gray-200 px-2 py-2 rounded-full text-black font-bold flex justify-center items-center w-full cursor-pointer hover:bg-gray-300"
                        onClick="showDeleteProposalModal(${idProposal})"
                    >
                        <ion-icon name="trash" fontSize='' class='text-lg'></ion-icon>
                    </div>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}



async function fetchAddProposal() {
    event.preventDefault();
    const fileInput = document.getElementById('file');
    const file = fileInput.files[0]; // Obtém o primeiro arquivo selecionado

    const data = {
        idLead: document.getElementById('idLead').value,
        idStatusProposal: document.getElementById('status').value,
        service: document.getElementById('service').value,
        proposalDate: document.getElementById('date').value,
        value: document.getElementById('value').value,
        description: document.getElementById('description').value,
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
            });
            handleCloseAddProposal();
            fetchAllProposals(currentPage);
        })
        .catch((error) => {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao cadastrar proposta!',
                showConfirmButton: false,
                timer: 2000
            });
            console.error('Error:', error);
        });
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


async function deleteProposal(id) {
event.preventDefault();
    await fetch(`/proposal/${sessionStorage.getItem("idProposalToDel")}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: 'Proposta excluída com sucesso!',
                showConfirmButton: false,
                timer: 2000
            });
            fetchAllProposals(currentPage);
            hideDeleteProposalModal()
        })
        .catch((error) => {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao excluir proposta!',
                showConfirmButton: false,
                timer: 2000
            });
            console.error('Error:', error);
        });
}


function handleCloseEditProposal(idProposal) {
    let editProposal = document.getElementById('editProposal');
    editProposal.classList.toggle('hidden');

    if (!editProposal.classList.contains('hidden')) {
        getElementsEditProposal(idProposal);
    } else {
        clearProposalEditFields();
    }
}

function clearProposalFields() {
    const today = new Date();
    document.getElementById('date').value = formatDate(today);
    document.getElementById('value').value = '';
    document.getElementById('service').value = '';
    document.getElementById('status').value = '';
    document.getElementById('file').value = '';
    document.getElementById('idLead').value = '';
    document.getElementById('idClient').value = '';
    document.getElementById('name').value = '';
    document.getElementById('description').value = '';

}


function clearProposalEditFields() {
    document.getElementById('dateEdit').value = '';
    document.getElementById('valueEdit').value = '';
    document.getElementById('serviceEdit').value = '';
    document.getElementById('statusEdit').value = '';
    document.getElementById('fileEdit').value = '';
    document.getElementById('idLeadEdit').value = '';
    document.getElementById('idClientEdit').value = '';
    document.getElementById('nameEdit').value = '';
    document.getElementById('descriptionEdit').value = '';

}

function getElementsEditProposal(id) {
    fetch(`/proposal/${id}/${localStorage.getItem('userId')}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.idLead && data.idStatusProposal) {
                idGeralProposal = data.idProposal;
                document.getElementById('dateEdit').value = (new Date(data.proposalDate)).toISOString().substring(0, 10);
                document.getElementById('valueEdit').value = data.value;
                document.getElementById('serviceEdit').value = data.service;
                document.getElementById('statusEdit').value = data.idStatusProposal.idStatusProposal;
                document.getElementById('idLeadEdit').value = data.idLead.idLead;
                document.getElementById('idClientEdit').value = data.idLead.idClient.idClient;
                document.getElementById('nameEdit').value = data.idLead.idClient.name;
                document.getElementById('descriptionEdit').value = data.description;
            }
        })
        .catch(error => {
            console.error('Error fetching proposal data:', error);
        });
}


async function fetchAllStatusProposals() {
    var selection = document.getElementById("status");
    var selectionEdit = document.getElementById("statusEdit");
    await fetch('/statusProposal')
        .then(response => response.json())
        .then(data => {
            data.forEach(value => {
                selection.innerHTML += `<option value="${value.idStatusProposal}">${value.name}</option>`
                selectionEdit.innerHTML += `<option value="${value.idStatusProposal}">${value.name}</option>`

            })
        })
        .catch(error => console.error('Error:', error));
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
});

async function fetchAddEditProposal(event) {
    event.preventDefault();
    const fileInput = document.getElementById('fileEdit');
    const file = fileInput.files[0]; // Obtém o primeiro arquivo selecionado

    const data = {
        idProposal: idGeralProposal,
        idLead: document.getElementById('idLeadEdit').value,
        idStatusProposal: document.getElementById('statusEdit').value,
        service: document.getElementById('serviceEdit').value,
        proposalDate: document.getElementById('dateEdit').value,
        value: document.getElementById('valueEdit').value,
        description: document.getElementById('descriptionEdit').value,
    };

    const formData = new FormData(); // Cria um objeto FormData
    formData.append('idProposal', data.idProposal);
    formData.append('idLead', data.idLead);
    formData.append('idStatusProposal', data.idStatusProposal);
    formData.append('service', data.service);
    formData.append('completionDate', data.proposalDate);
    formData.append('value', data.value);
    formData.append('description', data.description);
    formData.append('idUser', localStorage.getItem('userId'));

    if (file) {
        formData.append('file', file);
    }

    await fetch(`/proposal/${data.idProposal}`, {
        method: 'PUT',
        body: formData
    })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: 'Proposta atualizada com sucesso!',
                showConfirmButton: false,
                timer: 2000
            });
            clearProposalEditFields();
            handleCloseEditProposal();
            fetchAllProposals(currentPage);
        })
        .catch((error) => {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao atualizar proposta!',
                showConfirmButton: false,
                timer: 2000
            });
            console.error('Error:', error);
        });
}

        function showDeleteProposalModal(id) {
            sessionStorage.setItem("idProposalToDel", id)
            document.getElementById('deleteProposalModal').classList.remove('hidden');
        }

        function hideDeleteProposalModal() {
            sessionStorage.setItem("idProposalToDel", null)
            document.getElementById('deleteProposalModal').classList.add('hidden');
        }



function exportProposals() {
    Swal.fire({
        title: 'Exportando...',
        text: 'Por favor, aguarde enquanto exportamos as propostas.',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });
    fetch('/proposal/export/'+localStorage.getItem('userId'))
        .then(response => response.json())
        .then(data => {
            const csvRows = [];
            const header = ['ID', 'CLIENTE', 'CPF / CNPJ', 'SERVIÇO', 'STATUS', 'DESCRIÇÃO', 'DATA', 'VALOR'];
            csvRows.push(header.join(','));

            data.forEach(proposal => {
                const row = [
                    proposal.idProposal,
                    proposal.idLead.idClient.name,
                    proposal.idLead.idClient.cpfCnpj,
                    proposal.service,
                    proposal.idStatusProposal.name,
                    proposal.description,
                    new Date(proposal.proposalDate).toLocaleDateString(),
                    proposal.value
                ];
                csvRows.push(row.join(','));
            });

            const csvData = csvRows.join('\n');
            const blob = new Blob([csvData], { type: 'text/csv' });
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = 'proposals.csv';
            link.click();
            Swal.close();
        })
        .catch(error => {
            console.error('Error:', error)
            Swal.close();
            Swal.fire({
                title: 'Erro!',
                text: 'Ocorreu um erro ao exportar as propostas.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        });
}
function downloadFile(fileId, proposalId) {
    fetch(`/proposal/download/${fileId}/${localStorage.getItem('userId')}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao baixar o arquivo');
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `proposal_${proposalId}.pdf`;
            document.body.appendChild(a); // Necessário para Firefox
            a.click();
            document.body.removeChild(a); // Limpa o elemento
            setTimeout(() => window.URL.revokeObjectURL(url), 100); // Aguarda um pouco antes de revogar a URL
        })
        .catch(error => {
            console.error('Erro ao baixar o arquivo:', error);
            Swal.fire({
                icon: 'error',
                title: 'Erro ao baixar o arquivo!',
                text: 'Ocorreu um erro ao tentar baixar o arquivo. Por favor, tente novamente.',
                showConfirmButton: false,
                timer: 2000
            });
        });
}
