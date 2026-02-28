function fazerLogin() {
    const inputId = document.getElementById('user-id-input').value;

    if (!inputId) {
        alert("Por favor, insere um ID válido!");
        return;
    }

    localStorage.setItem('sim_user_id', inputId);

    window.location.href = 'index.html';
}