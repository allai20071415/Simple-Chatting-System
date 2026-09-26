<%@ page contentType="text/html;charset=UTF-8" isELIgnored="true" %>
<%
    String loggedInUser = (String) session.getAttribute("username");
    if (loggedInUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Simple Chat</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="chat-page">
    <header class="topbar">
        <div>
            <h1>Simple Chat</h1>
            <span>Logged in as <strong><%= loggedInUser %></strong></span>
        </div>
        <a class="logout" href="logout">Logout</a>
    </header>

    <section class="chat-shell">
        <div class="toolbar">
            <label for="receiver">Chat with</label>
            <select id="receiver">
                <option value="">Select a user</option>
            </select>
        </div>

        <div id="chatBox" class="chat-box">
            <div class="empty">Select a user to view messages.</div>
        </div>

        <form id="messageForm" class="message-form">
            <input id="message" type="text" maxlength="500" placeholder="Type your message..." autocomplete="off">
            <button id="sendButton" type="submit">Send</button>
        </form>
    </section>
</div>

<script>
const receiverSelect = document.getElementById('receiver');
const chatBox = document.getElementById('chatBox');
const messageInput = document.getElementById('message');
const messageForm = document.getElementById('messageForm');
let timer = null;

function redirectIfSessionExpired(response) {
    if (response.status !== 401) return false;
    window.location.replace('login.jsp?error=session');
    return true;
}

function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = value;
    return div.innerHTML;
}

async function loadUsers() {
    const response = await fetch('users', {cache: 'no-store'});
    if (redirectIfSessionExpired(response)) return;
    if (!response.ok) throw new Error('Could not load users');
    const users = await response.json();
    receiverSelect.innerHTML = '<option value="">Select a user</option>';
    users.forEach(user => {
        const option = document.createElement('option');
        option.value = user;
        option.textContent = user;
        receiverSelect.appendChild(option);
    });
}

async function loadMessages() {
    const receiver = receiverSelect.value;
    if (!receiver) {
        chatBox.innerHTML = '<div class="empty">Select a user to view messages.</div>';
        return;
    }

    const response = await fetch('messages?receiver=' + encodeURIComponent(receiver), {cache: 'no-store'});
    if (redirectIfSessionExpired(response)) return;
    if (!response.ok) throw new Error('Could not load messages');
    const messages = await response.json();

    if (messages.length === 0) {
        chatBox.innerHTML = '<div class="empty">No messages yet. Start the conversation.</div>';
        return;
    }

    chatBox.innerHTML = messages.map(item => `
        <div class="message ${item.sender === '<%= loggedInUser %>' ? 'mine' : 'theirs'}">
            <div class="message-meta">
                <span><strong>${escapeHtml(item.sender)}</strong> → ${escapeHtml(item.receiver)}</span>
                <span>${escapeHtml(item.time)}</span>
            </div>
            <div class="message-text">${escapeHtml(item.message)}</div>
        </div>`).join('');
    chatBox.scrollTop = chatBox.scrollHeight;
}

messageForm.addEventListener('submit', async event => {
    event.preventDefault();
    const receiver = receiverSelect.value;
    const message = messageInput.value.trim();

    if (!receiver) {
        alert('Select a user first.');
        return;
    }
    if (!message) return;

    const body = new URLSearchParams({receiver, message});
    const response = await fetch('sendMessage', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        body
    });

    if (redirectIfSessionExpired(response)) return;
    if (!response.ok) {
        alert(`Message could not be sent (HTTP ${response.status}).`);
        return;
    }

    messageInput.value = '';
    await loadMessages();
    messageInput.focus();
});

receiverSelect.addEventListener('change', () => {
    loadMessages().catch(console.error);
});

loadUsers()
    .then(() => loadMessages())
    .catch(error => {
        console.error(error);
        chatBox.innerHTML = '<div class="error">Could not connect to the chat database.</div>';
    });

timer = setInterval(() => loadMessages().catch(console.error), 2000);
</script>
</body>
</html>
