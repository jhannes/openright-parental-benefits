function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function loginHandler(jqXHR) {
    if( jqXHR.status === 401 ) {
        window.location = "login/?targetURL=" + encodeURIComponent(window.location);
    }
}

function ajax(serverUrl) {
    function ajax(type, url, object) {
        $("main").hide();
        $("footer").hide();
        $("body").prepend("<div class='progressBlock'></div>");

        return $.ajax({
            url: url,
            type: type,
            data: JSON.stringify(object),
            contentType: "application/json; charset=utf-8"
        }).always(function () {
            $("main").show();
            $("footer").show();
            $(".progressBlock").remove();
        }).fail(loginHandler);
    }

    function postJSON(localUrl, object) {
        return ajax('POST', serverUrl + localUrl, object);
    }

    function putJSON(localUrl, id, object) {
        return ajax('PUT', serverUrl + localUrl + id, object);
    }

    function deleteJSON(localUrl, object) {
        return ajax('DELETE', serverUrl + localUrl, object);
    }

    function getJSON(localUrl) {
        return $.get(serverUrl + localUrl).fail(loginHandler);
    }

    return {
        postJSON: postJSON,
        putJSON: putJSON,
        deleteJSON: deleteJSON,
        getJSON: getJSON
    };
}


var serverAPI = ajax('/parental/api/');
