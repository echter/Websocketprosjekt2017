/**
 * Created by EliasBrattli on 02/05/2017.
 */
var exampleSocket = new WebSocket("ws://localhost:2345");
exampleSocket.onopen = function (event) {
    //exampleSocket.send("abcdef");
};
exampleSocket.onmessage = function (event) {
    console.log(event.data);
}

function sendMessage() {
    exampleSocket.send("close");
    exampleSocket.send("abcdef");
}
function closeButtonFunction() {
    exampleSocket.close();
}

document.close(function () {
    exampleSocket.close();
})