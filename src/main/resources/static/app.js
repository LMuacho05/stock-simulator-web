const USER_ID = localStorage.getItem('sim_user_id');

if (!USER_ID) {
    window.location.href = 'login.html';
}

document.addEventListener('DOMContentLoaded', () => {
    carregarSaldo();
    carregarPortfolio();
    loadHistory();
});

function carregarSaldo() {
    // GET to API
    fetch(`/balance/${USER_ID}`)
        .then(response => response.text())
        .then(saldo => {
            document.getElementById('balance-display').innerText = parseFloat(saldo).toFixed(2);
        })
        .catch(error => console.error("Erro ao carregar saldo:", error));
}

function carregarPortfolio() {
    fetch(`/portfolio/${USER_ID}`)
        .then(response => response.json())
        .then(apiResponse => {
            const tableBody = document.getElementById('portfolio-body');
            tableBody.innerHTML = '';

            if (apiResponse.success && apiResponse.data.length > 0) {
                apiResponse.data.forEach(item => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td><strong>${item.ticker}</strong></td>
                        <td>${item.totalQuantity}</td>
                    `;
                    tableBody.appendChild(tr);
                });
            } else {
                tableBody.innerHTML = `<tr><td colspan="2" style="text-align: center;">Não tens ações. Vai às compras!</td></tr>`;
            }
        })
        .catch(error => console.error("Erro ao carregar portfólio:", error));
}

function loadHistory() {
    fetch(`/history/${USER_ID}`)
        .then(response => response.json())
        .then(apiResponse => {
            const tableBody = document.getElementById('history-body');

            tableBody.innerHTML = '';

            if (apiResponse.success && apiResponse.data.length > 0) {

                apiResponse.data.forEach(item => {

                    let dataFormatada = new Date(item.timestamp).toLocaleString('pt-PT');

                    let precoFormatado = item.price.toFixed(2);

                    const tr = document.createElement('tr');

                    tr.innerHTML = `
                        <td>${dataFormatada}</td>
                        <td><strong>${item.type}</strong></td>
                        <td>${item.ticker}</td>
                        <td>${item.quantity}</td>
                        <td>$${precoFormatado}</td>
                    `;

                    tableBody.appendChild(tr);
                });

            } else {
                tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center;">Ainda não tens transações.</td></tr>`;
            }
        })
        .catch(error => console.error("Erro ao carregar histórico:", error));
}

function trade(action) {

    const ticker = document.getElementById('ticker-input').value.toUpperCase();
    const quantity = parseInt(document.getElementById('quantity-input').value);

    if (!ticker || isNaN(quantity) || quantity <= 0) {
        alert("Por favor, preenche um Ticker válido e uma quantidade maior que zero.");
        return;
    }

    const tradeRequest = {
        userId: USER_ID,
        ticker: ticker,
        quantity: quantity
    };

    // POST to server
    fetch(`/${action}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(tradeRequest)
    })
        .then(response => response.json())
        .then(apiResponse => {

            if (apiResponse.success) {
                alert("✅ " + apiResponse.message);

                carregarSaldo();
                carregarPortfolio();
                loadHistory();

                document.getElementById('ticker-input').value = '';
                document.getElementById('quantity-input').value = '';
            } else {
                alert("❌ Erro: " + apiResponse.message);
            }
        })
        .catch(error => {
            console.error("Erro na transação:", error);
            alert("Ocorreu um erro ao comunicar com o servidor.");
        });
}

function fazerLogout() {
    localStorage.removeItem('sim_user_id');// Delete memory
    window.location.href = 'login.html';    // Back to login page
}

function searchShare() {
    const ticker = document.getElementById('search-ticker').value.toUpperCase();
    const resultDiv = document.getElementById('search-result');

    if(!ticker) {
        alert("Please insert a valid ticker.");
    }

    resultDiv.style.display = 'block';
    resultDiv.innerHTML = "A consultar Wall Street por " + ticker + "... ⏳";

    fetch(`/price/${ticker}`)
        .then(response => response.json())
        .then(apiResponse => {
            if(apiResponse.success){
                const precoFormatado = apiResponse.data.toFixed(2);
                resultDiv.innerHTML = `✅ <strong>${ticker}</strong> is worth: <span style="color: #27ae60; font-size: 24px; font-weight: bold;">$${precoFormatado}</span>`;
                const buyInput = document.getElementById('ticker-input');
                if(buyInput) buyInput.value = ticker;
            } else {
                resultDiv.innerHTML = `❌ Ups! We didn't found share <strong>${ticker}</strong>.`;
            }
        })
        .catch(error => {
            console.error(error);
            resultDiv.innerHTML = "❌ Error to connect server.";
        })
}