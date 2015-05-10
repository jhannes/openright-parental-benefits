function postJSON(localUrl, object) {
    $("main").hide();
    $("footer").hide();
    $("body").prepend("<div class='progressBlock'></div>");

    return $.ajax({
      url : '/parental/api/' + localUrl,
      type : 'POST',
      data : JSON.stringify(object),
      async : false,
      contentType : "application/json; charset=utf-8"
    }).always(function() {
        $("main").show();
        $("footer").show();
        $(".progressBlock").remove();
    });
}


var serverAPI = {
    postJSON: postJSON
};