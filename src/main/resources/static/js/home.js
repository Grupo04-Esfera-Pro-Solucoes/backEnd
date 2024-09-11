document.addEventListener("DOMContentLoaded", function() {
    const userId = localStorage.getItem('userId');
    if (userId) {
        fetch('/proposal/faturamento/' + userId)
            .then(response => response.json())
            .then(data => {
                let labelAumento = document.getElementById('faturamento-crescimento');
                document.getElementById('faturamento-total').innerText = `R$${data.totalFaturamento.toFixed(2)}`;

                if (data.crescimentoPercentual >= 0) {
                    labelAumento.innerHTML = `  <p class="font-bold text-xl">&#8593;${data.crescimentoPercentual.toFixed(2)}%
                                            <p class="text-sm">em comparação ao mês anterior</p>`;
                    labelAumento.classList.add('text-emerald-700');
                } else if (data.crescimentoPercentual < 0) {
                    labelAumento.innerHTML = `  <p class="font-bold text-xl">&#8595;${data.crescimentoPercentual.toFixed(2)}%
                                            <p class="text-sm">em comparação ao mês anterior</p>`;
                    labelAumento.classList.add('text-red-600');
                }

            })
            .catch(error => console.error('Erro ao buscar os dados de faturamento:', error));
    } else {
        console.error('User ID não encontrado no local storage.');
    }
});

// grafico linhas

window.onload = function () {
    setLeadsByDayOfTheWeak();
    setLeadsByDayOfTheMonth();
    setProposalsByDayOfTheMonth();
    fetchTasks(localStorage.getItem('userId'));
}

// grafico barras


var options = {
    scales: {
        yAxes: [{
            ticks: {
                beginAtZero: true
            }
        }]
    }
};


// grafico pizza (Propostas)

async function fetchAndUpdateChartProposal() {
    const userId = localStorage.getItem('userId');
    const periodSelect = document.getElementById('periodSelect');
    const period = periodSelect.value || 'all';

    try {
        const response = await axios.get(`/proposal/statistics/${userId}`, {
            params: { period: period }
        });
        const statistics = response.data;

        const labels = statistics.map(item => item[0]);
        const data = statistics.map(item => item[1]);

        const backgroundColors = [
            '#BA55D3',
            '#A020F0',
            '#8B008B',
            '#FF69B4',
            '#DA70D6'
        ];

        const chartData = {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: backgroundColors.slice(0, data.length)
            }]
        };

        const ctx = document.getElementById('pieChart1').getContext('2d');

        if (window.pieChart instanceof Chart) {
            window.pieChart.data = chartData;
            window.pieChart.update();
        } else {
            window.pieChart = new Chart(ctx, {
                type: 'pie',
                data: chartData,
                options: {
                    responsive: true,
                    title: {
                        display: true,
                        text: 'Gráfico de Pizza'
                    }
                }
            });
        }

        const totalProposals = data.reduce((a, b) => a + b, 0);
        document.getElementById('totalProposals').innerText = `${totalProposals} Propostas`;

    } catch (error) {
        console.error('Erro ao buscar dados: ', error);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAndUpdateChartProposal();
    document.getElementById('periodSelect').addEventListener('change', fetchAndUpdateChartProposal);
});

// grafico pizza (Ligações)

async function fetchAndUpdateChartCalls() {
    const userId = localStorage.getItem('userId');
    const periodSelect = document.getElementById('periodSelectCalls');
    const period = periodSelect.value || 'all';

    try {
        const response = await axios.get(`/lead/statistics/${userId}`, {
            params: { period: period }
        });
        const statistics = response.data;

        const labels = statistics.map(item => item[0]);
        const data = statistics.map(item => item[1]);

        const backgroundColors = [
            '#BA55D3',
            '#A020F0',
            '#8B008B',
            '#FF69B4',
            '#DA70D6'
        ];

        const chartDataCalls = {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: backgroundColors.slice(0, data.length)
            }]
        };

        const ctxCalls = document.getElementById('pieChart2').getContext('2d');

        if (window.pieChartCalls instanceof Chart) {
            window.pieChartCalls.data = chartDataCalls;
            window.pieChartCalls.update();
        } else {
            window.pieChartCalls = new Chart(ctxCalls, {
                type: 'doughnut',
                data: chartDataCalls,
                options: {
                    cutoutPercentage: 90,
                    responsive: true,
                    title: {
                        display: true,
                        text: 'Gráfico de Pizza (Ligações)'
                    }
                }
            });
        }

        const totalCalls = data.reduce((a, b) => a + b, 0);
        document.getElementById('totalCalls').innerText = `${totalCalls} Ligações`;

    } catch (error) {
        console.error('Erro ao buscar dados: ', error);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAndUpdateChartCalls();
    document.getElementById('periodSelectCalls').addEventListener('change', fetchAndUpdateChartCalls);
});

// gerenciador de tarefas

function closeFormsOnClickOutside(event) {
    const taskForm = document.querySelector('.task-form');
    const editForm = document.querySelector('.edit-task-form');
    
    if (taskForm.style.display === 'block' && !taskForm.contains(event.target)) {
        taskForm.style.display = 'none';
    }
    if (editForm.style.display === 'block' && !editForm.contains(event.target)) {
        editForm.style.display = 'none';
    }
}

document.addEventListener('click', closeFormsOnClickOutside);

const addTaskButton = document.getElementById('add-task-btn');
const taskForm = document.querySelector('.task-form');

addTaskButton.addEventListener('click', () => {
    taskForm.style.display = 'block';
    event.stopPropagation();
});

function updateTaskStatus(taskId, status) {
    fetch(`/task/${taskId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: status })
    })
    .then(response => {
        if (!response.ok) {
            console.error('Error updating task status');
        }
    })
    .catch(error => console.error('Error:', error));
}

function allowDrop(event) {
    event.preventDefault();
}

function drag(event) {
    event.dataTransfer.setData("text", event.target.dataset.id);
}

function drop(event, columnId) {
    event.preventDefault();
    const taskId = event.dataTransfer.getData("text");
    const taskElement = document.querySelector(`.task[data-id='${taskId}']`);
    const oldColumnId = taskElement.dataset.status;
    const newStatus = columnId;
    if (oldColumnId !== newStatus) {
        taskElement.dataset.status = newStatus;
        const newTaskList = document.getElementById(newStatus);
        newTaskList.appendChild(taskElement);

        fetch(`/task/${taskId}/status?status=${newStatus}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update task status');
            }
        })
        .catch(error => console.error('Error updating task status:', error));
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const taskFormElement = document.getElementById('task-form');
    taskFormElement.addEventListener('submit', function (event) {
        event.preventDefault();

        const taskName = document.getElementById('task-name').value;
        const taskDescription = document.getElementById('task-description').value;
        const dueDate = document.getElementById('due-date').value;
        const userId = localStorage.getItem('userId');

        const taskData = {
            name: taskName,
            description: taskDescription,
            dueDate: dueDate,
            user: { idUser: userId },
            status: "todo-list"
        };


        fetch('/task', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(taskData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create task');
            }
            return response.json();
        })
        .then(data => {
            addTaskToList(data);
            taskForm.style.display = 'none';
        })
        .catch(error => console.error('Error:', error));
    });
});

function fetchTasks(userId) {
    fetch(`/task/all/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch tasks');
            }
            return response.json();
        })
        .then(data => {
            data.forEach(task => addTaskToList(task));
        })
        .catch(error => console.error('Error:', error));
}

function openEditForm(taskElement) {
    const editForm = document.querySelector('.edit-task-form');
    const taskId = taskElement.dataset.id;
    const taskName = taskElement.querySelector('b').innerText;
    const taskDescription = taskElement.querySelector('.task-description').innerText;
    const dueDate = taskElement.querySelector('.due-date').innerText;

    document.getElementById('edit-task-id').value = taskId;
    document.getElementById('edit-task-name').value = taskName;
    document.getElementById('edit-task-description').value = taskDescription;

    const [day, month, year] = dueDate.split('-');
    const formattedDueDate = `${year}-${month}-${day}`;
    document.getElementById('edit-due-date').value = formattedDueDate;

    editForm.style.display = 'block';
    event.stopPropagation();
}

function submitEditForm() {
    const taskId = document.getElementById('edit-task-id').value;
    const taskName = document.getElementById('edit-task-name').value;
    const taskDescription = document.getElementById('edit-task-description').value;
    const dueDate = document.getElementById('edit-due-date').value;
    const taskStatus = document.querySelector(`.task[data-id='${taskId}']`).dataset.status;
    const userId = localStorage.getItem('userId');

    const taskData = {
        idTask: taskId,
        name: taskName,
        description: taskDescription,
        dueDate: dueDate,
        status: taskStatus,
        idUser: { idUser: userId }
    };

    fetch(`/task/${taskId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(taskData)
    })
    .then(response => response.json())
    .then(data => {
        updateTaskInList(data);
        document.querySelector('.edit-task-form').style.display = 'none';
    })
    .catch(error => console.error('Error updating task:', error));
}

function updateTaskInList(task) {
    const taskElement = document.querySelector(`.task[data-id='${task.idTask}']`);
    if (taskElement) {
        taskElement.querySelector('b').innerText = task.name;
        taskElement.querySelector('.task-description').innerText = task.description;
        taskElement.querySelector('.due-date').innerText = formatDate(task.dueDate);
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    date.setUTCHours(0, 0, 0, 0);

    const day = String(date.getUTCDate()).padStart(2, '0');
    const month = String(date.getUTCMonth() + 1).padStart(2, '0');
    const year = date.getUTCFullYear();

    return `${day}-${month}-${year}`;
}

function addTaskToList(task) {
    const statusMap = {
        "todo-list": "todo-list",
        "inprogress-list": "inprogress-list",
        "done-list": "done-list"
    };

    const taskListId = statusMap[task.status];
    const taskList = document.getElementById(taskListId);

    if (!taskList) {
        console.error(`Task list element with ID '${taskListId}' not found`);
        return;
    }

    const taskElement = document.createElement("li");
    taskElement.className = "task";
    taskElement.draggable = true;
    taskElement.dataset.id = task.idTask;
    taskElement.dataset.status = task.status;

    const formattedDate = formatDate(task.dueDate);
    const taskHTML = "<b class='font-semibold text-lg'>" + task.name + "</b><br><span class='task-description'>" + task.description + "</span><br><span class='due-date'>" + formattedDate + "</span>";
    taskElement.innerHTML = taskHTML;

    taskElement.addEventListener("dragstart", drag);

    const editButton = document.createElement("button");
    editButton.className = "edit-button";
    editButton.innerText = "Editar";
    editButton.addEventListener("click", function () {
        openEditForm(taskElement);
    });

    const space = document.createElement("span");
    space.innerHTML = "&nbsp;";

    const deleteButton = document.createElement("button");
    deleteButton.innerText = "Excluir";
    deleteButton.addEventListener("click", function () {
        deleteTask(taskElement);
    });

    const taskButtons = document.createElement("div");
    taskButtons.className = "task-buttons";
    taskButtons.appendChild(editButton);
    taskButtons.appendChild(space);
    taskButtons.appendChild(deleteButton);

    taskElement.appendChild(taskButtons);

    taskList.appendChild(taskElement);
}

function deleteTask(taskElement) {
    const taskId = taskElement.dataset.id;
    fetch(`/task/${taskId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            taskElement.remove();
        } else {
            console.error('Error deleting task');
        }
    });
}


async function setLeadsByDayOfTheWeak() {
    await fetch('/lead/graph/leadsweek/' + localStorage.getItem('userId'), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
                console.log(data);
                data.dateLead = data.dateLead.map(dateString => {
                    const [day, month, year] = dateString.split('/');
                    return `${day}/${month}`;
                });

                var biggerValue = Math.max(...data.leadCount) +10;
                let barGrap = document.getElementById('itensRowBarGraph');
                let leadCounterBar = document.getElementById('leadCounterBar');

                leadCounterBar.innerHTML = `
                            <p>${biggerValue}</p>
                            <p>${(biggerValue/1.25).toFixed(1)}</p>
                            <p>${(biggerValue/2).toFixed(1)}</p>
                            <p>${(biggerValue/4).toFixed(1)}</p>
                            <p>0</p>
                            <p></p>
                            `;

                data.dateLead.forEach((item, index) => {
                    barGrap.innerHTML += ` <div class="w-[10%] h-full flex flex-col justify-between">
                                <div class="w-full h-full bg-[#DCDCDC] rounded-full flex flex-col justify-end">
                                    <div class="h-[${(data.leadCount[index]/biggerValue)*100}%] bg-[#3D0053] rounded-full flex flex-col justify-end">
                                        <p class="text-white mb-4 text-bold text-lg">${data.leadCount[index]}</p>
                                    </div>
                                </div>
                                <p class="w-full text-sm">${item}</p>
                            </div>`;
                });
            }
        )
}


async function setLeadsByDayOfTheMonth() {
    await fetch('/lead/graph/leadsmonth/'+localStorage.getItem('userId'), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            var ctx = document.getElementById('lineChart1').getContext('2d');

            var gradient = ctx.createLinearGradient(0, 0, 0, ctx.canvas.height);
            gradient.addColorStop(0, 'rgba(139, 0, 139, 0.5)');
            gradient.addColorStop(1, 'rgba(139, 0, 139, 0)');

            document.getElementById('crescimento-ligacoes').innerText = `${data.crescimentoPercentual.toFixed(2)}% em comparação ao mês anterior`;
            var dataLead = {
                labels: data.dateLead,
                datasets: [{
                    label: 'Total de ligações (Últimos 30 dias)',
                    data: data.leadCount,
                    backgroundColor: gradient,
                    borderWidth: 1,
                    borderColor: "purple",
                    fill: true
                }]
            };

            var barChart = new Chart(ctx, {
                type: 'line',
                data: dataLead,
                options: options
            });

        })
}


async function setProposalsByDayOfTheMonth() {
    await fetch('/proposal/graph/proposalsmonth/'+localStorage.getItem('userId'), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            var ctx = document.getElementById('lineChart2').getContext('2d');
            var gradient = ctx.createLinearGradient(0, 0, 0, ctx.canvas.height);
            gradient.addColorStop(0, 'rgba(139, 0, 139, 0.5)');
            gradient.addColorStop(1, 'rgba(139, 0, 139, 0)');

            document.getElementById('crescimento-propostas').innerText = `${data.crescimentoPercentualProposal.toFixed(2)}% em comparação ao mês anterior`;
            var dataLead = {
                labels: data.dateProposal,
                datasets: [{
                    label: 'Total de Propostas (Últimos 30 dias)',
                    data: data.proposalCount,
                    backgroundColor: gradient,
                    borderWidth: 1,
                    borderColor: "purple",
                    fill: true
                }]
            };

            var barChart = new Chart(ctx, {
                type: 'line',
                data: dataLead,
                options: options
            });

        })
}