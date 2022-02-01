/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

var stompClient = null;
var selectedUsername = null;
var from = /*[[${user.username}]]*/ 'default';

document.addEventListener("DOMContentLoaded", function () {
    connect();
});

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility
        = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
}

function connect() {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/messages', function (messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });
        stompClient.subscribe('/topic/active', function (messageOutput) {
            updateUsers(JSON.parse(messageOutput.body));
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    var text = document.getElementById('text').value;
    stompClient.send("/app/chat", {},
        JSON.stringify({'from': from, 'text': text, 'recipient': selectedUsername}));
}

function showMessageOutput(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": "
        + messageOutput.message + " (" + messageOutput.time + ")"));
    response.appendChild(p);
}

function createTextNode(messageObj) {
    return '<div class= "row chat-message ' + (messageObj.myMsg ? ' my-message' : '') + '">' +
        '<div class="col-md-4">' +
        messageObj.from +
        '<br><small>' + messageObj.time + '</small>' +
        '</div>' +
        '<div class="col-md-8"><b>' +
        messageObj.message +
        '</b></div>' +
        '</div>';
}

function updateUsers(userList) {
    console.log('List of users : ' + userList);
    var activeUserUL = document.getElementById('active-users');

    var index;
    activeUserUL.innerHTML = '';
    if (userList.length == 0) {
        activeUserUL.innerHTML = '<p><i>No active users found.</i></p>';
        return;
    }
    activeUserUL.innerHTML = '<p class="text-muted">click on user to begin chat</p>';

    for (index = 0; index < userList.length; ++index) {
        if (userList[index] != from) {
            activeUserUL.innerHTML = activeUserUL.innerHTML + createUserNode(userList[index], index);
        }
    }
}

function createUserNode(username, index) {
    return '<li class="list-group-item">' +
        '<a class="active-user" href="javascript:void(0)" onclick="setSelectedUser(\'' + username + '\')">' + username + '</a>' +
        '</li>';
}

function setSelectedUser(username) {
    selectedUsername = username;
    document.getElementById('response').innerHTML = '';
}