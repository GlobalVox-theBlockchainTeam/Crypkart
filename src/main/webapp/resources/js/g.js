function Copy_Address() {

    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val(document.getElementById("current_address").value).select();
    document.execCommand("copy");
    $temp.remove();
    $.alert("Wallet address copied to clip board");
}

$(".btcrate-change, #advertisementType").on('click focusout', function () {
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var data = {};
    var headers = {};
    data[csrfParameter] = csrfToken;
    headers[csrfHeader] = csrfToken;
    var currencyCode = $("#currency").find(':selected').data('code');
    data["code"] = currencyCode;
    $('#basic-addon1').html(currencyCode);
    $('#basic-addon2').html(currencyCode);


    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/ajax/currencyrate",
        data: JSON.stringify(data),
        dataType: 'json',
        timeout: 100000,
        headers: headers,
        success: function (data) {
            var json = $.parseJSON(JSON.stringify(data));
            if (json.statusCode == "error")
                $("#current_btc_rate").html(json.statusMessage);
            else {
                var finalrate;
                var tradeType = $("#advertisementType").val();
                if (tradeType == "SELL") {
                    finalrate = parseFloat(json.rate) + parseFloat((json.rate / 100) * $("#margin").val());
                } else {
                    finalrate = parseFloat(json.rate) - parseFloat((json.rate / 100) * $("#margin").val());
                }

                $("#btc").val($.number(finalrate, 2, '.'));
                $("#current_btc_rate").html("Current Indicative Rate in " + currencyCode + " : " + $.number(json.rate, 2));
                $("#maxLimit").val($.number(json.maxRate, 0, '.'));
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);

        },
        done: function (e) {
            console.log("DONE");
        }
    });
});


function confirmCurrencyRate(id, currencyCode, rate, min, max, url) {
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var data = {};
    var headers = {};
    data[csrfParameter] = csrfToken;
    headers[csrfHeader] = csrfToken;
    data["code"] = currencyCode;
    data["minRate"] = min;
    data["maxRate"] = max;
    data["rate"] = rate;
    $('#basic-addon1').html(currencyCode);
    $('#basic-addon2').html(currencyCode);


    waitingDialog.show('Wait...', {dialogSize: 'sm', progressType: 'primary'});
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/ajax/currencyrate",
        data: JSON.stringify(data),
        dataType: 'json',
        timeout: 100000,
        headers: headers,
        success: function (data) {
            waitingDialog.hide();
            var json = $.parseJSON(JSON.stringify(data));
            if (json.statusCode == "error") {
                $('#inline_edit_id' + id).submit();
            }
            else {
                var finalrate = parseFloat(json.rate) + parseFloat((json.rate / 100));
                var plusRate = parseFloat(parseFloat(finalrate * 20) / 100);
                var yourRate = rate.replace(/,/g, '');
                if (yourRate > (finalrate + plusRate) || yourRate < (finalrate - plusRate)) {

                    var text = (yourRate - finalrate >= 0) ? "Your rate is plus " + $.number(yourRate - finalrate, 2, '.') + " which is more then 20%" : "Your rate is minus " + $.number(finalrate - yourRate, 2, '.') + " which is less then 20%";
                    $.confirm({
                        title: 'Confirm',
                        icon: 'fa fa-warning btn-danger btn',
                        content: 'Current approximate rate in ' + currencyCode + ' [ ' + $.number(finalrate, 2, '.') + ' ].<br/>' + text + '<br/>Are you sure ?',
                        animationBounce: 2.5,
                        buttons: {
                            confirm: {
                                btnClass: 'btn-blue',
                                action: function () {
                                    //$('#inline_edit_id' + id).submit();
                                    var form = $(document.createElement('form'));
                                    $(form).attr("action", url);
                                    $(form).attr("method", "POST");
                                    var input = $("<input>")
                                        .attr("type", "hidden")
                                        .attr("name", "rate")
                                        .val(rate);
                                    var input1 = $("<input>")
                                        .attr("type", "hidden")
                                        .attr("name", "minRate")
                                        .val(min);

                                    var input2 = $("<input>")
                                        .attr("type", "hidden")
                                        .attr("name", "maxRate")
                                        .val(max);

                                    var input3 = $("<input>")
                                        .attr("type", "hidden")
                                        .attr("name", "type")
                                        .val(id);
                                    var input4 = $("<input>")
                                        .attr("type", "hidden")
                                        .attr("name", "_csrf")
                                        .val(csrfToken);


                                    $(form).append($(input));
                                    $(form).append($(input1));
                                    $(form).append($(input2));
                                    $(form).append($(input3));
                                    $(form).append($(input4));


                                    form.appendTo(document.body);
                                    form.submit();

                                }
                            },
                            cancel: {
                                btnClass: 'btn-default',
                                action: function () {

                                }
                            }
                        }
                    });
                } else {
                    var form = $(document.createElement('form'));
                    $(form).attr("action", url);
                    $(form).attr("method", "POST");
                    var input = $("<input>")
                        .attr("type", "hidden")
                        .attr("name", "rate")
                        .val(rate);
                    var input1 = $("<input>")
                        .attr("type", "hidden")
                        .attr("name", "minRate")
                        .val(min);

                    var input2 = $("<input>")
                        .attr("type", "hidden")
                        .attr("name", "maxRate")
                        .val(max);

                    var input3 = $("<input>")
                        .attr("type", "hidden")
                        .attr("name", "type")
                        .val(id);
                    var input4 = $("<input>")
                        .attr("type", "hidden")
                        .attr("name", "_csrf")
                        .val(csrfToken);


                    $(form).append($(input));
                    $(form).append($(input1));
                    $(form).append($(input2));
                    $(form).append($(input3));
                    $(form).append($(input4));


                    form.appendTo(document.body);
                    form.submit();

                }
            }

        },
        error: function (e) {
            waitingDialog.hide();
            console.log("ERROR: ", e);
            var form = $(document.createElement('form'));
            $(form).attr("action", url);
            $(form).attr("method", "POST");
            var input = $("<input>")
                .attr("type", "hidden")
                .attr("name", "rate")
                .val(rate);
            var input1 = $("<input>")
                .attr("type", "hidden")
                .attr("name", "minRate")
                .val(min);

            var input2 = $("<input>")
                .attr("type", "hidden")
                .attr("name", "maxRate")
                .val(max);

            var input3 = $("<input>")
                .attr("type", "hidden")
                .attr("name", "type")
                .val(id);
            var input4 = $("<input>")
                .attr("type", "hidden")
                .attr("name", "_csrf")
                .val(csrfToken);


            $(form).append($(input));
            $(form).append($(input1));
            $(form).append($(input2));
            $(form).append($(input3));
            $(form).append($(input4));


            form.appendTo(document.body);
            form.submit();

        },
        done: function (e) {
            waitingDialog.hide();
            console.log("DONE");
        }
    });
}


$('#advertise_form').on('keyup keypress', function (e) {
    var keyCode = e.keyCode || e.which;
    if (keyCode === 13) {
        e.preventDefault();
        return false;
    }
});


$(".edit-inline-rate").on("click", function (e) {
    var advertiseId = $(this).data("id");
    var advertiseMin = $(this).data("min");
    var advertiseMax = $(this).data("max");
    var advertiseRate = $(this).data("rate");
    var advertiseUrl = $(this).data("url");
    var advertiseCode = $(this).data("code");

    $.confirm({
        title: 'Update Advertise Rates!',
        icon: 'fa fa-pencil btn-success btn',
        content: '' +
        '<form action="" class="formName">' +
        '<div class="form-group">' +
        '<label>BTC Rate</label>' +
        '<input type="text" placeholder="BTC Rate" class="edit-inline-btcrate form-control" value="' + advertiseRate + '" required  /><br/>' +
        '<label>Min Limit</label>' +
        '<input type="text" placeholder="Minimum Amount" class="edit-inline-minimum form-control" value="' + advertiseMin + '" required /><br/>' +
        '<label>Max Limit</label>' +
        '<input type="text" placeholder="Maximum Amount" class="edit-inline-maximum form-control" value="' + advertiseMax + '" required /><br/>' +
        '</div>' +
        '</form>',
        buttons: {
            formSubmit: {
                text: 'Submit',
                btnClass: 'btn-blue',
                action: function () {
                    var rate = this.$content.find('.edit-inline-btcrate').val();
                    var min = this.$content.find('.edit-inline-minimum').val();
                    var max = this.$content.find('.edit-inline-maximum').val();
                    if (!rate || !min || !max) {
                        $.alert('Please provide valid amounts');
                        return false;
                    }
                    else {
                        confirmCurrencyRate(advertiseId, advertiseCode, rate, min, max, advertiseUrl)
                    }
                }
            },
            cancel: function () {
                //close
            },
        },
        onContentReady: function () {
            // bind to events
            var jc = this;
            this.$content.find('form').on('submit', function (e) {
                // if the user submits the form by pressing enter in the field.
                e.preventDefault();
                jc.$$formSubmit.trigger('click'); // reference the button and click it
            });
        }
    });
});

$(function () {
    $('#file').change(function (e) {
        if (this.files[0].size < 104857600) {
            $("#progress").show();
            $('#uploadfilebutton').trigger("click");
            $("#alertMsg").addClass("label-info");
            $("#alertMsg").removeClass("label-danger");

        } else {
            $("#progress").hide();
            $("#alertMsg").removeClass("label-info");
            $("#alertMsg").addClass("label-danger");
            $("#alertMsg").html("Max allowed size : 3Mb your file size : " + formatBytes(this.files[0].size, 2));
        }
    })
});


function formatBytes(a, b) {
    if (0 == a) return "0 Bytes";
    var c = 1024, d = b || 2, e = ["Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
        f = Math.floor(Math.log(a) / Math.log(c));
    return parseFloat((a / Math.pow(c, f)).toFixed(d)) + " " + e[f]
}


function findByAdvertiser(id) {
    $("#" + id).toggle();
}

function loading() {
    $("#loading").show();
}

function loadingExit() {
    $("#loading").hide();
}

function getZones(countryCode) {
    waitingDialog.show("Loading Time Zones", {dialogSize: 'sm'});
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var data = {};
    var headers = {};
    data[csrfParameter] = csrfToken;
    headers[csrfHeader] = csrfToken;
    data["countryCode"] = $("#" + countryCode).find(':selected').data('code');


    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/beforeloginajax/zones",
        data: JSON.stringify(data),
        dataType: 'json',
        timeout: 100000,
        headers: headers,
        success: function (data) {
            $("#ajax-zones").html("");
            var json = $.parseJSON(JSON.stringify(data));
            console.log(json.zones);
            var combo = $("<select></select>").attr("id", "zone").attr("name", "zone").attr("class", "form-control");
            $.each(json.zones, function (key, value) {
                combo.append("<option value='" + value.id + "'>" + value.name + "</option>");
            });
            $("#ajax-zones").append(combo);
            $("#zones-div").slideDown();
            waitingDialog.hide();

        },
        error: function (e) {
            console.log("ERROR: ", e);
            waitingDialog.hide();
        },
        done: function (e) {
            console.log("DONE");
            waitingDialog.hide();
        }
    });
    getCurrencies(countryCode);
}

function getCountries(countryCode) {
    if ($("#countries-loaded").val() == 0) {
        waitingDialog.show("Loading Time Zones", {dialogSize: 'sm'});
        var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        var data = {};
        var headers = {};
        data[csrfParameter] = csrfToken;
        headers[csrfHeader] = csrfToken;
        data["countryCode"] = $("#" + countryCode).find(':selected').data('code');


        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/beforeloginajax/countries",
            data: JSON.stringify(data),
            dataType: 'json',
            timeout: 100000,
            headers: headers,
            success: function (data) {
                var json = $.parseJSON(JSON.stringify(data));
                console.log(json.countries);
                //$("#" + countryCode + " option[value='0']").remove();
                var combo = $("#" + countryCode);
                $.each(json.countries, function (key, value) {
                    combo.append("<option data-code='" + value.code + "' value='" + value.id + "'>" + value.name + "</option>");
                });
                /*$("#ajax-zones").append(combo);
                $("#zones-div").slideDown();*/
                waitingDialog.hide();

            },
            error: function (e) {
                console.log("ERROR: ", e);
                waitingDialog.hide();
            },
            done: function (e) {
                console.log("DONE");
                waitingDialog.hide();
            }
        });
        $("#countries-loaded").val(1);
    }
    //getCurrencies(countryCode);
}

function getCurrencies(countryCode) {
    waitingDialog.show("Loading Time Zones", {dialogSize: 'sm'});
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var data = {};
    var headers = {};
    data[csrfParameter] = csrfToken;
    headers[csrfHeader] = csrfToken;
    data["countryCode"] = $("#" + countryCode).find(':selected').data('code');


    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/beforeloginajax/currencies",
        data: JSON.stringify(data),
        dataType: 'json',
        timeout: 100000,
        headers: headers,
        success: function (data) {
            $("#ajax-currencies").html("");
            var json = $.parseJSON(JSON.stringify(data));
            console.log(json.currencies);
            var combo = $("<select></select>").attr("id", "Currency").attr("name", "Currency").attr("class", "form-control");
            $.each(json.currencies, function (key, value) {
                combo.append("<option value='" + value.id + "'>" + value.currencyCode + "</option>");
            });
            $("#ajax-currencies").append(combo);
            $("#currencies-div").slideDown();
            waitingDialog.hide();

        },
        error: function (e) {
            console.log("ERROR: ", e);
            waitingDialog.hide();
        },
        done: function (e) {
            console.log("DONE");
            waitingDialog.hide();
        }
    });
}


$("body a").on("click", function (e) {
    if (!e.shiftKey && !e.ctrlKey && !e.metaKey && !$(this).hasClass("no-loading")) {
        waitingDialog.show('Loading...', {dialogSize: 'sm', progressType: 'primary'});
    } else if ($(this).hasClass("dropdown-toggle")) {
        $(this).next("ul").slideToggle();
    }
});

function duplicateUser(data, headers, ele, url) {
    /*waitingDialog.show("Checking ", {dialogSize: 'sm'});*/
    $("#duplicate_" + ele + "1").slideUp();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/beforeloginajax/" + url,
        data: JSON.stringify(data),
        dataType: 'json',
        timeout: 100000,
        headers: headers,
        success: function (data) {
            var json = $.parseJSON(JSON.stringify(data));
            if (json.success == true) {
                $("#duplicate_" + ele).removeClass("label-danger");
                $("#duplicate_" + ele).addClass("label label-success");
            } else {
                $("#duplicate_" + ele).addClass("label label-danger");
                $("#duplicate_" + ele).removeClass("label-success");
            }
            $("#duplicate_" + ele).html(json.msg);
            /*waitingDialog.hide();*/
            $("#duplicate_" + ele).slideDown();

        },
        error: function (e) {
            console.log("ERROR: ", e);
            /*waitingDialog.hide();*/
        },
        done: function (e) {
            console.log("DONE");
            /*waitingDialog.hide();*/
        }
    });
}


$(".duplicate").on("blur", function (e) {
    var type = $(this).data("type");
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var data = {};
    var headers = {};
    data[csrfParameter] = csrfToken;
    headers[csrfHeader] = csrfToken;

    if (type == "email") {
        data['email'] = $(this).val();
        if ($(this).val() != "")
            duplicateUser(data, headers, "email", "duplicateemail");
        else {
            $("#duplicate_email").html("Enter valid email").slideDown("slow");
            $("#duplicate_email").addClass("label label-danger");
            $("#duplicate_email").removeClass("label-success");
        }
    } else if (type == "username") {
        data['username'] = $(this).val();
        if ($(this).val() != "")
            duplicateUser(data, headers, "username", "duplicateuser");
        else {
            $("#duplicate_username").html("Enter valid username").slideDown("slow");
            $("#duplicate_username").addClass("label label-danger");
            $("#duplicate_username").removeClass("label-success");
        }

    }
});


$(document).ready(function () {
    getCountries("countries");
    $('#password').keyup(function () {
        $('#result').html(checkStrength($('#password').val()))
    })

    function checkStrength(password) {
        var strength = 0
        if (password.length < 6) {
            $('#result').removeClass()
            $('#result').addClass('short')
            return 'Too short'
        }
        if (password.length > 7) strength += 1
// If password contains both lower and uppercase characters, increase strength value.
        if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/)) strength += 1
// If it has numbers and characters, increase strength value.
        if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)) strength += 1
// If it has one special character, increase strength value.
        if (password.match(/([!,%,&,@,#,$,^,*,?,_,~])/)) strength += 1
// If it has two special characters, increase strength value.
        if (password.match(/(.*[!,%,&,@,#,$,^,*,?,_,~].*[!,%,&,@,#,$,^,*,?,_,~])/)) strength += 1
// Calculated strength value, we can return messages
// If value is less than 2
        if (strength < 2) {
            $('#result').removeClass()
            $('#result').addClass('weak')
            return 'Weak'
        } else if (strength == 2) {
            $('#result').removeClass()
            $('#result').addClass('good')
            return 'Good'
        } else {
            $('#result').removeClass()
            $('#result').addClass('strong')
            return 'Strong'
        }
    }


    $("input[type='text']").attr("autocomplete", "off");
});


$(".sub-user-edit").on("click", function (e) {
    var id = $(this).data("id");
    var email = $(this).data("email");
    var name = $(this).data("name");
    var username = $(this).data("username");
    var url = $(this).data("url");
    var index = $(this).data("index");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var preid = $(this).data("preid");
    var enabled = ($(this).data("enabled")) ? "checked" : "";
    var dlg = $.confirm({
        title: 'Edit ' + name + '\'s Detail',
        icon: 'fa fa-pencil btn-success btn',
        content: '<form action="' + url + '" class="userEditForm" method="POST">'
        + '<label>Username</label>'
        + '<input type="text" class="form-control edit-username" name="username" placeholder="Username" value="' + username + '" />'
        + '<input type="hidden" class="form-control" name="_csrf"  value="' + csrfToken + '" /><br/>'
        + '<label>Name</label>'
        + '<input type="text" class="form-control edit-name" name="name" placeholder="Name" value="' + name + '" /><br/>'
        + '<label>Email</label>'
        + '<input type="text" class="form-control edit-email" name="email" placeholder="Email" value="' + email + '"/>'
        + '<input type="hidden" class="form-control" name="id" value="' + id + '"/><br/>'
        + '<label>Password</label>'
        + '<input type="password" class="form-control" name="password" /><br/>'
        + '<label>Enabled&nbsp;</label>'
        + '<input type="checkbox" class="" data-labelauty="Enable" name="enabled" value="true" ' + enabled + '/><br/>'
        + '</form>'
        ,
        animationBounce: 2.5,
        buttons: {
            formSubmit: {

                text: 'Submit',
                btnClass: 'btn-blue',
                action: function () {
                    var alt = $.dialog('<div style="width:100%;text-align: center;font-size:50px;"><i class="fa fa-spinner fa-spin"></i></div>', {closeIcon: ""});
                    var uName = this.$content.find('.edit-name').val();
                    var uEmail = this.$content.find('.edit-email').val();
                    var uUsername = this.$content.find('.edit-username').val();
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: this.$content.find('.userEditForm').serialize(), // serializes the form's elements.
                        success: function (data) {
                            alt.close();
                            var json = $.parseJSON(JSON.stringify(data));
                            $.alert(json.msg);
                            if (json.success) {
                                $("#" + preid + "name" + index).html(uName);
                                $("#" + preid + "username" + index).html(uUsername);
                                $("#" + preid + "email" + index).html(uEmail);
                                dlg.close();
                            }
                        },
                        error: function (data) {
                            alt.close();
                        }
                    });
                    return false;
                    //this.$content.find('.userEditForm').submit();
                }
            },
            cancel: {
                btnClass: 'btn-default',
                action: function () {

                }
            }
        }
    });
});

function getPostHeaders() {

    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;
    return headers;
}

$(".table-striped tr").on("click", function (e) {
    $(this).toggleClass("table-row-bg");
});

/*$(window).scroll(quickSearchHeader);
function quickSearchHeader(){
    var scroll = $(window).scrollTop();
    var temp;
    //console.log(scroll);
    if (scroll >= 50) {
        //$("#navbar-top-header-main").position().top
        temp =  $("#navbar-top-header-main").outerHeight(true);
        $("#top-quick-ad-search").css({'top': temp});
    }
}*/
/*

$( window ).scroll(function() {
    $("#top-quick-ad-search").top("80");
});*/


$(".ajax-div-paranet").on("click", ".page-link", function (e) {
    var formData = {};
    formData['pageNo'] = $(this).data('pageno');
    formData['maxCount'] = $(this).data('maxcount');
    formData['type'] = $(this).data('type');
    var divId = $(this).data('div');
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    formData[csrfParameter] = $("meta[name='_csrf']").attr("content");
    var url = $(this).data("url");
    $.ajax({
        type: "POST",
        url: url,
        headers: getPostHeaders(),
        data: formData,
        success: function (data) {
            $("#" + divId).html(data);
        },
        error: function (data) {
            alert(data);
            $("#" + divId).html(data.responseText);
        }
    });
});

function changeTradeStatus(tradeId) {
    var formData = {};
    formData['tradeId'] = tradeId;
    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    formData[csrfParameter] = $("meta[name='_csrf']").attr("content");
    $.ajax({
        type: "POST",
        url: "/ajax/getTradeStatusButtons/" + tradeId,
        headers: getPostHeaders(),
        data: formData,
        success: function (data) {
            $("#trade-status-buttons").html(data);
        },
        error: function (data) {
            $("#trade-status-buttons").html("Error loading data. Please <a onclick='window.location=window.location;'>refresh page.</a>");
        }
    });
}

$(".post-ad-status").on("change", function (e) {
    if (!$(this).is(':checked')) {
        $(".post-ad-status-child").prop("checked", false);
    }
});
$(".post-ad-status-child").on("change", function (e) {
    if ($(this).is(':checked')) {
        $(".post-ad-status").prop("checked", true);
    }
});

function getLocation() {

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);

    } else {
        // x.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function showPosition(position) {


    console.log(position)

    listCityData(position.coords.latitude,position.coords.longitude);
    // x.innerHTML = "Latitude: " + position.coords.latitude +
    //     "<br>Longitude: " + position.coords.longitude;
}

function listCityData(lat, lng)
{
    var latlng;
    latlng = new google.maps.LatLng(lat, lng);


    new google.maps.Geocoder().geocode({'latLng' : latlng}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                var country = null, countryCode = null, city = null, cityAlt = null;
                var c, lc, component;
                for (var r = 0, rl = results.length; r < rl; r += 1) {
                    var result = results[r];

                    if (!city && result.types[0] === 'locality') {
                        for (c = 0, lc = result.address_components.length; c < lc; c += 1) {
                            component = result.address_components[c];

                            if (component.types[0] === 'locality') {
                                city = component.long_name;
                                break;
                            }
                        }
                    }
                    else if (!city && !cityAlt && result.types[0] === 'administrative_area_level_1') {
                        for (c = 0, lc = result.address_components.length; c < lc; c += 1) {
                            component = result.address_components[c];

                            if (component.types[0] === 'administrative_area_level_1') {
                                cityAlt = component.long_name;
                                break;
                            }
                        }
                    } else if (!country && result.types[0] === 'country') {
                        country = result.address_components[0].long_name;
                        countryCode = result.address_components[0].short_name;
                    }

                    if (city && country) {
                        break;
                    }
                }

                console.log("City: " + city + ", City2: " + cityAlt + ", Country: " + country + ", Country Code: " + countryCode);

                var reload = 0;
                var locationCookie = getCookie('USER_LOCATION_COUNTRY_CODE');
                if (locationCookie == null){
                    reload = 1;
                }


                setCookie("USER_LOCATION_COUNTRY_CODE", countryCode);
                if (reload==1)
                    window.location=window.location;


                // document.getElementById('city').innerHTML = city;
                // document.getElementById('cityAlt').innerHTML = cityAlt;
                // document.getElementById('country').innerHTML = country;
                // document.getElementById('countryCode').innerHTML = countryCode;
                var country_option = $('#countries option[data-code = "'+countryCode+'"]').attr('value');
                $('#countries').val(country_option);
                $('#countries').trigger('change');
            }
        }
    });
}


$(".records-per-page").on("keyup", function (e) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code == 13) {
        $(this).next(".btn").click();
    }
});


$(".top-search-submit").on("click", function (e){

});


function setCookie(key, value) {
    var expires = new Date();
    expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
    document.cookie = key + '=' + value + ';expires=' + expires.toUTCString()+";path=/";

}

function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
}
