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
    });
}

function getJSON(localUrl) {
    return $.get('/parental/api/' + localUrl);
}


var serverAPI = {
    postJSON: postJSON,
    getJSON: getJSON
};