/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

$(document).ready(function () {
    $('a').tooltip();
});

function calculate_amount(value) {
    var btc_price = $("#btc_price").val();
    var sum = value * btc_price;
    var data = sum.toFixed(2);

    if ($.isNumeric(value)) {
        $("#amount").val(data);
        $("#btcAmount").removeClass("alert-danger");
        $("#btcamount-error").addClass("display-none");
        if (parseFloat($("#min").val()) > parseFloat(data) || parseFloat($("#max").val()) < parseFloat(data)) {
            $("#amount").addClass("alert-danger");
            $("#amount-error").html("Minimum allowed limit : " + $.number($("#min").val(), 2) + "<br/>Max allowed limit : " + $.number($("#max").val(), 2));
            $("#amount-error").removeClass("display-none");
        } else {
            $("#amount").removeClass("alert-danger");
            $("#amount-error").addClass("display-none");
        }
    }else{
        $("#btcAmount").addClass("alert-danger");
        $("#btcamount-error").html("Invalid value").removeClass("display-none");

    }

}

function calculate_btc_amount(value) {
    var btc_price = $("#btc_price").val();
    var sum = value / btc_price;
    var data = sum.toFixed(6);

    if (!$.isNumeric(value)) {

    }else {

        if (parseFloat($("#min").val()) > parseFloat(value) || parseFloat($("#max").val()) < parseFloat(value)) {
            $("#amount").addClass("alert-danger");
            $("#amount-error").html("Minimum allowed limit : " + $.number($("#min").val(), 2) + "<br/>Max allowed limit : " + $.number($("#max").val(), 2));
            $("#amount-error").removeClass("display-none");
        } else {
            $("#amount").removeClass("alert-danger");
            $(".amount-error").addClass("display-none");
        }
    }



    $("#btcAmount").val(data);
}



function btc_notification(message, link) {
    var url = $("#url").val();
    $.notify({
        message: message,
        url: link,
        target: "_self"
    });
    $.playSound(url + 'assets/alerts/alert');
}

function btc_check_notifications(uid) {
    var url = $("#url").val();
    var data_url = url + "requests/btc_check_notifications.php?uid=" + uid;
    $.ajax({
        type: "GET",
        url: data_url,
        dataType: "JSON",
        success: function (data) {
            if (data.status == "success") {
                btc_notification(data.msg, data.link);
            }
        }
    });
}

function btc_calculate_price() {
    var url = $("#url").val();
    var amount = $("#btc_amount").val();
    var currency = $("#btc_currency").val();
    var data_url = url + "requests/btc_calculate_price.php?amount=" + amount + "&currency=" + currency;
    $.ajax({
        type: "GET",
        url: data_url,
        dataType: "html",
        success: function (data) {
            $("#your_btc_price").html(data);
        }
    });
}

function btc_post_trade_message(trade_id) {
    var url = $("#url").val();
    var data_url = url + "requests/btc_post_trade_message.php?trade_id=" + trade_id;
    $.ajax({
        type: "POST",
        url: data_url,
        data: $("#trade_chat_form_" + trade_id).serialize(),
        dataType: "json",
        success: function (data) {
            if (data.status == "success") {
                $("#trade_chat_form_" + trade_id)[0].reset();
                var messages = $("#trade_chat_" + trade_id).html();
                var nmessages = data.msg + messages;
                $("#trade_chat_" + trade_id).html(nmessages);
            } else {
                // error adding trade message
            }
        }
    });
}


function btc_check_new_messages(uid, trade_id) {
    var url = $("#url").val();
    var data_url = url + "requests/btc_check_new_messages.php?uid=" + uid + "&trade_id=" + trade_id;
    $.ajax({
        type: "GET",
        url: data_url,
        dataType: "JSON",
        success: function (data) {
            if (data.status == "success") {
                var messages = $("#trade_chat_" + trade_id).html();

                var nmessages = data.msg + messages;
                $("#trade_chat_" + trade_id).html(nmessages);
            } else {

            }
        }
    });
}

function btc_check_new_file_messages(uid, trade_id) {
    var url = $("#url").val();
    var data_url = url + "requests/btc_check_new_file_messages.php?uid=" + uid + "&trade_id=" + trade_id;
    $.ajax({
        type: "GET",
        url: data_url,
        dataType: "JSON",
        success: function (data) {
            if (data.status == "success") {
                var messages = $("#trade_chat_" + trade_id).html();

                var nmessages = data.msg + messages;
                $("#trade_chat_" + trade_id).html(nmessages);
            } else {

            }
        }
    });
}

function btc_check_trade_status(trade_id) {
    var url = $("#url").val();
    var data_url = url + "requests/btc_check_trade_status.php?trade_id=" + trade_id;
    $.ajax({
        type: "GET",
        url: data_url,
        dataType: "JSON",
        success: function (data) {
            if (data.status == "success") {
                if (data.st == "2") {
                    $("#btc_cancel_trade").remove();
                }
                $("#trade_status_" + trade_id).html(data.msg);
            } else {

            }
        }
    });
}