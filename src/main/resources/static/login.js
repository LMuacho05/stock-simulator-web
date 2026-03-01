function fazerLogin() {
    const inputId = document.getElementById('user-id-input').value;

    if (!inputId) {
        alert("Por favor, insere um ID válido!");
        return;
    }

    localStorage.setItem('sim_user_id', inputId);

    window.location.href = 'index.html';
}

function registerAccount() {
    const username = document.getElementById('new-username-input').value;
    if (!username) {
        alert("Please insert a username.");
        return;
    }

    const userRegister = {
        username: username
    };

    fetch(`/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userRegister)
    })
        .then(response => response.json())
        .then(apiResponse => {
            if(apiResponse.success) {
                alert("✅ Account Created! Your access ID is: " + apiResponse.data);
                localStorage.setItem('sim_user_id', apiResponse.data);
                window.location.href = 'index.html';
            } else {
                alert("❌ Erro: " + apiResponse.message);
            }
        })
        .catch(error => console.error("Erro no registo:", error));
}