/**
 * Created by EliasBrattli on 02/05/2017.
 */
var ws = new WebSocket("ws://localhost:2345");
ws.onopen = function (event) {
    //ws.send("abcdef");
};
ws.onmessage = function (event) {
    console.log(event.data);
}

function sendMessage() {
    ws.send("close");
    ws.send("abcdef");
}
function closeButtonFunction() {
    ws.close();
}

document.close(function () {
    ws.close();
})