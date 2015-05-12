function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function loginHandler(jqXHR, textStatus, errorThrown) {
    if( jqXHR.status === 401 ) {
        window.location = "login/?targetURL=" + encodeURIComponent(window.location);
    }
}

function postJSON(localUrl, object) {
    $("main").hide();
    $("footer").hide();
    $("body").prepend("<div class='progressBlock'></div>");

    return $.ajax({
      url : '/parental/api/' + localUrl,
      type : 'POST',
      data : JSON.stringify(object),
      contentType : "application/json; charset=utf-8"
    }).always(function() {
        $("main").show();
        $("footer").show();
        $(".progressBlock").remove();
    }).fail(loginHandler);
}

function putJSON(localUrl, id, object) {
    $("main").hide();
    $("footer").hide();
    $("body").prepend("<div class='progressBlock'></div>");

    return $.ajax({
      url : '/parental/api/' + localUrl + id,
      type : 'PUT',
      data : JSON.stringify(object),
      contentType : "application/json; charset=utf-8"
    }).always(function() {
        $("main").show();
        $("footer").show();
        $(".progressBlock").remove();
    }).fail(loginHandler);
}

function deleteJSON(localUrl, object) {
    $("main").hide();
    $("footer").hide();
    $("body").prepend("<div class='progressBlock'></div>");

    return $.ajax({
      url : '/parental/api/' + localUrl,
      type : 'DELETE',
      data : JSON.stringify(object),
      contentType : "application/json; charset=utf-8"
    }).always(function() {
        $("main").show();
        $("footer").show();
        $(".progressBlock").remove();
    }).fail(loginHandler);
}


function getJSON(localUrl) {
    return $.get('/parental/api/' + localUrl).fail(loginHandler);
}


var serverAPI = {
    postJSON: postJSON,
    putJSON: putJSON,
    deleteJSON: deleteJSON,
    getJSON: getJSON
};